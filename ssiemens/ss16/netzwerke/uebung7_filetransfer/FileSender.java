package ssiemens.ss16.netzwerke.uebung7_filetransfer;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.tools.javac.code.Attribute;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Class which models the state machine itself.
 */
public class FileSender {
    // Final object variables
    private final DatagramSocket udpSocket;
    private final String targetHost;
    private final int targetPort = 7777;
    private final int sizeOfFile;
    private final FileInputStream fileInputStream;
    private final String filename;
    private final Checksum checksum;
    private final String hiMessage;

    // Object variables
    private byte[] bytesToSend;
    private byte[] bytesReceived;
    private DatagramPacket packetToSend;
    private DatagramPacket packetReceived;
    private int sendHiRetryCounter;
    private long rttStart;
    private long rttStop;
    private long rtt;
    private long timeout;
    private boolean sequenzNumberIsZero;
    private byte[] copyBytesToSend;


    // all states for this FSM
    private enum State {
        IDLE, WAIT_FOR_HI, SERVER_UNREACHABLE, WAIT_FOR_SEND_SEQ_ZERO, WAIT_FOR_SEND_SEQ_ONE, WAIT_FOR_ACK_ZERO, WAIT_FOR_ACK_ONE, FINISH
    }


    // all messages/conditions which can occur
    private enum Msg {
        SEND_HI,
        WAIT_FOR_HI,
        SEND_SEQ,
        WAIT_FOR_ACK,
        RETRANSMIT
    }

    // current state of the FSM
    private State currentState;
    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    /**
     * constructor
     */
    public FileSender(DatagramSocket udpSocket, FileInputStream fileInputStream, int sizeOfFile, String filename, String targetHost) throws SocketException {
        this.udpSocket = udpSocket;
        this.fileInputStream = fileInputStream;
        this.sizeOfFile = sizeOfFile;
        this.filename = filename;
        this.checksum = new CRC32();
        this.targetHost = targetHost;
        this.bytesReceived = new byte[1500];
        this.packetReceived = new DatagramPacket(bytesReceived, bytesReceived.length);
        this.hiMessage = "Hi! " + sizeOfFile + " " + filename;
        this.sequenzNumberIsZero = true;

        // define all valid state transitions for our state machine
        // (undefined transitions will be ignored)
        currentState = State.IDLE;
        transition = new Transition[State.values().length][Msg.values().length];
        transition[State.IDLE.ordinal()][Msg.SEND_HI.ordinal()] = new SendHi();
        transition[State.WAIT_FOR_HI.ordinal()][Msg.WAIT_FOR_HI.ordinal()] = new WaitForHiResponse();
        transition[State.WAIT_FOR_HI.ordinal()][Msg.SEND_HI.ordinal()] = new SendHi();

        transition[State.WAIT_FOR_SEND_SEQ_ZERO.ordinal()][Msg.SEND_SEQ.ordinal()] = new SendSeq();
        transition[State.WAIT_FOR_ACK_ZERO.ordinal()][Msg.WAIT_FOR_ACK.ordinal()] = new WaitForAck();
        transition[State.WAIT_FOR_ACK_ZERO.ordinal()][Msg.RETRANSMIT.ordinal()] = new Retransmit();

        transition[State.WAIT_FOR_SEND_SEQ_ONE.ordinal()][Msg.SEND_SEQ.ordinal()] = new SendSeq();
        transition[State.WAIT_FOR_ACK_ONE.ordinal()][Msg.WAIT_FOR_ACK.ordinal()] = new WaitForAck();
        transition[State.WAIT_FOR_ACK_ONE.ordinal()][Msg.RETRANSMIT.ordinal()] = new Retransmit();
        System.out.println("INFO FSM constructed, current state: " + currentState);
    }

    /**
     * Process a message (a condition has occurred).
     *
     * @param input Message or condition that has occurred.
     */
    public void processMsg(Msg input) throws IOException {
        System.out.println("INFO Received " + input + " in state " + currentState);
        Transition trans = transition[currentState.ordinal()][input.ordinal()];
        if (trans != null) {
            currentState = trans.execute(input);
        }
        System.out.println("INFO State: " + currentState);
    }

    /**
     * Abstract base class for all transitions.
     * Derived classes need to override execute thereby defining the action
     * to be performed whenever this transition occurs.
     */
    abstract class Transition {
        abstract State execute(Msg input) throws IOException;
    }

    private class SendHi extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Send Hi!");
            final byte[] message = hiMessage.getBytes();
            final byte[] calculatedCRC = getCRC32InBytes(message);
            bytesToSend = ByteBuffer.allocate(calculatedCRC.length + message.length).put(calculatedCRC).put(message).array();
            packetToSend = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getByName(targetHost), targetPort);
            rttStart = System.currentTimeMillis();
            udpSocket.send(packetToSend);
            return State.WAIT_FOR_HI;
        }
    }

    private class WaitForHiResponse extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Wait for Hi-Response!");

            udpSocket.setSoTimeout(3_000);
            try {
                udpSocket.receive(packetReceived);
                rttStop = System.currentTimeMillis();
            } catch (SocketTimeoutException ex) {
                System.out.println("TIMEOUT-HI");
                sendHiRetryCounter++;
                if (sendHiRetryCounter < 5) {
                    return State.WAIT_FOR_HI;
                } else {
                    return State.SERVER_UNREACHABLE;
                }
            }
            bytesReceived = Arrays.copyOfRange(packetReceived.getData(), 0, packetReceived.getLength());
            final boolean packetIsValid = crc32Check(bytesReceived);
            if (!packetIsValid) return State.WAIT_FOR_HI;
            final boolean responseIsValid = Arrays.equals(hiMessage.getBytes(), Arrays.copyOfRange(bytesReceived, 4, bytesReceived.length));
            if (!responseIsValid) return State.WAIT_FOR_HI;
            bytesToSend = new byte[1405];
            rtt = rttStop - rttStart;
            timeout = rtt * 2;
            udpSocket.setSoTimeout((int) timeout);

            return State.WAIT_FOR_SEND_SEQ_ZERO;
        }
    }

    private class SendSeq extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Send sequence number: " + (sequenzNumberIsZero ? 0 : 1));

            int sequenceNumber = sequenzNumberIsZero ? 0 : 1;

            bytesToSend = new byte[500];
            int numberOfBytesRead = fileInputStream.read(bytesToSend);
            if (numberOfBytesRead == -1) return State.FINISH; // end of file reached
            byte[] dataToSend = ByteBuffer.allocate(1 + numberOfBytesRead).put((byte) sequenceNumber).put(Arrays.copyOfRange(bytesToSend, 0, numberOfBytesRead)).array();
            byte[] crc32 = getCRC32InBytes(dataToSend);

            bytesToSend = ByteBuffer.allocate(crc32.length + dataToSend.length).put(crc32).put(dataToSend).array();
            copyBytesToSend = Arrays.copyOf(bytesToSend, bytesToSend.length);

            System.out.println(bytesToSend.length);
            packetToSend = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getByName(targetHost), targetPort);
            rttStart = System.currentTimeMillis();
            udpSocket.send(packetToSend);
            return sequenzNumberIsZero ? State.WAIT_FOR_ACK_ZERO : State.WAIT_FOR_ACK_ONE;
        }
    }

    private class WaitForAck extends Transition {
        @Override
        public State execute(Msg input) {

            int sequenceNumber = sequenzNumberIsZero ? 0 : 1;

            System.out.println("INFO Wait for ACK: " + (sequenzNumberIsZero ? 0 : 1));
            timeout = (long) (0.875 * timeout + 0.125 * rtt);
            System.out.println("Timeout: " +timeout);
            try {
                udpSocket.setSoTimeout((int) timeout + 3);        // In the unlike event of a rtt of 0 (localhost) there will be 3 ms added additionally
            } catch (SocketException e) {
                System.out.println("SocketException");
            }
            try {
                udpSocket.receive(packetReceived);
                rttStop = System.currentTimeMillis();
            } catch (SocketTimeoutException ex) {
                System.out.println("TIMEOUT");
                timeout = timeout * 2;
                return currentState;
            } catch (IOException e) {
                System.out.println("IOException");
            }
            bytesReceived = Arrays.copyOfRange(packetReceived.getData(), 0, packetReceived.getLength());
            System.out.println("Length bytesReceived: "+ bytesReceived.length);
            final boolean packetIsValid = crc32Check(bytesReceived);
            if (!packetIsValid) {
                System.out.println("CRC32-ERROR");
                return currentState;
            }

            final boolean ackHasZero = (int) bytesReceived[4] == sequenceNumber;
            if (!ackHasZero) {
                System.out.println("WRONG ACK-NR - Got: " + bytesReceived[4] + " Length: " + bytesReceived.length);
                return currentState;
            }
            rtt = rttStop - rttStart;
            sequenzNumberIsZero = !sequenzNumberIsZero;
            return sequenzNumberIsZero ? State.WAIT_FOR_SEND_SEQ_ZERO : State.WAIT_FOR_SEND_SEQ_ONE;
        }
    }

    private class Retransmit extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("Retransmit Seq: " + (sequenzNumberIsZero ? 0 : 1));
            System.out.println(Arrays.toString(copyBytesToSend));

            packetToSend = new DatagramPacket(copyBytesToSend, copyBytesToSend.length, InetAddress.getByName(targetHost), targetPort);
            rttStart = System.currentTimeMillis();
            udpSocket.send(packetToSend);
            return sequenzNumberIsZero ? State.WAIT_FOR_ACK_ZERO : State.WAIT_FOR_ACK_ONE;
        }
    }


    /**
     * Main programm for the file sender
     *
     * @param args Expects two parameters. First one is the filname to copy. Second one is the target host where to copy the file.
     */
    public static void main(String[] args) throws IOException {
        // check argument length
        if (args.length != 2) {
            throw new IllegalArgumentException("Need two parameters: java FileSender <filename> <ip-address or dns-hostname> .");
        }
        // parse arguments
        final String fileName = args[0];
        final String targetHost = args[1];

        // create file-object
        final Path fileToCopy = Paths.get(fileName);

        // check if file exists
        if (!Files.exists(fileToCopy))
            throw new IllegalArgumentException("File \"" + fileToCopy.getFileName() + "\" not found!");

        // get size of file (number in bytes)
        final int sizeOfFile = (int) Files.size(fileToCopy);
        if (sizeOfFile == 0) throw new IllegalArgumentException("File has zero bytes!");
        System.out.println("Transfer \"" + fileName + "\" with " + sizeOfFile + " bytes.");

        // create byte-stream of file-to-copy
        FileInputStream fileInputStream = new FileInputStream(fileName);

        // create UDP-Socket
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            // create FileSender
            FileSender fileSender = new FileSender(udpSocket, fileInputStream, sizeOfFile, fileName, targetHost);


            // START COMMUNICATION WITH TARGET HOST
            fileSender.processMsg(Msg.SEND_HI);
            fileSender.processMsg(Msg.WAIT_FOR_HI);

            while (fileSender.currentState == State.WAIT_FOR_HI) {
                fileSender.processMsg(Msg.SEND_HI);
                fileSender.processMsg(Msg.WAIT_FOR_HI);
            }

            if (fileSender.currentState == State.SERVER_UNREACHABLE) {
                System.out.println("RETRY TIMEOUT");
                return;
            }

            // START FILE TRANSFER
            while (fileSender.currentState != State.FINISH) {
                fileSender.processMsg(Msg.SEND_SEQ);
                State stateBefore = fileSender.currentState;
                fileSender.processMsg(Msg.WAIT_FOR_ACK);
                while (fileSender.currentState != State.FINISH && fileSender.currentState == stateBefore) {
                    fileSender.processMsg(Msg.RETRANSMIT);
                    fileSender.processMsg(Msg.WAIT_FOR_ACK);
                }
            }

        }
        System.out.println("FileSender ended - Goodbye!");
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