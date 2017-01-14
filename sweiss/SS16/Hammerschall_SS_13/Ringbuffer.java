package sweiss.SS16.Hammerschall_SS_13;

import java.util.*;

/**
 * Created by Nelson on 20.12.2016.
 */
public class Ringbuffer<E> extends LinkedList<E> {

    public static void main(String[] args) {
        LinkedList<Integer> ll = new LinkedList<>();
        ll.add(4);
        ll.add(5);
        ll.add(7);
        Ringbuffer<Integer> rb = new Ringbuffer<>(2, ll);
        System.out.println(rb.toString());
    }


    int max_size;
    ArrayList<E> collection;


    public Ringbuffer(int max) {
        max_size = max;
    }

    public Ringbuffer(int max, Collection<E> collection) {



    }


}
