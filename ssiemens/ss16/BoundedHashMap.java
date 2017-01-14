package ssiemens.ss16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sascha on 04/10/2016.
 */
public class BoundedHashMap<T, U> extends HashMap<T, U> {

    public static final int MAX_SIZE = 3;

    // Objektvariablen

    private final List<T> arrayList = new ArrayList<>();

    // Methode put
    @Override
    public U put(T key, U value) {
        if(arrayList.size() <= MAX_SIZE) {
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