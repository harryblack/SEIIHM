import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Nelson on 16.09.2016.
 */
public class HashTest {

    public static void main(String[] args) {
        final Map<Integer, String> hMap = new TreeMap<>();
        hMap.put(1, "One");
        hMap.put(2, "Two");
        hMap.put(3, "Three");
        hMap.put(7,"Seven");
        hMap.put(5, "Five");

        System.out.println(hMap.toString());
    }
}
