package ssiemens.ss16.netzwerke.uebung7_filetransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by Nelson on 22.12.2016.
 */
public class DoReceiveFiles {
    //****************************************************
    // File format used: "<CRC32> <Hi!> <size> <filename>"
    //                    <4Byte> <xBy> <xBy>   <xBytes>

    public static void main(String[] args) throws IOException {
        FileReceiver fileReceiver = new FileReceiver();
        boolean socketTimeout = false;
        Long checksumPacketReceived;
        Checksum calculatedChecksum = new CRC32();


        fileReceiver.packetReceived = new DatagramPacket(fileReceiver.packetAsBytes, fileReceiver.packetAsBytes.length);
        fileReceiver.socket.setSoTimeout(10_000);

        while (!socketTimeout) try {
            fileReceiver.socket.receive(fileReceiver.packetReceived);
            System.out.println("received something");
            // Checksum received with packet

            checksumPacketReceived = new Long(new String(Arrays.copyOfRange(fileReceiver.packetAsBytes, 0, 4)));
            // Checksum calculated based on data received
            calculatedChecksum.update(fileReceiver.packetAsBytes, 4, fileReceiver.packetAsBytes.length - 4);

            // Compare original checksum and new checksum
            if (checksumPacketReceived == calculatedChecksum.getValue()) {
                if (new String(Arrays.copyOfRange(fileReceiver.packetAsBytes, 4, 7)).startsWith("Hi!")) {
                    System.out.println("got hi");
                    socketTimeout = true;
                    fileReceiver.processMsg(FileReceiver.Msg.GOTHI);
                }
            }

        } catch (SocketTimeoutException s) {
            socketTimeout = true;
        }


/*

        // Establish connection
        while (!gotInitialHi) try {
            fileReceiver.socket.setSoTimeout(10_000);
            fileReceiver.socket.receive(fileReceiver.packetReceived);
            if (new String(fileReceiver.packetReceived.getData()).startsWith("Hi!")) {
                gotInitialHi = true;
                fileReceiver.currentConnectionSender = fileReceiver.packetReceived.getAddress();
                fileReceiver.bytes = fileReceiver.packetReceived.getData();
                fileReceiver.packetToSend = new DatagramPacket(fileReceiver.bytes, fileReceiver.bytes.length, fileReceiver.currentConnectionSender, 7777);
                fileReceiver.socket.send(fileReceiver.packetToSend);
            }
        } catch (SocketTimeoutException s) {
            return;
        }

        while (!gotFirstDataPacket) try {
            fileReceiver.socket.setSoTimeout(10_000);
            fileReceiver.socket.receive(fileReceiver.packetReceived);
            if (!new String(fileReceiver.packetReceived.getData()).startsWith("Hi!")) {
                // store info locally and send ack
                gotFirstDataPacket = true;
            } else {
                fileReceiver.packetToSend = new DatagramPacket(fileReceiver.bytes, fileReceiver.bytes.length, fileReceiver.currentConnectionSender, 7777);
                fileReceiver.socket.send(fileReceiver.packetToSend);
            }
        } catch (SocketTimeoutException s) {
            return;
        }

        while (!socketTimeout)
            try {
                fileReceiver.socket.setSoTimeout(5_000);
                fileReceiver.socket.receive(fileReceiver.packetReceived);

            } catch (SocketTimeoutException s) {
                socketTimeout = true;
            }


        while (!socketTimeout)
            try {
                // Set socket timeout and wait for file to receive
                fileReceiver.socket.setSoTimeout(10_000);
                fileReceiver.socket.receive(fileReceiver.packetReceived);

                System.out.println("Packet length: " + fileReceiver.packetReceived.getLength());
                System.out.println("Packet port: " + fileReceiver.packetReceived.getPort());

                fileReceiver.bytes = fileReceiver.packetReceived.getData();
                System.out.println("Received byte[] as string: " + new String(fileReceiver.bytes));

                // Check for sequence number in packet where sequence number is final byte of byte[]
                fileReceiver.processMsg(FileReceiver.Msg.GOTSEQ0);
                fileReceiver.processMsg(fileReceiver.bytes[0] == 0 ? FileReceiver.Msg.GOTSEQ0 : FileReceiver.Msg.GOTSEQ1);

                // send ack as response to received data
                fileReceiver.socket.send(fileReceiver.packetToSend);

            } catch (SocketTimeoutException s) {
                socketTimeout = true;
            }
        */
    }
}
