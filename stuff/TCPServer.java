package stuff;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer extends Thread {
    // ##########################
    // #### Object variables ####
    // ##########################
    private final int packetSize = 1400;
    private final int serverPort;

    // ################
    // ### C'tor    ###
    // ################
    TCPServer(int serverPort) {
        this.serverPort = serverPort;
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


    // ###############
    // ### Methods ###
    // ###############
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getServerPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TCP-Server started...");
        while (true) {
            long startTime = 0; // initial value
            long sumBytesReceived = 0; // initial value
            try {
                final byte[] receiveSize = new byte[getPacketSize()];

                System.out.println("TCP-Server: Waiting for client connection...");
                try (
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
            System.out.println("\nTCP SERVER TRANSFER FINISHED - Socket closed!");
            System.out.println("---------------------------------------------------");
            System.out.println("Server Real duration: " + realDuration + "\r\n");
            System.out.println("\nServer Bytes received: " + sumBytesReceived);
            System.out.println("Server KBits/Second: " + ((float) sumBytesReceived * 8 / 1_000) / ((float) realDuration / 1000));
            System.out.println("Server MB/Second: " + ((float) sumBytesReceived / 1_000_000) / ((float) realDuration / 1000));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Use following parameters: java TCPServer <port>");
            throw new IllegalArgumentException("Invalid parameters");
        }

        final int serverPort = Integer.parseInt(args[0]);
        TCPServer test = new TCPServer(serverPort);
        test.start();
    }
}