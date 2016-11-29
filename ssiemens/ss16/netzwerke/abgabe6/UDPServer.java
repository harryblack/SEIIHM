package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UDPServer {
    private static final int PACKET_SIZE = 1_400;           // Bytes
    private static final long WAIT_FOR_TIMEOUT = 2_000;     // Milliseconds


    public static void main(String[] args) throws SocketException {
        System.out.println("UDP-Server started...");

        final byte[] receiveSize = new byte[PACKET_SIZE];

        final DatagramSocket udpSocket = new DatagramSocket(7777);
        final DatagramPacket receiveData = new DatagramPacket(receiveSize, receiveSize.length);

        long sumBytesReceived = 0;
        long startTime = -1;         // Only inital value
        try {
            udpSocket.receive(receiveData);     // First wait for data
            startTime = System.currentTimeMillis();
            udpSocket.setSoTimeout((int) WAIT_FOR_TIMEOUT);
            sumBytesReceived += receiveData.getLength();
            while (true) {
                udpSocket.receive(receiveData);
                sumBytesReceived += receiveData.getLength();
            }
        } catch (SocketTimeoutException e) {
            final long sendDuration = System.currentTimeMillis() - WAIT_FOR_TIMEOUT - startTime;
            System.out.println("Send duration: " + sendDuration);
            System.out.println("Received Data: " + sumBytesReceived);
            System.out.println("BytesPerSecond: " + sumBytesReceived / sendDuration * 1000);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
