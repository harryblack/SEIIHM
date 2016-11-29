package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "10.179.11.78";//"localhost";
    private static final long PACKET_SIZE = 1000;           // Bytes
    private static final long SENDING_DURATION = 10_000;    // Milliseconds
    private static final long waitAfterNPackets = 1;
    private static final long waitForKMillis = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("TCP-Client started...");

        final byte[] dataToSent = new byte[(int) PACKET_SIZE];
        try (Socket clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
             OutputStream outputStream = clientSocket.getOutputStream();
        ) {
            long i = 0;
            long counter = 0;
            final long startTime = System.currentTimeMillis();
            final long stopTime = startTime + SENDING_DURATION;
            while (System.currentTimeMillis() < stopTime) {
                i++;
                outputStream.write(dataToSent);
                counter++;
                if (counter % waitAfterNPackets == 0){
                    Thread.sleep(waitForKMillis);
                }
            }
            System.out.println("i: "+i);
            System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
            System.out.println("Bytes sent: " + counter * PACKET_SIZE);
            System.out.println("BytesPerSecond: " + (counter * PACKET_SIZE) / (SENDING_DURATION / 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
