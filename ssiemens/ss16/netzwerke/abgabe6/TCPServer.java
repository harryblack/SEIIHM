package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer extends Thread {
    // ##########################
    // #### Object variables ####
    // ##########################
    private final int packetSize;
    private final int serverPort;
    private final Object outputMonitor;

    // ################
    // ### C'tor    ###
    // ################
    TCPServer(int packetSize, int serverPort, Object outputMonitor) {
        this.packetSize = packetSize;
        this.serverPort = serverPort;
        this.outputMonitor = outputMonitor;
    }

    // ################
    // ### Getter   ###
    // ################
    private int getPacketSize() {
        return packetSize;
    }

    private int getServerPort() {
        return serverPort;
    }

    private Object getOutputMonitor() {
        return outputMonitor;
    }

    // ###############
    // ### Methods ###
    // ###############
    @Override
    public void run() {
        System.out.println("TCP-Server started...");
        long startTime = 0; // initial value
        long sumBytesReceived = 0; // initial value
        try {
            final byte[] receiveSize = new byte[getPacketSize()];




            try (ServerSocket serverSocket = new ServerSocket(getServerPort());
                 Socket clientSocket = serverSocket.accept();
                 InputStream inputStream = clientSocket.getInputStream()
            ) {
                System.out.println("TCP-Server: Connected with ip address: " + clientSocket.getInetAddress());
                sumBytesReceived = 0;
                startTime = System.currentTimeMillis();
                for (int line = inputStream.read(receiveSize); line != -1; line = inputStream.read(receiveSize)) {
                    sumBytesReceived += line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final long realDuration = System.currentTimeMillis() - startTime;
        synchronized (getOutputMonitor()) {
            System.out.println("\nTCP SERVER TRANSFER FINISHED - Socket closed!");
            System.out.println("---------------------------------------------------");

            System.out.println("Server Real duration: " + realDuration + "\r\n");

            System.out.println("Server Bits received: " + sumBytesReceived * 8);
            System.out.println("Server KBits received: " +(float) sumBytesReceived * 8 / 1000);
            System.out.println("Server MBits received: " + (float)sumBytesReceived * 8 / 1000);

            System.out.println("\nServer Bytes received: " + sumBytesReceived);
            System.out.println("Server KB received: " + (float)sumBytesReceived / 1_000);
            System.out.println("Server MB received: " + (float)sumBytesReceived / 1_000_000);

            System.out.println("\nServer Bits/second: " + (sumBytesReceived * 8) / ((float)realDuration / 1000));
            System.out.println("Server KBits/Second: " + ((float)sumBytesReceived * 8 / 1_000) / ((float)realDuration / 1000));
            System.out.println("Server MBits/Second: " + ((float)sumBytesReceived * 8 / 1_000_000) / ((float)realDuration / 1000));

            System.out.println("\nServer Bytes/second: " + sumBytesReceived / (realDuration / 1000));
            System.out.println("Server KB/Second: " + ((float)sumBytesReceived / 1_000) / ((float)realDuration / 1000));
            System.out.println("Server MB/Second: " + ((float)sumBytesReceived / 1_000_000) / ((float)realDuration / 1000));
        }
    }
}