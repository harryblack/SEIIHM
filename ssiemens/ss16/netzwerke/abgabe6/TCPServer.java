package ssiemens.ss16.netzwerke.abgabe6;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private static final int SERVER_PORT = 7777;
    private static final long PACKET_SIZE = 1000;

    public static void main(String[] args) throws IOException {
        System.out.println("TCP-Server started...");
        final byte[] receiveSize = new byte[(int) PACKET_SIZE];
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        try (Socket clientSocket = serverSocket.accept();
             InputStream inputStream = clientSocket.getInputStream()
        ) {
            System.out.println("Connected with ip address: "+clientSocket.getInetAddress());
            long i = 0;
            long sumBytesReceived = 0;
            final long startTime = System.currentTimeMillis();
            for (int line = inputStream.read(receiveSize); line != -1; line = inputStream.read(receiveSize)) {
                i++;
                sumBytesReceived += line;
                if(line != PACKET_SIZE)
                    System.out.println(i+". not "+ PACKET_SIZE + ": " +line);
            }
            final long stopTime = System.currentTimeMillis();
            System.out.println("i: "+i);
            System.out.println("Bytes received: " + sumBytesReceived);
            System.out.println("Duration: " + (stopTime - startTime));
            System.out.println("BytesPerSecond: " + sumBytesReceived / ((stopTime-startTime) / 1000));

        }
    }
}