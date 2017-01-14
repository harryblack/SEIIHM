package externerCode;


import java.util.ArrayList;
import java.util.HashMap;

public class BoundedHashMapByChrisTheFreakBrammer<K, V> extends HashMap<K, V> {
    final int MAX_SIZE = 6;
    final ArrayList<K> list = new ArrayList();

    @Override
    public V put(K key, V value) {
        if (!this.containsKey(key)) {
            if (list.size() < MAX_SIZE) {
                list.remove(0);
            }
            // Put new
            list.add(key);
        } else {
            list.remove(key);
            list.add(key);
        }
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        list.remove(key);
        return super.remove(key);
    }

    @Override
    public V get(Object key) {
        return super.get(key);
    }
}