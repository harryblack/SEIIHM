package ssiemens.ss16.netzwerke.abgabe6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sascha on 28/11/2016.
 */
public class TCPServer {
    private static final int SERVER_PORT = 7777;
    private static final long PACKET_SIZE = 1400;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        final byte[] receiveSize = new byte[(int) PACKET_SIZE];

        try (Socket clientSocket = serverSocket.accept();
             InputStream inputStream = clientSocket.getInputStream()
        ) {
            long counter = 0;
            final long startTime = System.currentTimeMillis();
            for (int line = inputStream.read(receiveSize); line != -1; line = inputStream.read(receiveSize))
                counter++;

            final long stopTime = System.currentTimeMillis();
            System.out.println("Bytes received: " + counter * PACKET_SIZE);
            System.out.println("Duration: " + (stopTime - startTime));
        }
    }
}
