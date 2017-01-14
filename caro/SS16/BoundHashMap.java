import java.util.ArrayList;
import java.util.HashMap;

public class BoundHashMap<T, U> extends HashMap<T, U> {

	public static final int MAX_SIZE = 5;

	private ArrayList<T> arrayList = new ArrayList<>();

	@Override
	public U put(T key, U value) {
		if (MAX_SIZE <= size()) {
			remove(getArrayList().get(0));
		}
		getArrayList().add(key);
		return super.put(key, value);
	}

	@Override
	public U remove(Object key) {
		getArrayList().remove(0);
		System.out.println(key);
		return super.remove(key);
	}

	private ArrayList<T> getArrayList() {
		return arrayList;
	}

	private void setArrayList(ArrayList<T> arrayList) {
		this.arrayList = arrayList;
	}

}
