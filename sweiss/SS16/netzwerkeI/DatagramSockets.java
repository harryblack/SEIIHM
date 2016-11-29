package sweiss.SS16.netzwerkeI;

import java.io.IOException;
import java.net.*;

/**
 * Created by Nelson on 07.11.2016.
 */
public class DatagramSockets {
    public static void main(String[] args) throws IOException {
        DatagramSocket s = new DatagramSocket(7777);
        DatagramPacket p = new DatagramPacket("Servus Sascha".getBytes(), "Servus Sascha".length());
        InetAddress addr = InetAddress.getByName("10.179.12.170");

        p.setAddress(addr);
        p.setPort(7777);
        s.send(p);

    }
}
