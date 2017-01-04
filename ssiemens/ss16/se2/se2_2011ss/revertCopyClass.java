package ssiemens.ss16.se2.se2_2011ss;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by Sascha on 04/01/2017.
 */
public class revertCopyClass {
    public static <T> void revertCopy(List<T> list1, List<T> list2){
        ListIterator<T> it = list1.listIterator(list1.size());
        while(it.hasPrevious()){
            list2.add(it.previous());
        }
    }
}
