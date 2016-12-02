package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPClient extends Thread {
    // ##########################
    // #### Object variables ####
    // ##########################
    private final int packetSize;          // Bytes
    private final long sendingDuration;     // Milliseconds
    private final String serverHost;        // Serverhost address or dns name
    private final int serverPort;           // Server port number
    private final long waitAfterNPackets;   // Waits after the given packet for a given time (@see waitForKMillis)
    private final long waitForKMillis;      // Waits for the given amount of time in milliseconds

    // ################
    // ### C'tor    ###
    // ################
    UDPClient(int packetSize, long sendingDuration, String serverHost, int serverPort, long waitAfterNPackets, long waitForKMillis) {
        this.packetSize = packetSize;
        this.sendingDuration = sendingDuration;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.waitAfterNPackets = waitAfterNPackets;
        this.waitForKMillis = waitForKMillis;
    }

    // ##########################
    // #### Object variables ####
    // ##########################
    private int getPacketSize() {
        return packetSize;
    }

    private long getSendingDuration() {
        return sendingDuration;
    }

    private String getServerHost() {
        return serverHost;
    }

    private int getServerPort() {
        return serverPort;
    }

    private long getWaitAfterNPackets() {
        return waitAfterNPackets;
    }

    private long getWaitForKMillis() {
        return waitForKMillis;
    }

    // ###############
    // ### Methods ###
    // ###############
    @Override
    public void run() {
        System.out.println("UDP-Client started...");
        final byte[] packetToBeSent = new byte[getPacketSize()];
        long packetsSent = 0; // Initial value
        long startTime = 0;
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket datagramPacket = new DatagramPacket(packetToBeSent, packetToBeSent.length, InetAddress.getByName(getServerHost()), getServerPort());
            startTime = System.currentTimeMillis();
            final long timeToStop = startTime + getSendingDuration();
            System.out.println("Client: Packet size: " + getPacketSize());
            System.out.println("Client: Sending duration: " + getSendingDuration());
            System.out.println("Client: Sending to: " + getServerHost() + " || On port: " + getServerPort());
            System.out.println("Client: Wait after " + getWaitAfterNPackets() + " packets for " + getWaitForKMillis() + " milliseconds");
            System.out.println("Client: Sending data - Please wait...\r\n");
            while (System.currentTimeMillis() < timeToStop) {
                datagramSocket.send(datagramPacket);
                packetsSent++;
                if (getWaitAfterNPackets() > 0) {
                    if (packetsSent % getWaitAfterNPackets() == 0) {
                        final long timeLeft = timeToStop - System.currentTimeMillis();
                        sleep(timeLeft < getWaitForKMillis() ? timeLeft : getWaitForKMillis());
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        final long realDuration = System.currentTimeMillis() - startTime;
        final long bytesTransferred = getPacketSize() * packetsSent;

        System.out.println("\nUDP CLIENT TRANSMIT FINISHED - Socket closed!");
        System.out.println("---------------------------------------------------");

        System.out.println("Client Real duration: " + realDuration + "\r\n");

        System.out.println("Client Bits sent: " + bytesTransferred * 8);
        System.out.println("Client KBits sent: " + (float)bytesTransferred * 8 / 1000);
        System.out.println("Client MBits sent: " + (float)bytesTransferred * 8 / 1000);

        System.out.println("\nClient Bytes sent: " + bytesTransferred);
        System.out.println("Client KB sent: " + (float)bytesTransferred / 1_000);
        System.out.println("Client MB sent: " + (float)bytesTransferred / 1_000_000);

        System.out.println("\nClient Bits/second: " + (bytesTransferred * 8) / ((float)getSendingDuration() / 1000));
        System.out.println("Client KBits/Second: " + ((float)bytesTransferred * 8 / 1_000) / ((float)getSendingDuration() / 1000));
        System.out.println("Client MBits/Second: " + ((float)bytesTransferred * 8 / 1_000_000) / ((float)getSendingDuration() / 1000));

        System.out.println("\nClient Bytes/second: " + bytesTransferred / ((float)getSendingDuration() / 1000));
        System.out.println("Client KB/Second: " + ((float)bytesTransferred / 1_000) / ((float)getSendingDuration() / 1000));
        System.out.println("Client MB/Second: " + ((float)bytesTransferred / 1_000_000) / ((float)getSendingDuration() / 1000));
    }

    public static void main(String[] args) {
        new UDPClient(1400,30_000, "localhost", 7777,0,0).start();
    }
}