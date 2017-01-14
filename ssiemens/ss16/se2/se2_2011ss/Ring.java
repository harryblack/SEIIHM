package ssiemens.ss16.se2.se2_2011ss;

import java.util.List;

/**
 * Created by Sascha on 04/01/2017.
 */
public interface Ring<E> extends List<E> {
    // Liefert eine Instanz eines RingIterators
    RingIterator<E> ringIterator();
}
