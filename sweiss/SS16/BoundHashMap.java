package sweiss.SS16;

import java.util.*;

/**
 * Created by Nelson on 16.09.2016.
 */
public class BoundHashMap<T, U> extends HashMap {

    public static final int MAX_SIZE = 5;

    // Objektvariablen

    private final Map linkedHashMap = new LinkedHashMap<>();

    // Methode put

    @Override
    public Object put(Object key, Object value) {
        return super.put(key, value);
    }


    // Methode remove


    @Override
    public boolean remove(Object key, Object value) {
        return super.remove(key, value);
    }
}