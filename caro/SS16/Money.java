public class Money implements Cloneable {

	public static final int TOTAL = 400;
	private int coins;

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	private Money(int coins) {
		super();
		this.coins = coins;
	}

	static class Factory {
		static int counter = 0;

		public static Money make(int coins) {

			if (counter + coins <= TOTAL) {
				counter += coins;
				return new Money(coins);
			}
			return null;
		}

	}

	public Money split(int coins) {
		if (getCoins() - coins >= 1) {
			setCoins(getCoins() - coins);
			return new Money(coins);
		}
		return null;
	}

	public Money clone() {
		return Money.Factory.make(getCoins());
	}

	@Override
	protected void finalize() throws Throwable {
		Money.Factory.counter -= getCoins();
		super.finalize();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
