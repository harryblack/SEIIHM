package ssiemens.ss16;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamCheck {
    public static void main(String[] args) {
        Stream<Integer> stream1 = Stream.iterate(0, n -> n + 1);
        Stream<Integer> stream2 = stream1.peek(System.out::println);
        Stream<Integer> stream3 = stream2.filter(n -> n > 3);
        System.out.println("---");
        Stream<Integer> stream4 = stream3.skip(1);
        Optional<Integer> optional = stream4.findFirst();

        System.out.println("=> " + optional.get());
    }
}