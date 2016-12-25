package stuff;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPClient extends Thread {
    // ##########################
    // #### Object variables ####
    // ##########################
    private final int packetSize = 1400;          // Bytes
    private final long sendingDuration;     // Milliseconds
    private final String serverHost;        // Serverhost address or dns name
    private final int serverPort;           // Server port number
    private final long waitAfterNPackets;   // Waits after the given packet for a given time (@see waitForKMillis)
    private final long waitForKMillis;      // Waits for the given amount of time in milliseconds

    // ################
    // ### C'tor    ###
    // ################
    UDPClient(String serverHost, int serverPort, long sendingDuration, long waitAfterNPackets, long waitForKMillis) {
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
            long timeSinceLastWait = startTime;
            while (System.currentTimeMillis() < timeToStop) {
                datagramSocket.send(datagramPacket);
                packetsSent++;
                if (getWaitAfterNPackets() > 0) {
                    if (packetsSent % getWaitAfterNPackets() == 0) {
                        final long timeLeft = timeToStop - System.currentTimeMillis();
                        long timeToWait = getWaitForKMillis() - (System.currentTimeMillis()-timeSinceLastWait);
                        sleep(timeLeft < getWaitForKMillis() ? timeLeft : timeToWait);
                        timeSinceLastWait = System.currentTimeMillis();
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
        System.out.println("\nClient Bytes sent: " + bytesTransferred);
        System.out.println("Client KBits/Second: " + ((float) bytesTransferred * 8 / 1_000) / ((float) getSendingDuration() / 1000));
        System.out.println("Client MB/Second: " + ((float) bytesTransferred / 1_000_000) / ((float) getSendingDuration() / 1000));
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Use following parameters: java UDPClient <server-ip or dns name> <port> <sending-duration> <wait after packet count> <for a amount of milliseconds>");
            throw new IllegalArgumentException("Invalid parameters");
        }

        final String serverHost = args[0];
        final int serverPort = Integer.parseInt(args[1]);
        final int sendingDuration = Integer.parseInt(args[2]);
        final int waitAfterNPackets = Integer.parseInt(args[3]);
        final int waitForKMillis = Integer.parseInt(args[4]);

        new UDPClient(serverHost, serverPort, sendingDuration, waitAfterNPackets, waitForKMillis).start();
    }
}