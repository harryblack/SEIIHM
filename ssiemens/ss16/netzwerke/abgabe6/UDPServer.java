package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Sascha on 28/11/2016.
 */
public class UDPServer {
    private static final int PACKET_SIZE = 1_400;           // Bytes
    private static final long WAIT_FOR_TIMEOUT = 3_000;     // Milliseconds


    public static void main(String[] args) throws SocketException {
        System.out.println("Server started...");

        final byte[] receiveSize = new byte[PACKET_SIZE];

        final DatagramSocket udpSocket = new DatagramSocket(7777);
        final DatagramPacket receiveData = new DatagramPacket(receiveSize, receiveSize.length);

        long counter = 0;
        long startTime = 0;

        try {
            udpSocket.receive(receiveData);     // First wait for data
            startTime = System.currentTimeMillis();
            udpSocket.setSoTimeout((int) WAIT_FOR_TIMEOUT);
            counter++;
            while (true) {
                udpSocket.receive(receiveData);
                counter++;
            }
        } catch (SocketTimeoutException e) {
            final long sendDuration = System.currentTimeMillis() - WAIT_FOR_TIMEOUT - startTime;
            final long receivedBytes = PACKET_SIZE * counter;
            System.out.println("Send duration: " + sendDuration);
            System.out.println("Received Data: " + receivedBytes);
            System.out.println("BytesPerSecond: " + receivedBytes / sendDuration * 1000);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
