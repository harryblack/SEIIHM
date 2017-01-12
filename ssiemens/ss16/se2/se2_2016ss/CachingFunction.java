package ssiemens.ss16.se2.se2_2016ss;

import java.util.function.Function;

/**
 * Created by Sascha on 11/01/2017.
 */
public class CachingFunction<T, R> implements Function<T,R> {
    private final BoundHashMap<T,R> boundHashMap;
    private final Function<T, R> function;

    public CachingFunction(Function<T, R> function) {
        this.boundHashMap = new BoundHashMap<>();
        this.function = function;
    }

    @Override
    public R apply(T t) {
        if(boundHashMap.containsKey(t)) {
            return boundHashMap.get(t);
        } else {
            R result = function.apply(t);
            boundHashMap.put(t,result);
            return result;
        }
    }
}
