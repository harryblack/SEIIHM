package stuff;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

class UDPServer extends Thread {
    // ##########################
    // #### Object variables ####
    // ##########################
    private final int packetSize = 1400;           // Bytes
    private final long waitForTimeout = 1500;     // Milliseconds
    private final int serverPort;

    // ################
    // ### C'tor    ###
    // ################
    UDPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    // ################
    // ### Getter   ###
    // ################


    private int getPacketSize() {
        return packetSize;
    }

    private long getWaitForTimeout() {
        return waitForTimeout;
    }

    private int getServerPort() {
        return serverPort;
    }

    // ###############
    // ### Methods ###
    // ###############
    @Override
    public void run() {
        System.out.println("UDP-Server started...");

        // Expected data size to receive
        final byte[] receiveSize = new byte[getPacketSize()];
        final DatagramPacket receiveData = new DatagramPacket(receiveSize, receiveSize.length);
        while (true) {
            long sumBytesReceived = 0;
            long startTime = -1;         // Initial value only
            try (DatagramSocket udpSocket = new DatagramSocket(getServerPort())) {
                System.out.println("Server: Waiting for transmitting client...");
                udpSocket.receive(receiveData);     // First wait for data
                System.out.println("Server: Connected with client: " + receiveData.getAddress() + ". Reading data - Please wait...");
                startTime = System.currentTimeMillis();
                udpSocket.setSoTimeout((int) getWaitForTimeout());
                sumBytesReceived += receiveData.getLength();
                while (true) {
                    udpSocket.receive(receiveData);
                    sumBytesReceived += receiveData.getLength();
                }
            } catch (SocketTimeoutException e) {
                final long realDuration = System.currentTimeMillis() - getWaitForTimeout() - startTime;

                System.out.println("\nUDP SERVER TRANSFER FINISHED - Socket closed!");
                System.out.println("---------------------------------------------------");
                System.out.println("Server Real duration: " + realDuration + "\r\n");
                System.out.println("\nServer Bytes received: " + sumBytesReceived);
                System.out.println("Server KBits/Second: " + ((float) sumBytesReceived * 8 / 1_000) / ((float) realDuration / 1000));
                System.out.println("Server MB/Second: " + ((float) sumBytesReceived / 1_000_000) / ((float) realDuration / 1000));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Use following parameters: java UDPServer <port>");
            throw new IllegalArgumentException("Invalid parameters");
        }

        final int serverPort = Integer.parseInt(args[0]);
        new UDPServer(serverPort).start();
    }
}
