package sweiss.SS16;

/**
 * Created by Nelson on 25.09.2016.
 */
public class Money implements Cloneable {
    public static final int TOTAL = 10;
    private int coins;

    private Money(int noOfCoins) {
        coins = noOfCoins;
    }

    public int getCoins() {
        return coins;
    }

    private class makeMoney {

    }


}
