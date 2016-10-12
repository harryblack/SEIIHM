package ssiemens.ss16;

/**
 * Created by Sascha on 10/10/2016.
 */
public class Firlefanz {

    public static void main(String[] args) {
        BoundedHashMap<Integer, String> boundedHashMap = new BoundedHashMap<>();

        boundedHashMap.put(1,"test1");
        boundedHashMap.put(2,"test2");
        boundedHashMap.put(3,"test3");
        boundedHashMap.put(3,"test33");
        boundedHashMap.put(4,"test4");
        boundedHashMap.remove(4);
        boundedHashMap.put(5,"test5");


        System.out.println(boundedHashMap.toString());


        // ------
        /*
        CachingFunction cachingFunction = new CachingFunction(Math::sin);
        cachingFunction.apply(0.8);
        */
    }



}
