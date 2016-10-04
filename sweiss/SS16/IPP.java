package sweiss.SS16;

/**
 * Created by Nelson on 19.09.2016.
 */
public class IPP extends Thread {
    public static final int N = 5;

    private IPP nextIPP;



    public IPP getNextIPP() {
        return nextIPP;
    }

    public void setNextIPP(IPP nextIPP) {
        this.nextIPP = nextIPP;
    }



}
