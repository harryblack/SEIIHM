package sweiss.SS16.netzwerkeI.uebung6_udp_tcp;

import java.io.IOException;
import java.net.*;

/**
 * Created by Nelson on 28.11.2016.
 */
public class Client_UDP extends Thread {
    // Socket objects
    byte[] bytes = new byte[1400];
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), 7777);
    DatagramSocket socket = new DatagramSocket();

    public Client_UDP() throws SocketException, UnknownHostException {
    }

    @Override
    public void run() {
        int packetCounter = 0;
        final long start = System.currentTimeMillis();
        final long k = 50;  // duration of sleep
        final int N = 200;  // frequency of sleep
        final long duration = 30_000;
        final long end = start + duration;
        int totalLastBreak = 0;

        while (System.currentTimeMillis() < end) {
            try {
                socket.send(packet);
                packetCounter++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (packetCounter % N == 0) {
                try {
                    sleep(k);
                    //System.out.println(packetCounter - totalLastBreak);
                    //totalLastBreak = packetCounter;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Packets sent: " + packetCounter);
    }
}
