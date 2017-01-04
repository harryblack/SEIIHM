package ssiemens.ss16.se2.se2_2013ss;

import ssiemens.ss16.se2.se2_2013ss.ConsumerRobot;
import ssiemens.ss16.se2.se2_2013ss.MaxSizeQueue;
import ssiemens.ss16.se2.se2_2013ss.Part;
import ssiemens.ss16.se2.se2_2013ss.ProducerRobot;

/**
 * Created by Sascha on 02/01/2017.
 */
public class MainRobot {
    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException();

        MaxSizeQueue<Part> partsQueue = new MaxSizeQueue<>(300);

        int numberOfRobots = Integer.parseInt(args[0]);
        for (int i = 0; i < numberOfRobots; i++) {
            new ProducerRobot(partsQueue).start();
            new ConsumerRobot(partsQueue).start();
        }
    }
}
