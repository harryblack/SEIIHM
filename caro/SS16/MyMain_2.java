import java.util.ArrayList;

public class MyMain_2 {
	public static void main(String... args) {
		final int ringSize = 5;
		ArrayList<IPP> liste = new ArrayList<>();

		for (int i = 0; i < ringSize; i++) {
			if (liste.isEmpty()) {
				liste.add(new IPP());
			} else {
				System.out.println(i + " " + liste.get(i - 1).getName());
				liste.add(new IPP(liste.get(i - 1), "Thread: " + i));
			}

		}
		liste.get(0).setNextIPP(liste.get(ringSize - 1));

		for (IPP ipp : liste) {
			ipp.start();
		}

		liste.get(0).interrupt();
	}
}
