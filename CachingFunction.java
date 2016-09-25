import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by Nelson on 18.09.2016.
 */
public class CachingFunction implements Function {

    private Function theFunction;
    private final HashMap hashMap = new HashMap();

    public CachingFunction(Function f) {
        theFunction = f;
    }

    /**
    * Returns the corresponding function value for the input value
    * @param o the function argument
    * @return function value of theFunction
     */
    @Override
    public Object apply(Object o) {
        if (hashMap.containsKey(o)) {
            return hashMap.get(o);
        }
        else hashMap.put(o, theFunction.apply(o));
            return theFunction.apply(o);
    }
}
