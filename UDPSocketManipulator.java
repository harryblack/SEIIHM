import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

class UDPSocketManipulator extends DatagramSocket {

    private final double probabilityLoosePacket;
    private final double probabilityDoublePacket;
    private final double probabilityBiterrorPacket;
    private final Random randomGenerator = new Random();
    private long totalPackets;
    private long destroyedPackets;
    private long doublePackets;
    private long bitErrorPackets;
    private DatagramPacket packetWithBitError;

    UDPSocketManipulator(int port,
                         double probabilityBiterrorPacket,
                         double probabilityDoublePacket,
                         double probabilityLoosePacket) throws SocketException {
        super(port);
        this.probabilityBiterrorPacket = probabilityBiterrorPacket;
        this.probabilityDoublePacket = probabilityDoublePacket;
        this.probabilityLoosePacket = probabilityLoosePacket;
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        boolean doDoubleSend;
        boolean doBitError;

        totalPackets++;
        final double randomNumberBitError = randomGenerator.nextDouble();
        final double randomNumberDoubleSend = randomGenerator.nextDouble();
        final double randomNumberDestroy = randomGenerator.nextDouble();

        // MAKE BITERROR IN PACKET
        if (randomNumberBitError >= probabilityBiterrorPacket) {
            doBitError = false;
        } else {
            assert Tracer.printConsoleLog("GENERATE BITERROR");
            bitErrorPackets++;
            byte[] data = p.getData().clone();
            int lastByte = data[p.getLength() - 1];
            int newLastByte = lastByte ^ 1;
            assert Tracer.printConsoleLog("lastByte: " + lastByte + " NewLastByte: " + newLastByte);
            data[p.getLength() - 1] = (byte) newLastByte;
            packetWithBitError = new DatagramPacket(data, data.length, p.getAddress(), p.getPort());
            doBitError = true;
        }

        // DOUBLE PACKET
        if (randomNumberDoubleSend >= probabilityDoublePacket) {
            doDoubleSend = false;
        } else {
            assert Tracer.printConsoleLog("SEND PACKET TWICE");
            doublePackets++;
            doDoubleSend = true;
        }

        // DESTROY PACKET
        if (randomNumberDestroy >= probabilityLoosePacket) {
            int sendTimes = doDoubleSend ? 2 : 1;
            for (int i = 0; i < sendTimes; i++) {
                super.send(doBitError ? packetWithBitError : p);
            }
        } else {
            assert Tracer.printConsoleLog("DESTROY PACKET");
            destroyedPackets++;
        }

    }

    @Override
    public void close() {
        System.out.println("Probability destroyed packets: " + ((double) destroyedPackets) / totalPackets);
        System.out.println("Probability BitError packets: " + ((double) bitErrorPackets) / totalPackets);
        System.out.println("Probability double packets: " + ((double) doublePackets) / totalPackets);

        super.close();
    }
}
