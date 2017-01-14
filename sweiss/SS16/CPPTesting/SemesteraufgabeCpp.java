package sweiss.SS16.CPPTesting;

import java.util.PriorityQueue;

/**
 * Created by Nelson on 18.11.2016.
 */
public class SemesteraufgabeCpp {

    public static void main(String[] args) {

        PriorityQueue<String> priorityQueue = new PriorityQueue<>(20);

        priorityQueue.add("one");
        priorityQueue.add("twoo");
        priorityQueue.add("five");

        System.out.println(priorityQueue.size());
        //while(priorityQueue.size() != 0) {
            System.out.println(priorityQueue.peek());
        //}
        priorityQueue.add("alpha");
        System.out.println(priorityQueue.peek());
    }
}
