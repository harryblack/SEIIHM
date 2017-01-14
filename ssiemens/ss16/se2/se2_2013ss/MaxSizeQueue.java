package ssiemens.ss16.se2.se2_2013ss;

import java.util.ArrayList;

/**
 * Created by Sascha on 02/01/2017.
 */
public class MaxSizeQueue<T> {
    private final int maxSize;
    private final ArrayList<T> elements;

    public MaxSizeQueue(int maxSize) {
        this.maxSize = maxSize;
        this.elements = new ArrayList<>();
    }

    public synchronized void enqueue(T elementToInsert) {
        if (elements.size() == maxSize) return;
        elements.add(elementToInsert);
    }

    public synchronized T dequeue() {
        if (elements.size() == 0) return null;
        T result;
        result = elements.get(0);
        elements.remove(0);
        return result;
    }
}
