package ssiemens.ss16.se2.se2_2013ss;

/**
 * Created by Sascha on 02/01/2017.
 */
public class ProducerRobot extends Thread {
    final MaxSizeQueue<Part> queue;

    public ProducerRobot(MaxSizeQueue<Part> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            Part elem = new Part();
            queue.enqueue(elem);
        }
    }
}
