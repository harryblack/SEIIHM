package sweiss.SS16.netzwerkeI.uebung6_udp_tcp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Nelson on 28.11.2016.
 */
public class Throughput_UDP_TCP {
    public static void main(String... args) throws IOException {
        // Create 1400 Byte Array
        byte[] bytes = new byte[1400];
        // Write Array content to file
        FileOutputStream out = new FileOutputStream("bytes.txt");
        out.write(bytes);
        out.close();
        System.out.println(bytes.length);


        DatagramSocket client = new DatagramSocket(7777);
        InetAddress addr = InetAddress.getByName("10.179.12.170");
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr, 7777);

        client.send(packet);
    }

}
