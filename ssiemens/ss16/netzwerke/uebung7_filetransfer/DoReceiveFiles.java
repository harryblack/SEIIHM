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
        boolean socketTimeout = false;
        int checksumPacketReceived;
        byte[] tmpChecksum = null;
        String codeString = new String();
        Checksum calculatedChecksum = new CRC32();

//fileReceiver.packetToSend = new DatagramPacket()
        fileReceiver.packetReceived = new DatagramPacket(fileReceiver.packetAsBytes, fileReceiver.packetAsBytes.length);

        fileReceiver.socket.setSoTimeout(10_000);

        while (!socketTimeout) try {
            fileReceiver.socket.receive(fileReceiver.packetReceived);
            fileReceiver.currentPacketLength = fileReceiver.packetReceived.getLength();
            System.out.println("Length of received packet: " + fileReceiver.packetReceived.getLength());
            System.out.println("received something");
            // Checksum received with packet

            tmpChecksum = Arrays.copyOfRange(fileReceiver.packetAsBytes, 0, 4);

            System.out.println("received checksum byte[]: " + Arrays.toString(tmpChecksum));
            checksumPacketReceived = ByteBuffer.wrap(tmpChecksum).getInt();
            System.out.println("received checksum int: " + checksumPacketReceived);
            System.out.println("Full data including chksum: " + Arrays.toString(fileReceiver.packetAsBytes));


            // Checksum calculated based on data received
            byte[] receivedDataBytes = Arrays.copyOfRange(fileReceiver.packetAsBytes, 4, 27);
            System.out.println("Received data bytes: " + Arrays.toString(receivedDataBytes));

            calculatedChecksum.update(receivedDataBytes, 0, receivedDataBytes.length);
            System.out.println((int) calculatedChecksum.getValue());


            // Compare original checksum and new checksum
            if (checksumPacketReceived == (int) calculatedChecksum.getValue()) {
                System.out.println("checksum passt: " + Arrays.toString(tmpChecksum));
                codeString = new String(Arrays.copyOfRange(fileReceiver.packetAsBytes, 4, 7));
                if (codeString.startsWith("Hi!")) {
                    System.out.println("INFO Received Hi!");
                    socketTimeout = true;
                    fileReceiver.processMsg(FileReceiver.Msg.GOTHI);
                }
                else if(codeString.startsWith("0")) {

                }
                else if(codeString.startsWith("1")) {

                }
                else fileReceiver.processMsg(FileReceiver.Msg.GOTOTHERDATA);
            }

        } catch (SocketTimeoutException s) {
            socketTimeout = true;
        }



    }
}
