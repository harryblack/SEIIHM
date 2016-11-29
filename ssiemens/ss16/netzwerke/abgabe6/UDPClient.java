package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private static final long PACKET_SIZE = 1400;           // Bytes
    private static final long SENDING_DURATION = 10_000;    // Milliseconds
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;
    private static final long waitAfterNPackets = 10_000;
    private static final long waitForKMillis = 200;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDP-Client started...");

        final byte[] packetToBeSent = new byte[(int) PACKET_SIZE];

        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(packetToBeSent, packetToBeSent.length, InetAddress.getByName(SERVER_HOST), SERVER_PORT);
        final long startTime = System.currentTimeMillis();
        final long timeToStop = startTime + SENDING_DURATION;
        long counter = 0;
        while (System.currentTimeMillis() < timeToStop) {
            datagramSocket.send(datagramPacket);
            counter++;
            if (counter % waitAfterNPackets == 0) {
                final long timeLeft = timeToStop - System.currentTimeMillis();
                Thread.sleep(timeLeft < waitForKMillis ? timeLeft : waitForKMillis);
            }
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
        System.out.println("Bytes transfered: " + PACKET_SIZE * counter);
        System.out.println("Bytes/Second: " + (PACKET_SIZE * counter) / (SENDING_DURATION / 1000));
    }
}