package ssiemens.ss16.netzwerke.uebung7_filetransfer;

/**
 * Class which models the state machine itself.
 */
public class FileSender {


    // all states for this FSM
    enum State {
        WAIT_FOR_SEND_SEQ_ZERO, WAIT_FOR_SEND_SEQ_ONE, WAIT_FOR_ACK_ZERO, WAIT_FOR_ACK_ONE
    }

    ;

    // all messages/conditions which can occur
    enum Msg {
        REQUEST_FOR_SEND_SEQ_ZERO,
        REQUEST_FOR_SEND_SEQ_ONE,
        GOT_ACK_ZERO,
        GOT_ACK_ONE,
        TIMED_OUT_SEQ_ZERO,
        TIMED_OUT_SEQ_ONE,
    }

    // current state of the FSM
    private State currentState;
    // 2D array defining all transitions that can occur
    private Transition[][] transition;

    /**
     * constructor
     */
    public FileSender() {
        currentState = State.WAIT_FOR_SEND_SEQ_ZERO;
        // define all valid state transitions for our state machine
        // (undefined transitions will be ignored)
        transition = new Transition[State.values().length][Msg.values().length];
        transition[State.WAIT_FOR_SEND_SEQ_ZERO.ordinal()][Msg.REQUEST_FOR_SEND_SEQ_ZERO.ordinal()] = new SendSeqZero();
        transition[State.WAIT_FOR_ACK_ZERO.ordinal()][Msg.TIMED_OUT_SEQ_ZERO.ordinal()] = new RetransmitSeqZero();
        transition[State.WAIT_FOR_ACK_ZERO.ordinal()][Msg.GOT_ACK_ZERO.ordinal()] = new StopTimerSeqZero();
        transition[State.WAIT_FOR_SEND_SEQ_ONE.ordinal()][Msg.REQUEST_FOR_SEND_SEQ_ONE.ordinal()] = new SendSeqOne();
        transition[State.WAIT_FOR_ACK_ONE.ordinal()][Msg.GOT_ACK_ONE.ordinal()] = new StopTimerSeqOne();
        transition[State.WAIT_FOR_ACK_ONE.ordinal()][Msg.TIMED_OUT_SEQ_ONE.ordinal()] = new RetransmitSeqOne();
        System.out.println("INFO FSM constructed, current state: " + currentState);
    }

    /**
     * Process a message (a condition has occurred).
     *
     * @param input Message or condition that has occurred.
     */
    public void processMsg(Msg input) {
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
        abstract public State execute(Msg input);
    }

    class SendSeqZero extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("SendSeqZero!");
            return State.WAIT_FOR_ACK_ZERO;
        }
    }

    class RetransmitSeqZero extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("RetransmitSeqZero!");
            return State.WAIT_FOR_ACK_ZERO;
        }
    }

    class StopTimerSeqZero extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("StopTimerSeqZero!");
            return State.WAIT_FOR_SEND_SEQ_ONE;
        }
    }

    class SendSeqOne extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("SendSeqOne!");
            return State.WAIT_FOR_ACK_ONE;
        }
    }

    class StopTimerSeqOne extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("StopTimerSeqOne!");
            return State.WAIT_FOR_SEND_SEQ_ZERO;
        }
    }

    class RetransmitSeqOne extends Transition {
        @Override
        public State execute(Msg input) {
            System.out.println("RetransmitSeqOne!");
            return State.WAIT_FOR_ACK_ONE;
        }
    }

}
