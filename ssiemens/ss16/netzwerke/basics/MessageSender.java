package ssiemens.ss16.netzwerke.basics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MessageSender {
    public static final String TARGET_ADDRESS = "127.0.0.1";
    public static final int LOCAL_PORT = 4711;
    // test on local system:
    public static final int TARGET_PORT = LOCAL_PORT;

    public static void main(String[] args) {
        MessageSender client = new MessageSender();
        client.sendMessage("Das Pferd frisst keinen Gurkensalat.");
    }

    public void sendMessage(String message) {

        try (Socket s = new Socket(TARGET_ADDRESS, TARGET_PORT, null, 7777);
             BufferedWriter toServer = new BufferedWriter(
                     new OutputStreamWriter(s.getOutputStream()))) {
            toServer.write(message + "\r\n");
            toServer.write("\r\n");
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}