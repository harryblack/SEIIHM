package ssiemens.ss16.netzwerke.uebung7_filetransfer;

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
    private final DatagramSocket udpSocket = new DatagramSocket(7777);
    private final String targetHost;
    private final int targetPort = 7777;
    private final int sizeOfFile;
    private final FileInputStream fileInputStream;
    private final String filename;
    private final Checksum checksum;

    // Object variables
    byte[] bytesToSend;
    byte[] bytesReceived;
    DatagramPacket packetToSend;
    DatagramPacket packetReceived;


    // all states for this FSM
    private enum State {
        IDLE, WAIT_FOR_HI, SERVER_UNREACHABLE, WAIT_FOR_SEND_SEQ_ZERO, WAIT_FOR_SEND_SEQ_ONE, WAIT_FOR_ACK_ZERO, WAIT_FOR_ACK_ONE, RETRY_TIMEOUT
    }


    // all messages/conditions which can occur
    private enum Msg {
        SEND_HI,
        GET_HI,
        SEND_SEQ_ZERO,
        SEND_SEQ_ONE,
        GOT_ACK_ZERO,
        GOT_ACK_ONE,
        TIMED_OUT_SEQ_ZERO,
        TIMED_OUT_SEQ_ONE
    }

    // current state of the FSM
    private State currentState;
    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    /**
     * constructor
     */
    public FileSender(FileInputStream fileInputStream, int sizeOfFile, String filename, String targetHost) throws SocketException {
        this.fileInputStream = fileInputStream;
        this.sizeOfFile = sizeOfFile;
        this.filename = filename;
        this.checksum = new CRC32();
        this.targetHost = targetHost;
        this.bytesReceived = new byte[1400];
        this.packetReceived = new DatagramPacket(bytesReceived,bytesReceived.length);

        // define all valid state transitions for our state machine
        // (undefined transitions will be ignored)
        currentState = State.IDLE;
        transition = new Transition[State.values().length][Msg.values().length];
        transition[State.IDLE.ordinal()][Msg.SEND_HI.ordinal()] = new SendHi();
        transition[State.WAIT_FOR_HI.ordinal()][Msg.GET_HI.ordinal()] = new GetHi();
        transition[State.WAIT_FOR_SEND_SEQ_ZERO.ordinal()][Msg.SEND_SEQ_ZERO.ordinal()] = new SendSeqZero();
        transition[State.WAIT_FOR_ACK_ZERO.ordinal()][Msg.TIMED_OUT_SEQ_ZERO.ordinal()] = new RetransmitSeqZero();
        transition[State.WAIT_FOR_ACK_ZERO.ordinal()][Msg.GOT_ACK_ZERO.ordinal()] = new StopTimerSeqZero();
        transition[State.WAIT_FOR_SEND_SEQ_ONE.ordinal()][Msg.SEND_SEQ_ONE.ordinal()] = new SendSeqOne();
        transition[State.WAIT_FOR_ACK_ONE.ordinal()][Msg.GOT_ACK_ONE.ordinal()] = new StopTimerSeqOne();
        transition[State.WAIT_FOR_ACK_ONE.ordinal()][Msg.TIMED_OUT_SEQ_ONE.ordinal()] = new RetransmitSeqOne();
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
        abstract public State execute(Msg input) throws IOException;
    }

    private class SendHi extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Send Hi!");

            final byte[] message = (" Hi! " + sizeOfFile + " " + filename).getBytes();
            checksum.update(message, 0, message.length);
            byte[] calculatedCRC = ByteBuffer.allocate(8).putLong(checksum.getValue()).array();
            System.out.println(Arrays.toString(calculatedCRC));
            bytesToSend = ByteBuffer.allocate(8 + message.length).put(calculatedCRC).put(message).array();
            System.out.println(Arrays.toString(bytesToSend));
            packetToSend = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getByName(targetHost), targetPort);
            udpSocket.send(packetToSend);

            return State.WAIT_FOR_HI;
        }
    }

    private class GetHi extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Receive Hi!");
            udpSocket.setSoTimeout(3_000);
            try {
                udpSocket.receive(packetReceived);
            } catch (SocketTimeoutException ex) {
                return State.WAIT_FOR_HI;
            }
            System.out.println("Length: " + packetReceived.getLength());
            bytesReceived = packetReceived.getData();
            byte[] receivedChecksumBytes = Arrays.copyOfRange(bytesReceived,0,8);
            byte[] receivedData = Arrays.copyOfRange(bytesReceived,8,bytesReceived.length);
            System.out.println(Arrays.toString(receivedChecksumBytes));
            System.out.println(Arrays.toString(receivedData));



            return State.WAIT_FOR_SEND_SEQ_ZERO;
        }
    }

    private class SendSeqZero extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Send bytes!");
            return State.WAIT_FOR_ACK_ZERO;
        }
    }

    private class RetransmitSeqZero extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("RetransmitSeqZero!");
            return State.WAIT_FOR_ACK_ZERO;
        }
    }

    private class StopTimerSeqZero extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("StopTimerSeqZero!");
            return State.WAIT_FOR_SEND_SEQ_ONE;
        }
    }

    private class SendSeqOne extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("SendSeqOne!");
            return State.WAIT_FOR_ACK_ONE;
        }
    }

    private class StopTimerSeqOne extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("StopTimerSeqOne!");
            return State.WAIT_FOR_SEND_SEQ_ZERO;
        }
    }

    private class RetransmitSeqOne extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("RetransmitSeqOne!");
            return State.WAIT_FOR_ACK_ONE;
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
        if (!Files.exists(fileToCopy)) {
            throw new IllegalArgumentException("File \"" + fileToCopy.getFileName() + "\" not found!");
        }

        // get size of file (number in bytes)
        final int sizeOfFile = (int) Files.size(fileToCopy);

        // create byte-stream of file-to-copy
        FileInputStream fileInputStream = new FileInputStream(fileName);

        // create FileSender
        FileSender fileSender = new FileSender(fileInputStream, sizeOfFile, fileName, targetHost);

        fileSender.processMsg(Msg.SEND_HI);
        fileSender.processMsg(Msg.GET_HI);
        // while (fileSender.currentState == State.WAIT_FOR_HI){
        //     fileSender.processMsg(Msg.SEND_HI);
        //     fileSender.processMsg(Msg.GET_HI);
        // }

        if (fileSender.currentState == State.RETRY_TIMEOUT) return;


        //Checksum checksum = new CRC32();
        //checksum.update(pack[4, ende]);
        //long toCheck = checksum.getValue();
        //toCheck == pack[0,3]
        System.out.println("FileSender ended - Goodbye!");
    }
}