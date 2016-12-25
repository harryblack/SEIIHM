package ssiemens.ss16.netzwerke.uebung7_filetransfer;

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
public class FileReceiver {
    //****************************************************
    // File format used: "<CRC32> <Hi!> <size> <filename>"
    //                    <4Byte> <xBy> <xBy>   <xBytes>

    private static int port = 7777;
    protected final DatagramSocket socket = new DatagramSocket(port);
    protected boolean socketTimeout = false;
    protected static DatagramPacket packetToSend;
    protected static DatagramPacket packetReceived;
    protected static DatagramPacket packetAck0;
    protected static DatagramPacket packetAck1;
    protected static Checksum checksumAck0 = new CRC32();
    protected static Checksum checksumAck1 = new CRC32();
    protected final byte[] ACKBytes0;
    protected final byte[] ACKBytes1;
    protected byte[] receivedData;

    String infoString = new String();
    String totalSize = new String();
    String fileName = new String();
    File outputFile;
    FileOutputStream fileOutputStream;

    byte[] packetAsBytes = new byte[1405];

    protected int currentPacketLength;
    protected InetAddress currentSenderAddress;
    protected int currentSenderPort;


    // current state of the FSM
    private State currentState;

    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    // all states for this FSM
    enum State {
        WAITHI,
        WAITDATA,
        WAIT0,
        WAIT1
    }

    // all messages/conditions which can occur
    enum Msg {
        GOTHI,
        GOTOTHERDATA,
        GOTSEQ0,
        GOTSEQ1
    }

    /**
     * constructor
     */
    public FileReceiver() throws SocketException {
        // Set current state
        currentState = State.WAITHI;

        // define all valid state transitions for our state machine
        // (undefined transitions will be ignored)
        transition = new Transition[State.values().length][Msg.values().length];

        transition[State.WAITHI.ordinal()][Msg.GOTHI.ordinal()] = new sendHiAfterReceivedHi();
        //transition[State.WAITHI.ordinal()][Msg.GOTOTHERDATA.ordinal()] = new noActionAfterReceivedData();
        transition[State.WAITDATA.ordinal()][Msg.GOTHI.ordinal()] = new sendHiAfterReceivedHi();
        transition[State.WAITDATA.ordinal()][Msg.GOTSEQ0.ordinal()] = new ack0AfterReceivedFirstData();

        transition[State.WAIT1.ordinal()][Msg.GOTSEQ0.ordinal()] = new sendAck1AfterReceived0();
        transition[State.WAIT1.ordinal()][Msg.GOTSEQ1.ordinal()] = new sendAck1AfterReceived1();
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ0.ordinal()] = new sendAck0AfterReceived0();
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ1.ordinal()] = new sendAck0AfterReceived1();

        ACKBytes0 = new byte[]{0};
        ACKBytes1 = new byte[]{1};
        checksumAck0.update(ACKBytes0, 0, ACKBytes0.length);
        checksumAck1.update(ACKBytes1, 0, ACKBytes1.length);

        byte[] checkSumAck0AsByteArray = ByteBuffer.allocate(4).putInt((int) checksumAck0.getValue()).array();
        byte[] checkSumAck1AsByteArray = ByteBuffer.allocate(4).putInt((int) checksumAck1.getValue()).array();

        byte[] ACK0WithChecksum = ByteBuffer.allocate(4 + ACKBytes0.length).put(checkSumAck0AsByteArray).put(ACKBytes0).array();
        byte[] ACK1WithChecksum = ByteBuffer.allocate(4 + ACKBytes1.length).put(checkSumAck1AsByteArray).put(ACKBytes1).array();

        packetAck0 = new DatagramPacket(ACK0WithChecksum, ACK0WithChecksum.length, currentSenderAddress, port);
        packetAck1 = new DatagramPacket(ACK1WithChecksum, ACK1WithChecksum.length, currentSenderAddress, port);

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

    class sendHiAfterReceivedHi extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Received Hi!");
            currentSenderAddress = packetReceived.getAddress();
            currentSenderPort = packetReceived.getPort();

            receivedData = Arrays.copyOfRange(packetReceived.getData(), 0, currentPacketLength);
            infoString = new String(Arrays.copyOfRange(receivedData, 4, receivedData.length));
            System.out.println(infoString);
            String[] infoParts = infoString.split("\\s+");
            totalSize = infoParts[1];
            fileName = infoParts[2];
            outputFile = new File(fileName);
            fileOutputStream = new FileOutputStream(fileName, true);

            packetToSend = new DatagramPacket(receivedData, receivedData.length, currentSenderAddress, 7776);
            socket.send(packetToSend);
            return State.WAITDATA;
        }
    }

    class ack0AfterReceivedFirstData extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Sending Ack0...");

            receivedData = Arrays.copyOfRange(packetReceived.getData(), 6, currentPacketLength - 6);
            infoString = new String(receivedData);
            String[] infoParts = infoString.split("s");
            fileOutputStream.write(infoParts[infoParts.length - 1].getBytes());
            fileOutputStream.flush();

            socket.send(packetAck0);
            return State.WAIT1;
        }
    }


    class sendAck0AfterReceived0 extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Sending Ack0...");

            //Folgender Code gleich wie vorher ???
            receivedData = Arrays.copyOfRange(packetReceived.getData(), 6, currentPacketLength - 6);
            infoString = new String(receivedData);
            String[] infoParts = infoString.split("s");
            fileOutputStream.write(infoParts[infoParts.length - 1].getBytes());
            fileOutputStream.flush();

            socket.send(packetAck0);
            return State.WAIT1;
        }
    }

    class sendAck0AfterReceived1 extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Sending Ack0...");

            // Folgender Code gleich wie vorher ???
            receivedData = Arrays.copyOfRange(packetReceived.getData(), 6, currentPacketLength - 6);
            infoString = new String(receivedData);
            String[] infoParts = infoString.split("s");
            fileOutputStream.write(infoParts[infoParts.length - 1].getBytes());
            fileOutputStream.flush();

            socket.send(packetAck0);
            return State.WAIT0;
        }
    }

    class sendAck1AfterReceived0 extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Sending Ack1...");

            receivedData = Arrays.copyOfRange(packetReceived.getData(), 6, currentPacketLength - 6);
            infoString = new String(receivedData);
            String[] infoParts = infoString.split("s");
            fileOutputStream.write(infoParts[infoParts.length - 1].getBytes());
            fileOutputStream.flush();

            socket.send(packetAck1);
            return State.WAIT1;
        }
    }

    class sendAck1AfterReceived1 extends Transition {
        @Override
        public State execute(Msg input) throws IOException {
            System.out.println("INFO Sending Ack1...");

            receivedData = Arrays.copyOfRange(packetReceived.getData(), 6, currentPacketLength - 6);
            infoString = new String(receivedData);
            String[] infoParts = infoString.split("s");
            fileOutputStream.write(infoParts[infoParts.length - 1].getBytes());
            fileOutputStream.flush();

            socket.send(packetAck1);
            return State.WAIT0;
        }
    }
}
