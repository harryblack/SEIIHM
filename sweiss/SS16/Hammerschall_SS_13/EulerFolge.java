package sweiss.SS16.Hammerschall_SS_13;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by Nelson on 08.12.2016.
 */
public abstract class EulerFolge implements Iterable {

    public static void main(String[] args) {

        EulerFolge e = new EulerFolge() {
            double i = 1;
            @Override
            public Iterator iterator() {
                return new Iterator() {
                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Object next() {
                        return Math.pow(1+(1/i),i++);
                    }
                };
            }
        };
        for(Object i : e) {
            System.out.println((double)i);
        }
    }
}

