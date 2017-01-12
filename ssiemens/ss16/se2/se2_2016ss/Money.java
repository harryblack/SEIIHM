package ssiemens.ss16.se2.se2_2016ss;

public class Money implements Cloneable {
    public static final int TOTAL = 20;
    private int coins;

    public int getCoins() {
        return coins;
    }

    private Money(int coins) {
        this.coins = coins;
    }

    public static class CreateMoney {
        private static int sumUsedCoins;

        public Money make(int coins) {
            if(coins < 1) return null;
            if (sumUsedCoins + coins > TOTAL) return null;
            else {
                sumUsedCoins += coins;
                return new Money(coins);
            }
        }
    }
}
