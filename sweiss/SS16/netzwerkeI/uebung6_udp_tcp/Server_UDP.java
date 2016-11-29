package sweiss.SS16.netzwerkeI.uebung6_udp_tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Nelson on 28.11.2016.
 */
public class Server_UDP extends Thread {
    private DatagramSocket socket = new DatagramSocket(7777);
    private int bytesReceived;
    private byte[] bytes = new byte[1400];
    boolean timedOut;

    public Server_UDP() throws SocketException {
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        while(!timedOut)
        try {
            socket.setSoTimeout(2000);
            socket.receive(packet);
            socket.setSoTimeout(0);
            bytesReceived += packet.getData().length;
        } catch (SocketTimeoutException s) {
            System.out.println("Transfer rate in kbit/s: " + ((bytesReceived * 0.008) / ((System.currentTimeMillis() - startTime) / 1000)));
            System.out.println("Server packets received: " + bytesReceived / 1400);
            timedOut = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
