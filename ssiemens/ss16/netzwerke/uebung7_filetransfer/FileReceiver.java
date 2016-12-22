package ssiemens.ss16.netzwerke.uebung7_filetransfer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Arrays;

/**
 * Created by Sascha on 20/12/2016.
 */
public class FileReceiver {

    //****************************************************
    // File format used: "Hi! <size> <filename>"


    private static DatagramPacket packetToSend;
    private static DatagramPacket packetReceived;
    byte[] ACKBytes = new byte[1];

    // all states for this FSM
    enum State {
        WAIT0, WAIT1
    }

    // all messages/conditions which can occur
    enum Msg {
        GOTSEQ0, GOTSEQ1
    }

    // current state of the FSM
    private State currentState;
    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    /**
     * constructor
     */
    public FileReceiver() {
        currentState = State.WAIT0;
        // define all valid state transitions for our state machine
        // (undefined transitions will be ignored)
        transition = new Transition[State.values().length][Msg.values().length];
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ0.ordinal()] = new got0SendAck0();
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ1.ordinal()] = new got1SendAck0();
        transition[State.WAIT1.ordinal()][Msg.GOTSEQ0.ordinal()] = new got0SendAck1();
        transition[State.WAIT1.ordinal()][Msg.GOTSEQ1.ordinal()] = new got1SendAck1();
        System.out.println("INFO FSM constructed, current state: " + currentState);
    }

    /**
     * Process a message (a condition has occurred).
     *
     * @param input Message or condition that has occurred.
     */
    public void
    processMsg(Msg input) throws UnknownHostException {
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
    abstract class
    Transition {
        abstract public State execute(Msg input) throws UnknownHostException;
    }

    class got0SendAck0 extends Transition {
        @Override
        public State execute(Msg input) throws UnknownHostException {
            System.out.println("Got SEQ0");
            ACKBytes[0] = 0;
            packetToSend = new DatagramPacket(ACKBytes, ACKBytes.length, InetAddress.getByName("localhost"), 7774);
            return State.WAIT1;
        }
    }

    class got1SendAck0 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Got SEQ1");
            return State.WAIT0;
        }
    }

    class got0SendAck1 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Got SEQ0");
            return State.WAIT1;
        }
    }

    class got1SendAck1 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Got SEQ1");
            return State.WAIT0;
        }
    }

    public static void main(String[] args) throws IOException {
        // Create new FileReceiver
        FileReceiver fileReceiver = new FileReceiver();
        DatagramSocket socket = new DatagramSocket(7777);

        // Only accept this IP with which initial connection was established
        InetAddress currentConnectionSender = null;
        byte[] greetingBytes = null;
        boolean gotInitialHi = false;
        boolean gotFirstDataPacket = false;

        // Instantiate timeout variable to check for potential socket timeout later
        boolean timeout = false;

        // Instantiate byteArray to store received info
        byte[] bytes = new byte[1400];

        packetReceived = new DatagramPacket(bytes, bytes.length);

        // Establish connection
        while (!gotInitialHi) try {
            socket.setSoTimeout(10_000);
            socket.receive(packetReceived);
            if (new String(packetReceived.getData()).startsWith("Hi!")) {
                gotInitialHi = true;
                currentConnectionSender = packetReceived.getAddress();
                greetingBytes = packetReceived.getData();
                packetToSend = new DatagramPacket(greetingBytes, greetingBytes.length, currentConnectionSender, 7777);
                socket.send(packetToSend);
            }
        } catch (SocketTimeoutException s) {
            return;
        }

        while (!gotFirstDataPacket) try {
            socket.setSoTimeout(10_000);
            socket.receive(packetReceived);
            if (!new String(packetReceived.getData()).startsWith("Hi!")) {
                // store info locally and send ack
                gotFirstDataPacket = true;
            } else {
                packetToSend = new DatagramPacket(greetingBytes, greetingBytes.length, currentConnectionSender, 7777);
                socket.send(packetToSend);
            }
        } catch (SocketTimeoutException s) {
            return;
        }

        while (!timeout)
            try {
                socket.setSoTimeout(5_000);
                socket.receive(packetReceived);

            } catch (SocketTimeoutException s) {
                timeout = true;
            }


        while (!timeout)
            try

            {
                // Set socket timeout and wait for file to receive
                socket.setSoTimeout(10_000);
                socket.receive(packetReceived);

                System.out.println("Packet length: " + packetReceived.getLength());
                System.out.println("Packet port: " + packetReceived.getPort());

                bytes = packetReceived.getData();
                System.out.println("Received byte[] as string: " + new String(bytes));

                // Check for sequence number in packet where sequence number is final byte of byte[]
                fileReceiver.processMsg(Msg.GOTSEQ0);
                fileReceiver.processMsg(bytes[0] == 0 ? Msg.GOTSEQ0 : Msg.GOTSEQ1);

                // send ack as response to received data
                socket.send(packetToSend);

            } catch (
                    SocketTimeoutException s
                    )

            {
                timeout = true;
            }
    }
}
