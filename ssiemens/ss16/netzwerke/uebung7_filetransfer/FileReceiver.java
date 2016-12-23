package ssiemens.ss16.netzwerke.uebung7_filetransfer;

import java.io.IOException;
import java.net.*;

/**
 * Created by Sascha on 20/12/2016.
 */
public class FileReceiver {
    //****************************************************
    // File format used: "<CRC32> <Hi!> <size> <filename>"
    //                    <4Byte> <xBy> <xBy>   <xBytes>

    protected final DatagramSocket socket = new DatagramSocket(7777);
    protected static DatagramPacket packetToSend;
    protected static DatagramPacket packetReceived;
    protected final byte[] ACKBytes = new byte[1];
    protected byte[] greetingBytes = null;
    byte[] packetAsBytes = new byte[1400];

    protected InetAddress currentConnectionSender;


    // current state of the FSM
    private State currentState;

    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    // all states for this FSM
    enum State {
        WAITHI, WAITDATA, WAIT0, WAIT1
    }

    // all messages/conditions which can occur
    enum Msg {
        GOTHI, GOTDATA, GOTSEQ0, GOTSEQ1
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
        //transition[State.WAITHI.ordinal()][Msg.GOTDATA.ordinal()] = new noActionAfterReceivedData();
        transition[State.WAITDATA.ordinal()][Msg.GOTHI.ordinal()] = new sendHiAfterReceivedHi();
        transition[State.WAITDATA.ordinal()][Msg.GOTSEQ0.ordinal()] = new ack0AfterReceivedFirstData();

        transition[State.WAIT1.ordinal()][Msg.GOTSEQ0.ordinal()] = new sendAck1AfterReceived0();
        transition[State.WAIT1.ordinal()][Msg.GOTSEQ1.ordinal()] = new sendAck1AfterReceived1();
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ0.ordinal()] = new sendAck0AfterReceived0();
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ1.ordinal()] = new sendAck0AfterReceived1();


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
            packetToSend.setData(packetReceived.getData());
            packetToSend.setAddress(packetReceived.getAddress());
            packetToSend.setPort(7777);
            socket.send(packetToSend);
            return State.WAITDATA;
        }
    }

    class noActionAfterReceivedData extends Transition {
        @Override
        public State execute(Msg input) throws UnknownHostException {

            return State.WAITHI;
        }
    }

    class ack0AfterReceivedFirstData extends Transition {
        @Override
        public State execute(Msg input) throws UnknownHostException {
            System.out.println("Got SEQ0");

            return State.WAIT1;
        }
    }


    class sendAck0AfterReceived0 extends Transition {
        @Override
        public State execute(Msg input) throws UnknownHostException {
            System.out.println("Got SEQ0");
            ACKBytes[0] = 0;
            packetToSend = new DatagramPacket(ACKBytes, ACKBytes.length, InetAddress.getByName("localhost"), 7774);
            return State.WAIT1;
        }
    }

    class sendAck0AfterReceived1 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Got SEQ1");
            return State.WAIT0;
        }
    }

    class sendAck1AfterReceived0 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Got SEQ0");
            return State.WAIT1;
        }
    }

    class sendAck1AfterReceived1 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Got SEQ1");
            return State.WAIT0;
        }
    }
}
