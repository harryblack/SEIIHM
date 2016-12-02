package ssiemens.ss16.netzwerke.abgabe6;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient extends Thread {
    // ##########################
    // #### Object variables ####
    // ##########################
    private final int packetSize;          // Bytes
    private final long sendingDuration;     // Milliseconds
    private final String serverHost;        // Serverhost address or dns name
    private final int serverPort;           // Server port number
    private final long waitAfterNPackets;   // Waits after the given packet for a given time (@see waitForKMillis)
    private final long waitForKMillis;      // Waits for the given amount of time in milliseconds
    private final Object outputMonitor;

    // ################
    // ### C'tor    ###
    // ################
    TCPClient(int packetSize, long sendingDuration, String serverHost, int serverPort, long waitAfterNPackets, long waitForKMillis) {
        this.packetSize = packetSize;
        this.sendingDuration = sendingDuration;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.waitAfterNPackets = waitAfterNPackets;
        this.waitForKMillis = waitForKMillis;
        this.outputMonitor = this;
    }

    TCPClient(int packetSize, long sendingDuration, String serverHost, int serverPort, long waitAfterNPackets, long waitForKMillis, Object outputMonitor) {
        this.packetSize = packetSize;
        this.sendingDuration = sendingDuration;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.waitAfterNPackets = waitAfterNPackets;
        this.waitForKMillis = waitForKMillis;
        this.outputMonitor = outputMonitor;
    }

    // ################
    // ### Getter   ###
    // ################
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

    private Object getOutputMonitor() {
        return outputMonitor;
    }

    // ###############
    // ### Methods ###
    // ###############
    @Override
    public void run() {
        System.out.println("TCP-Client started...");

        final byte[] dataToSent = new byte[getPacketSize()];
        // initial values
        long packetsSentCounter = 0;
        long startTime = 0;
        try (Socket clientSocket = new Socket(getServerHost(), getServerPort());
             OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            startTime = System.currentTimeMillis();
            final long timeToStop = startTime + getSendingDuration();
            while (System.currentTimeMillis() < timeToStop) {
                outputStream.write(dataToSent);
                packetsSentCounter++;
                if (getWaitAfterNPackets() > 0) {
                    if (packetsSentCounter % getWaitAfterNPackets() == 0) {
                        final long timeLeft = timeToStop - System.currentTimeMillis();
                        sleep(timeLeft < getWaitForKMillis() ? timeLeft : getWaitForKMillis());
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        final long realDuration = System.currentTimeMillis() - startTime;
        final long bytesTransferred = getPacketSize() * packetsSentCounter;

        synchronized (getOutputMonitor()) {
            System.out.println("\nTCP CLIENT TRANSMIT FINISHED - Socket closed!");
            System.out.println("---------------------------------------------------");
            System.out.println("Client Real duration: " + realDuration + "\r\n");

            System.out.println("Client Bits sent: " + bytesTransferred * 8);
            System.out.println("Client KBits sent: " + (float)bytesTransferred * 8 / 1000);
            System.out.println("Client MBits sent: " + (float)bytesTransferred * 8 / 1000);

            System.out.println("\nClient Bytes sent: " + bytesTransferred);
            System.out.println("Client KB sent: " + (float)bytesTransferred / 1_000);
            System.out.println("Client MB sent: " + (float)bytesTransferred / 1_000_000);

            System.out.println("\nClient Bits/second: " + ((float)bytesTransferred * 8) / ((float)getSendingDuration() / 1000));
            System.out.println("Client KBits/Second: " + ((float)bytesTransferred * 8 / 1_000) / ((float)getSendingDuration() / 1000));
            System.out.println("Client MBits/Second: " + ((float)bytesTransferred * 8 / 1_000_000) / ((float)getSendingDuration() / 1000));

            System.out.println("\nClient Bytes/second: " + bytesTransferred / (getSendingDuration() / 1000));
            System.out.println("Client KB/Second: " + ((float)bytesTransferred / 1_000) / ((float)getSendingDuration() / 1000));
            System.out.println("Client MB/Second: " + ((float)bytesTransferred / 1_000_000) / ((float)getSendingDuration() / 1000));
        }
    }

    public static void main(String[] args) {
         new TCPClient(1400, 30_000, "localhost", 7777, 0, 0, new Object()).start();
    }
}
