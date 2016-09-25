import java.util.function.Function;

/**
 * Created by Nelson on 18.09.2016.
 */
public class MainForCachingFunction {

    public static void main(String[] args) {
        Function f = (t) -> (Integer)t + 5;

        CachingFunction cachingFunction = new CachingFunction(f);

        System.out.println(cachingFunction.apply(5));
        System.out.println(cachingFunction.apply(5));
    }
}
