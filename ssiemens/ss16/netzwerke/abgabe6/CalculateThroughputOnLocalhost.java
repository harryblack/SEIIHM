package ssiemens.ss16.netzwerke.abgabe6;

public class CalculateThroughputOnLocalhost {
    private static final long WAIT_FOR_UDP_SERVER_TIMEOUT = 1_000;      // Timeout value after the udp server closes the connection if no more packets are received

    public static void main(String[] args) throws InterruptedException {
        // ########################################
        // ### Change parameters to your needs ###
        // ########################################
        final boolean useTCP = true;          // true: use TCP. false: use UDP
        final long sendingDuration = 10_000;    // Milliseconds
        final int packetSize = 1400;            // Bytes
        final long waitAfterNPackets = 0;       // Waits after the given packet number for a given time (see waitForKMillis)
        final long waitForKMillis = 0;          // Waits for the given amount of time in milliseconds after every waitAfterNPackets value (ignored if waitAfterNPackets = 0)

        final String serverHost = "localhost";  // Server host address or dns name
        final int serverPort = 7777;            // Server port number
        final Object outputMonitor = new Object(); // Monitor for synchronized output for tcp connections (sugar only - no technical need)
        // ------------------------------------------------------------------------------------


        // ########################################
        // ### DO NOT CHANGE                    ###
        // ########################################
        for (int i=0;i<10;i++) {
            if (useTCP) { // Start TCP throughput test
                final TCPServer tcpServer = new TCPServer(packetSize, serverPort, outputMonitor);
                final TCPClient tcpClient = new TCPClient(packetSize, sendingDuration, serverHost, serverPort, waitAfterNPackets, waitForKMillis, outputMonitor);
                tcpServer.start();
                while (!tcpServer.isAlive())
                    Thread.sleep(10);
                tcpClient.start();
                tcpClient.join();
                tcpServer.join();
            } else { // Start UDP throughput test
                final UDPServer udpServer = new UDPServer(packetSize, WAIT_FOR_UDP_SERVER_TIMEOUT, serverPort);
                final UDPClient udpClient = new UDPClient(packetSize, sendingDuration, serverHost, serverPort, waitAfterNPackets, waitForKMillis);
                udpServer.start();
                while (!udpServer.isAlive())
                    Thread.sleep(10);
                udpClient.start();
                udpClient.join();
                udpServer.join();
            }
        }
    }
}
