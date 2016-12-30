import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by Sascha on 20/12/2016.
 */
public class FileReceiverTest {
    //****************************************************
    // File format used: "<CRC32> <Hi!> <size> <filename>"
    //                    <4Byte> <xBy> <xBy>   <xBytes>


    private final DatagramSocket udpSocket;

    private byte[] receiveBuffer = new byte[1500];
    private byte[] receivedBytes;
    private byte[] dataReceived;
    private int totalBytesReceived;
    private DatagramPacket receivedPacket;
    private DatagramPacket packetToSent;
    private Checksum checksum = new CRC32();
    private String filename;
    private int sizeOfFile;
    private FileOutputStream fileOutputStream;
    private boolean seqNumberIsZero = true;
    private boolean receiveTimeout = false;
    private boolean noRepeatAck;


    protected InetAddress ipFromSender;


    // current state of the FSM
    private State currentState;

    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    // all states for this FSM
    enum State {
        WAIT_FOR_HI, WAIT_FOR_FIRST_PACKET, WAIT_FOR_SEQ_ZERO, WAIT_FOR_SEQ_ONE, FINISH
    }

    // all messages/conditions which can occur
    enum Msg {
        GET_HI, SEND_RESPONSE_HI, GET_SEQ_ZERO, GET_SEQ_ONE, SEND_ACK_ZERO, SEND_ACK_ONE
    }

    /**
     * constructor
     */
    public FileReceiverTest(DatagramSocket udpSocket) throws SocketException {
        this.udpSocket = udpSocket;

        // Set current state
        currentState = State.WAIT_FOR_HI;

        // define all valid state transitions for our state machine
        // (undefined transitions will be ignored)
        transition = new Transition[State.values().length][Msg.values().length];
        transition[State.WAIT_FOR_HI.ordinal()][Msg.GET_HI.ordinal()] = new waitForHi();
        transition[State.WAIT_FOR_FIRST_PACKET.ordinal()][Msg.SEND_RESPONSE_HI.ordinal()] = new respondWithHi();
        transition[State.WAIT_FOR_FIRST_PACKET.ordinal()][Msg.GET_SEQ_ZERO.ordinal()] = new getSeqZero();
        transition[State.WAIT_FOR_SEQ_ONE.ordinal()][Msg.SEND_ACK_ZERO.ordinal()] = new sendAckZero();
        transition[State.WAIT_FOR_SEQ_ONE.ordinal()][Msg.GET_SEQ_ONE.ordinal()] = new getSeqOne();
        transition[State.WAIT_FOR_SEQ_ZERO.ordinal()][Msg.SEND_ACK_ONE.ordinal()] = new sendAckOne();
        transition[State.WAIT_FOR_SEQ_ZERO.ordinal()][Msg.GET_SEQ_ZERO.ordinal()] = new getSeqZero();
        transition[State.FINISH.ordinal()][Msg.GET_HI.ordinal()] = new restartReceiver();

        System.out.println("INFO FSM constructed, current state: " + currentState);
    }

    /**
     * Process a message (a condition has occurred).
     *
     * @param input Message or condition that has occurred.
     */
    public void
    processMsg(Msg input) throws IOException {
        System.out.println("INFO Received " + input + " in state " + currentState);
        Transition trans = transition[currentState.ordinal()][input.ordinal()];
        if (trans != null) {
            currentState = trans.execute(input);
        }
        System.out.println("INFO New State: " + currentState);
    }

    /**
     * Abstract base class for all transitions.
     * Derived classes need to override execute thereby defining the action
     * to be performed whenever this transition occurs.
     */
    abstract class Transition {
        abstract public State execute(Msg input) throws IOException;
    }

    class waitForHi extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            udpSocket.receive(receivedPacket);
            receivedBytes = Arrays.copyOfRange(receiveBuffer, 0, receivedPacket.getLength());

            // check for bit error
            if (!crc32Check(receivedBytes)) {
                System.out.println("ERROR - CRC32 check failed");
                return State.WAIT_FOR_HI;
            }

            // check for valid HI-Message with file size and file name
            dataReceived = Arrays.copyOfRange(receivedBytes, 4, receivedBytes.length);
            String message = new String(dataReceived);
            String[] messageParts = message.split(" ");

            boolean messageIsValid = false;
            if (messageParts.length == 3) {
                if (messageParts[0].equals("Hi!")) {
                    sizeOfFile = Integer.parseInt(messageParts[1]);
                    filename = messageParts[2];
                    ipFromSender = receivedPacket.getAddress();
                    messageIsValid = true;
                }
            }

            if (!messageIsValid) {
                System.out.println("ERROR - Hi-Message has wrong format");
                return State.WAIT_FOR_HI;
            }

            System.out.println("filesize: " + sizeOfFile + " filename: " + filename);

            // create file
            File file = new File("new_" + filename);
            if (file.exists()) {
                file.delete();
            }
            //fileOutputStream = new FileOutputStream(file);
            return State.WAIT_FOR_FIRST_PACKET;
        }
    }

    class respondWithHi extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            packetToSent = new DatagramPacket(dataReceived, dataReceived.length, ipFromSender, 8888);
            System.out.println(new String(packetToSent.getData()));
            udpSocket.send(receivedPacket);
            udpSocket.setSoTimeout(3_000);
            return State.WAIT_FOR_FIRST_PACKET;
        }
    }

    class getSeqZero extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            noRepeatAck = false;
            receiveBuffer = new byte[1500];
            receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                udpSocket.receive(receivedPacket);
            } catch (SocketTimeoutException ex) {
                return State.FINISH;
            }

            // Check sender ip address
            if (!ipFromSender.equals(receivedPacket.getAddress())) {
                System.out.println("ERROR - Packet from different host");
                noRepeatAck = true;
                return currentState;
            }

            receivedBytes = Arrays.copyOfRange(receiveBuffer, 0, receivedPacket.getLength());
            // check for bit error
            if (!crc32Check(receivedBytes)) {
                System.out.println("ERROR - CRC32 check failed");
                noRepeatAck = true;
                return currentState;
            }

            if (receivedBytes[4] != 0) {
                System.out.println("ERROR - Got wrong Seq-Number: " + 1);
                return currentState;
            }
            fileOutputStream.write(receivedBytes, 5, receivedBytes.length - 5);
            fileOutputStream.flush();
            totalBytesReceived += receivedBytes.length - 5;
            return State.WAIT_FOR_SEQ_ONE;
        }
    }

    class getSeqOne extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            noRepeatAck = false;
            receiveBuffer = new byte[1500];
            receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                udpSocket.receive(receivedPacket);
            } catch (SocketTimeoutException ex) {
                return State.FINISH;
            }
            // Check sender ip address
            if (!ipFromSender.equals(receivedPacket.getAddress())) {
                System.out.println("ERROR - Packet from different host");
                noRepeatAck = true;
                return currentState;
            }

            receivedBytes = Arrays.copyOfRange(receiveBuffer, 0, receivedPacket.getLength());
            // check for bit error
            if (!crc32Check(receivedBytes)) {
                System.out.println("ERROR - CRC32 check failed");
                noRepeatAck = true;
                return currentState;
            }

            if (receivedBytes[4] != 1) {
                System.out.println("ERROR - Got wrong Seq-Number: " + 0);
                return currentState;
            }

            fileOutputStream.write(receivedBytes, 5, receivedBytes.length - 5);
            fileOutputStream.flush();
            totalBytesReceived += receivedBytes.length - 5;
            return State.WAIT_FOR_SEQ_ZERO;
        }
    }

    class sendAckZero extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            byte[] zeroByteArray = new byte[]{(byte) 0};

            byte[] data = ByteBuffer.allocate(5).put(getCRC32InBytes(zeroByteArray)).put((byte) 0).array();

            packetToSent = new DatagramPacket(data, data.length, ipFromSender, 8888);
            udpSocket.send(packetToSent);
            return State.WAIT_FOR_SEQ_ONE;
        }
    }

    class sendAckOne extends Transition {
        @Override
        public State execute(Msg input) throws IOException {

            byte[] oneByteArray = new byte[]{(byte) 1};
            byte[] data = ByteBuffer.allocate(5).put(getCRC32InBytes(oneByteArray)).put((byte) 1).array();

            packetToSent = new DatagramPacket(data, data.length, ipFromSender, 8888);
            udpSocket.send(packetToSent);
            return State.WAIT_FOR_SEQ_ZERO;
        }
    }

    class restartReceiver extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Restarting receiver...");
            udpSocket.setSoTimeout(0);
            return State.WAIT_FOR_HI;
        }
    }

    /*
     * MAIN
     */
    public static void main(String[] ignored) throws IOException {
        try (DatagramSocket udpSocket = new DatagramSocket(7777)) {
            FileReceiverTest fileReceiver = new FileReceiverTest(udpSocket);

            while (true) {
                fileReceiver.totalBytesReceived = 0;
                while (fileReceiver.currentState == State.WAIT_FOR_HI)
                    fileReceiver.processMsg(Msg.GET_HI);

                try (FileOutputStream fileOutputStream = new FileOutputStream(new File("new_"+fileReceiver.filename))){
                    fileReceiver.fileOutputStream = fileOutputStream;


                    while (fileReceiver.currentState == State.WAIT_FOR_FIRST_PACKET) {
                        fileReceiver.processMsg(Msg.SEND_RESPONSE_HI);
                        fileReceiver.processMsg(Msg.GET_SEQ_ZERO);
                    }

                    while (fileReceiver.currentState != State.FINISH) {
                        while (fileReceiver.currentState == State.WAIT_FOR_SEQ_ONE) {
                            if (!fileReceiver.noRepeatAck) {
                                fileReceiver.processMsg(Msg.SEND_ACK_ZERO);
                            }
                            fileReceiver.processMsg(Msg.GET_SEQ_ONE);
                        }

                        while (fileReceiver.currentState == State.WAIT_FOR_SEQ_ZERO) {
                            if (!fileReceiver.noRepeatAck) {
                                fileReceiver.processMsg(Msg.SEND_ACK_ONE);
                            }
                            fileReceiver.processMsg(Msg.GET_SEQ_ZERO);
                        }
                    }
                }

                System.out.println("expected size of file: " + fileReceiver.sizeOfFile);
                System.out.println("actual size of file: " + fileReceiver.totalBytesReceived);

                if (fileReceiver.sizeOfFile == fileReceiver.totalBytesReceived) {
                    System.out.println("File: " + fileReceiver.filename + " received successfully!");
                } else {
                    System.out.println("Receiver timed out prematurely. " + "File " + fileReceiver.filename + " was not received correctly.");
                }
                fileReceiver.processMsg(Msg.GET_HI);
            }
        }
    }

    private boolean crc32Check(byte[] data) {
        byte[] receivedChecksumBytes = Arrays.copyOfRange(data, 0, 4);
        byte[] receivedData = Arrays.copyOfRange(data, 4, data.length);
        checksum.update(receivedData, 0, receivedData.length);
        int checksumOfData = (int) checksum.getValue();
        int checksumOfPacket = ByteBuffer.wrap(receivedChecksumBytes).getInt();
        checksum.reset();

        return checksumOfPacket == checksumOfData;
    }

    private byte[] getCRC32InBytes(byte[] data) {
        checksum.update(data, 0, data.length);
        int crc32 = (int) checksum.getValue();
        checksum.reset();
        return ByteBuffer.allocate(4).putInt(crc32).array();
    }
}
