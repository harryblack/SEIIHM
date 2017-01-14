package sweiss.SS16;

import java.util.*;

/**
 * Created by Nelson on 16.09.2016.
 */
public class BoundHashMap<T, U> extends HashMap<T, U> {

    public static final int MAX_SIZE = 3;

    // Objektvariablen
    private final List<Object> arrayList = new ArrayList<>();

    // Methode put
    @Override
    public U put(T key, U value) {
        if(arrayList.size() < MAX_SIZE && !arrayList.contains(key)) {
            arrayList.add(key);
        }

        else {
            super.remove(arrayList.get(0));
            arrayList.remove(0);
            arrayList.add(key);
        }
        return super.put(key, value);
    }


    // Methode remove
    @Override
    public U remove(Object key) {
        arrayList.remove(key);
        return super.remove(key);
    }
}