import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient extends Thread {
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
    TCPClient(String serverHost, int serverPort, long sendingDuration, long waitAfterNPackets, long waitForKMillis) {
        this.sendingDuration = sendingDuration;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.waitAfterNPackets = waitAfterNPackets;
        this.waitForKMillis = waitForKMillis;
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
        System.out.println("\nTCP CLIENT TRANSMIT FINISHED - Socket closed!");
        System.out.println("---------------------------------------------------");
        System.out.println("Client Real duration: " + realDuration + "\r\n");
        System.out.println("\nClient Bytes sent: " + bytesTransferred);
        System.out.println("Client KBits/Second: " + ((float) bytesTransferred * 8 / 1_000) / ((float) getSendingDuration() / 1000));
        System.out.println("Client MB/Second: " + ((float) bytesTransferred / 1_000_000) / ((float) getSendingDuration() / 1000));
    }

    public static void main(String... args) {
        if (args.length != 5){
            System.out.println("Use following parameters: java TCPClient <destination-ip or name> <port> <sending-duration[ms]> <wait after packet count> <for a amount of milliseconds>");
            throw new IllegalArgumentException("Invalid parameters!");
        }

        final String serverHost = args[0];
        final int serverPort = Integer.parseInt(args[1]);
        final int sendingDuration = Integer.parseInt(args[2]);
        final int waitAfterNPackets = Integer.parseInt(args[3]);
        final int waitForKMillis = Integer.parseInt(args[4]);

        new TCPClient(serverHost, serverPort, sendingDuration, waitAfterNPackets, waitForKMillis).start();
    }
}
