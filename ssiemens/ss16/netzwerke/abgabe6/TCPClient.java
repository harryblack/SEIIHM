package ssiemens.ss16.netzwerke.abgabe6;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Sascha on 28/11/2016.
 */
public class TCPClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final long PACKET_SIZE = 1400;           // Bytes
    private static final long SENDING_DURATION = 30_000;    // Milliseconds

    public static void main(String[] args) throws IOException {

        final byte[] dataToSent = new byte[(int) PACKET_SIZE];

        try (Socket clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
             OutputStream outputStream = clientSocket.getOutputStream();
        ) {
            final long startTime = System.currentTimeMillis();
            final long stopTime = startTime + SENDING_DURATION;
            long counter = 0;

            while (System.currentTimeMillis() < stopTime) {
                outputStream.write(dataToSent);
                counter++;
            }
            System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
            System.out.println("Bytes sent: " + counter * PACKET_SIZE);
            System.out.println("BytesPerSecond: " + (counter * PACKET_SIZE) / (SENDING_DURATION / 1000));
        }
    }
}
