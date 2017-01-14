package sweiss.SS16;

/**
 * Created by Nelson on 25.09.2016.
 */
public class Money implements Cloneable {

    public static final int TOTAL = 10;
    private int coins;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    private Money(int numberOfCoins) {
        coins = numberOfCoins;
    }

    public Money split(int coins) {
        if (getCoins() - coins > 0) {
            setCoins(getCoins() - coins);
            return new Money(coins);
        }
        return null;
    }

    public Money clone() {
        return Money.Factory.make(this.coins);
    }

    @Override
    public void finalize() {
        Money.Factory.setExistingCoins(Factory.getExistingCoins() - this.coins);
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static class Factory {

        static int existingCoins;

        public static Money make(int coins) {
            if (existingCoins + coins <= Money.TOTAL) {
                existingCoins += coins;
                return new Money(coins);
            }
            return null;
        }

        public static int getExistingCoins() {
            return existingCoins;
        }

        public static void setExistingCoins(int existingCoins) {
            Factory.existingCoins = existingCoins;
        }
    }

}
