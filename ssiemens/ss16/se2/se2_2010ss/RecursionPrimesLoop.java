package ssiemens.ss16.se2.se2_2010ss;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sascha on 11/01/2017.
 */
public class RecursionPrimesLoop {
    public static List<Integer> primesLoop(int n){
        if(n<2) return null;
        List<Integer> factors = new ArrayList<>();
        doRecursion(factors, n, 2);
        return factors;
    }

    private static void doRecursion(List<Integer> list, int number, int factor) {
        if(number < factor) return;

        if(number % factor == 0){
            list.add(factor);
            number = number/factor;
        } else {
            factor++;
        }
        doRecursion(list, number, factor);
    }

    public static void main(String[] args) {
        System.out.println(primesLoop(6));
    }
}
