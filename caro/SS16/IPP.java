
public class IPP extends Thread {

	public static final int N = 5;

	private IPP nextIPP;

	/**
	 * @param ipp
	 */
	public IPP(IPP nextIPP, String name) {
		super();
		this.nextIPP = nextIPP;
		setName(name);
	}

	public IPP() {
	}

	public IPP getNextIPP() {
		return nextIPP;
	}

	public void setNextIPP(IPP nextIPP) {
		this.nextIPP = nextIPP;
	}

	@Override
	public void run() {
		for (int i = 1; i <= N; i++) {
			synchronized (this) {
				try {
					Thread.currentThread().wait();
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName() + ",   " + i);
				}
			}
			nextIPP.interrupt();
		}
	}
}
