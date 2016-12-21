package ssiemens.ss16.netzwerke.uebung7_filetransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Sascha on 20/12/2016.
 */
public class FileReceiver {
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
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ0.ordinal()] = new wait0got0();
        transition[State.WAIT0.ordinal()][Msg.GOTSEQ1.ordinal()] = new wait0got1();
        transition[State.WAIT1.ordinal()][Msg.GOTSEQ0.ordinal()] = new wait1got0();
        transition[State.WAIT1.ordinal()][Msg.GOTSEQ1.ordinal()] = new wait1got1();
        System.out.println("INFO FSM constructed, current state: " + currentState);
    }

    /**
     * Process a message (a condition has occurred).
     *
     * @param input Message or condition that has occurred.
     */
    public void
    processMsg(Msg input) {
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
        abstract public State execute(Msg input);
    }

    class wait0got0 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("Went from WAIT0 to WAIT1");
            return State.WAIT1;
        }
    }

    class wait0got1 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("wait0got1");
            return State.WAIT0;
        }
    }

    class wait1got0 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("wait1got0");
            return State.WAIT1;
        }
    }

    class wait1got1 extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("wait1got1");
            return State.WAIT0;
        }
    }
}
