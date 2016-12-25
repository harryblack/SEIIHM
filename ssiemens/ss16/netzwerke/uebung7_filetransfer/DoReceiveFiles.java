package ssiemens.ss16.netzwerke.uebung7_filetransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
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
        int checksumPacketReceived;
        byte[] tmpChecksum;
        String codeString;
        Checksum calculatedChecksum = new CRC32();

        fileReceiver.packetReceived = new DatagramPacket(fileReceiver.packetAsBytes, fileReceiver.packetAsBytes.length);

        fileReceiver.socket.setSoTimeout(10_000);

        while (!fileReceiver.socketTimeout) try {
            fileReceiver.socket.receive(fileReceiver.packetReceived);
            fileReceiver.currentPacketLength = fileReceiver.packetReceived.getLength();
            System.out.println("Length of received packet: " + fileReceiver.currentPacketLength);

            // Checksum received with packet
            tmpChecksum = Arrays.copyOfRange(fileReceiver.packetAsBytes, 0, 4);

            System.out.println("received checksum byte[]: " + Arrays.toString(tmpChecksum));
            checksumPacketReceived = ByteBuffer.wrap(tmpChecksum).getInt();
            System.out.println("received checksum int: " + checksumPacketReceived);
            System.out.println("Full data including chksum: " + Arrays.toString(fileReceiver.packetAsBytes));


            // Checksum calculated based on data received
            byte[] receivedDataBytes = Arrays.copyOfRange(fileReceiver.packetAsBytes, 4, fileReceiver.currentPacketLength);
            System.out.println("Received data bytes: " + Arrays.toString(receivedDataBytes));

            calculatedChecksum.update(receivedDataBytes, 0, receivedDataBytes.length);
            System.out.println("Calculated checksum: " + (int) calculatedChecksum.getValue());

            // Compare original checksum and new checksum
            if (checksumPacketReceived == (int) calculatedChecksum.getValue()) {
                System.out.println("checksum passt: " + Arrays.toString(tmpChecksum));
                codeString = new String(Arrays.copyOfRange(fileReceiver.packetAsBytes, 4, 7));

                if (codeString.startsWith("Hi!")) {
                    System.out.println("INFO Code is Hi!");
                    fileReceiver.socketTimeout = true;
                    fileReceiver.processMsg(FileReceiver.Msg.GOTHI);
                } else if (codeString.startsWith("0")) {
                    System.out.println("INFO Code is 0");

                    fileReceiver.processMsg(FileReceiver.Msg.GOTSEQ0);
                } else if (codeString.startsWith("1")) {
                    System.out.println("INFO Code is 1");

                    fileReceiver.processMsg(FileReceiver.Msg.GOTSEQ1);
                } else fileReceiver.processMsg(FileReceiver.Msg.GOTOTHERDATA);
            }

        } catch (SocketTimeoutException s) {
            fileReceiver.socketTimeout = true;
        }


    }
}
