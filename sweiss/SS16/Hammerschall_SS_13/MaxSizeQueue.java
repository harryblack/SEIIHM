package sweiss.SS16.Hammerschall_SS_13;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nelson on 21.12.2016.
 */
public class MaxSizeQueue<T> {
    public ArrayList<T> arrayList;
    private int max;

    public MaxSizeQueue(ArrayList arrayList, int max) {
        this.arrayList = arrayList;
        this.max = max;
    }

    public synchronized void enqueue(T element) {
        if (arrayList.size() < max)
            arrayList.add(element);
        return;
    }

    public synchronized T dequeue() {
        T value = null;
        if (arrayList.size() > 0) {
            value = arrayList.get(arrayList.size() - 1);
            arrayList.remove(arrayList.size() - 1);
        }
        return value;
    }


    public static void main(String[] args) {
        MaxSizeQueue<Part> maxSizeQueue = new MaxSizeQueue<>(new ArrayList(), 10);

        ProducerRobot producerRobot = new ProducerRobot(maxSizeQueue);
        ConsumerRobot consumerRobot = new ConsumerRobot(maxSizeQueue);
        producerRobot.start();
        consumerRobot.start();

    }
}
