package ssiemens.ss16.se2;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Sascha on 02/01/2017.
 */
public class BiMap<K, V> extends HashMap {
    @Override
    public Object put(Object key, Object value) {
        if (containsValue(value)) {
            Set<K> set = keySet();
            Object[] test = new Object[set.size()];
            set.toArray(test);
            boolean haveFound = false;
            for (int i = 0; i < test.length && !haveFound; i++) {
                haveFound = remove(test[i], value);
            }
        }
        return super.put(key, value);
    }
}
