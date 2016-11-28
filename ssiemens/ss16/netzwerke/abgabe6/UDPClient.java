package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Sascha on 28/11/2016.
 */
public class UDPClient {
    public static void main(String[] args) throws IOException, InterruptedException {


        DatagramSocket datagramSocket = new DatagramSocket();
        final long packetSize = 1400;

        byte[] sendPacket = new byte[(int) packetSize];

        DatagramPacket datagramPacket = new DatagramPacket(sendPacket, sendPacket.length, InetAddress.getByName("localhost"), 7777);


        long sendingDuration = 5_000;

        long startTime = System.currentTimeMillis();
        long timeToStop = startTime + sendingDuration;
        long counter = 0;
        while (System.currentTimeMillis() < timeToStop) {
            datagramSocket.send(datagramPacket);
            counter++;
        }
        System.out.println("Duration: " + (System.currentTimeMillis()-startTime));
        System.out.println("Data transfered: " + packetSize * counter);
        System.out.println("Bytes/Second: " + packetSize * counter / sendingDuration*1000);


    }
}
