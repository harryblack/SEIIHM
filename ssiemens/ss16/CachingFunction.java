package ssiemens.ss16;

import java.util.function.Function;

/**
 * Created by Sascha on 10/10/2016.
 */

public class CachingFunction implements Function {

    // Objektvatriablen

    BoundedHashMap rememberParametersAndResults = new BoundedHashMap<>();
    Function f;

    // Ctor

    public CachingFunction(Function f) {
        this.f = f;
    }

    // Methode(n)
    @Override
    public Object apply(Object o) {
        Object result;
        if (rememberParametersAndResults.containsKey(o))
            result = rememberParametersAndResults.get(o);
        else
            result = f.apply(o);
            rememberParametersAndResults.put(o, result);

        return result;
    }
}
