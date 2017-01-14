package sweiss.SS16;

/**
 * Created by Nelson on 25.09.2016.
 */
public class javaTest {

    public static void main(String[] args) {


        BoundHashMap<Integer, String> boundHashMap = new BoundHashMap<>();

        boundHashMap.put(1, "one");
        boundHashMap.put(2, "two");
        boundHashMap.put(3, "three");

        System.out.println(boundHashMap.toString());

        boundHashMap.put(2, "four");

        System.out.println(boundHashMap.toString());




        IPP ipp1 = new IPP();
        IPP ipp2 = new IPP();
        IPP ipp3 = new IPP();
        ipp2.setNextIPP(ipp3);
        ipp3.setNextIPP(ipp1);
        ipp1.setNextIPP(ipp2);


    }
}
