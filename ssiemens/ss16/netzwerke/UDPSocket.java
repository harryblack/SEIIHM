package ssiemens.ss16.netzwerke;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by Sascha on 07/11/2016.
 */
public class UDPSocket {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(7777);


        byte[] test = "Servus Stephan, gemma bissl getzln? Oder in's Nordbard?".getBytes();
        System.out.println(new String(test));
        DatagramPacket packet = new DatagramPacket(test, test.length);
        packet.setAddress(InetAddress.getByName("10.179.6.29"));
        packet.setPort(7777);

        socket.send(packet);

        DatagramPacket received = new DatagramPacket("test".getBytes(), 4);
        socket.receive(received);
        System.out.println(String.format(received.getAddress() + " " + received.getPort() + " " + new String(received.getData())));

    }
}
