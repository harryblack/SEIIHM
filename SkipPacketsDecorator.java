import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * Created by Sascha on 23/12/2016.
 */
class SkipPacketsDecorator extends DatagramSocket {
    private final double probability;
    private final Random randomGenerator = new Random();
    private long totalPackets;
    private long destroyedPackets;

    SkipPacketsDecorator(int port, double probability) throws SocketException {
        super(port);
        this.probability = probability;
    }

    @Override
    public synchronized void receive(DatagramPacket p) throws IOException {
        totalPackets++;
        final double randomNumber = randomGenerator.nextDouble();
        if (randomNumber >= probability) {
            super.receive(p);
        } else {
            System.out.println("DESTROY PACKET");
            this.
            destroyedPackets++;
            super.receive(null);
        }

    }

    @Override
    public void close() {
        System.out.println("Probability destroyed packets: "+ ((double)destroyedPackets) / totalPackets);
        super.close();
    }
}
