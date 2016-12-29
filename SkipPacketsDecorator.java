import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Sascha on 23/12/2016.
 */
class SkipPacketsDecorator extends DatagramSocket {
    private final double probabilityLoosePacket;
    private final double probabilityDoublePacket;
    private final double probabilityBiterrorPacket;
    private final Random randomGenerator = new Random();
    private long totalPackets;
    private long destroyedPackets;
    private long doublePackets;
    private long biterrorPackets;

    SkipPacketsDecorator(int port, double probabilityLoosePacket, double probabilityDoublePacket, double probabilityBiterrorPacket) throws SocketException {
        super(port);
        this.probabilityLoosePacket = probabilityLoosePacket;
        this.probabilityDoublePacket = probabilityDoublePacket;
        this.probabilityBiterrorPacket = probabilityBiterrorPacket;
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        totalPackets++;
        final double randomNumber = randomGenerator.nextDouble();

        /*
        // DESTROY PACKET
        if (randomNumber >= probabilityLoosePacket) {
            super.send(p);
        } else {
            System.out.println("DESTROY PACKET");
            destroyedPackets++;
        }
        */

        // DOUBLE PACKET
        if (randomNumber >= probabilityDoublePacket) {
            super.send(p);
        } else {
            System.out.println("SEND PACKET TWICE");
            super.send(p);
            super.send(p);
        }

/*
        // MAKE BITERROR IN PACKET
        if (randomNumber >= probabilityBiterrorPacket) {
            super.send(p);
        } else {
            System.out.println("GENERATE BITERROR");
            byte[] data = p.getData().clone();
            int lastByte = data[p.getLength() - 1];
            int newLastByte = lastByte ^ 1;
            System.out.println("lastByte: " + lastByte + " NewLastByte: " + newLastByte);
            data[p.getLength() - 1] = (byte) newLastByte;
            System.out.println(p.getPort());
            DatagramPacket packetWithBitError = new DatagramPacket(data, data.length, p.getAddress(), p.getPort());
            super.send(packetWithBitError);
        }
        */
    }

    @Override
    public void close() {
        System.out.println("Probability destroyed packets: " + ((double) destroyedPackets) / totalPackets);
        super.close();
    }
}
