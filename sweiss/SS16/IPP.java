package sweiss.SS16;

/**
 * Created by Nelson on 19.09.2016.
 */
public class IPP extends Thread {

    // Objektvariablen
    public static final int N = 5;
    private IPP nextIPP;

    // Constructor (default)
    public IPP() {
        // empty
    }

    // Weitere Methoden

    public IPP getNextIPP() {
        return nextIPP;
    }

    public void setNextIPP(IPP nextIPP) {
        this.nextIPP = nextIPP;
    }

    @Override
    public void run() {
        int interrupts = 0;
        while (true && interrupts < N) {
            while (!isInterrupted()) {
                // do nothing
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            nextIPP.interrupt();
            interrupts++;
        }

    }


}
