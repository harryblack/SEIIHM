package ssiemens.ss16.netzwerke.abgabe9;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class BroadcastToAllInterfaces {
    static void sendBroadcastMessage(String broadcastMessage, int port) throws IOException {
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        final DatagramSocket udpSocket = new DatagramSocket(port);
        while (interfaces.hasMoreElements()) {
            final NetworkInterface intf = interfaces.nextElement();
            if (!intf.isLoopback()) {
                final List<InterfaceAddress> addresses = intf.getInterfaceAddresses();
                for (InterfaceAddress address : addresses) {
                    if (address.getAddress() != null && address.getBroadcast() != null) {
                        System.out.println("Broadcast-IP: " + address.getBroadcast().getHostAddress());
                        DatagramPacket broadcastToSend = new DatagramPacket(broadcastMessage.getBytes(), broadcastMessage.length(), address.getBroadcast(), port);
                        udpSocket.send(broadcastToSend);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BroadcastToAllInterfaces.sendBroadcastMessage("Hello Broadcast-Domain", 8888);
    }
}
