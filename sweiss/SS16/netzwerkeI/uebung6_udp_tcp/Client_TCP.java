package sweiss.SS16.netzwerkeI.uebung6_udp_tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Nelson on 28.11.2016.
 */
public class Client_TCP {
    public static void main(String[] args) throws IOException, InterruptedException {

        byte[] bytes = new byte[1400];
        final long duration = 10_000;
        final long start = System.currentTimeMillis();
        final long end = start + duration;
        int packetCounter = 0;
        final int N = 1000;
        final int k = 10;

        Socket clientSocket = new Socket("localhost", 7777);
        OutputStreamWriter outToServer = new OutputStreamWriter(clientSocket.getOutputStream());
        while (System.currentTimeMillis() < end) {
            outToServer.write("a");  // bytes.toString()
            outToServer.flush();
            packetCounter++;

            if (packetCounter % N == 0) {
                Thread.sleep(k);
            }
            //System.out.println("Packets sent: " + packetCounter);
        }
        System.out.println("packets sent: " + packetCounter);
        System.out.println(("kbit/s: " + (packetCounter*1400*0.008)/duration));
    }
}
