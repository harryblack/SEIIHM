package sweiss.SS16.Hammerschall_SS_13;

import java.util.Arrays;

/**
 * Created by Nelson on 02.01.2017.
 */
public class ProducerRobot extends Thread {
    MaxSizeQueue<Part> maxSizeQueue;

    public ProducerRobot(MaxSizeQueue<Part> maxSizeQueue) {
        this.maxSizeQueue = maxSizeQueue;
    }

    public void run() {
        int count = 0;
        while(count < 100) {
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            maxSizeQueue.dequeue();

            count++;
            System.out.println("produced");
            if(count%10 == 0)
            System.out.println("current queue: " + maxSizeQueue.arrayList);
        }
    }
}
