package sweiss.SS16.Hammerschall_SS_13;


/**
 * Created by Nelson on 02.01.2017.
 */
public class ConsumerRobot extends Thread {
    MaxSizeQueue<Part> maxSizeQueue;

    public ConsumerRobot(MaxSizeQueue<Part> maxSizeQueue) {
        this.maxSizeQueue = maxSizeQueue;
    }

    public void run() {
        int count = 0;
        while (count < 100) {
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            maxSizeQueue.enqueue(new Part());
            count++;
            System.out.println("consumed");
            if (count % 10 == 0)
                System.out.println("current queue: " + maxSizeQueue.arrayList);
        }
    }
}
