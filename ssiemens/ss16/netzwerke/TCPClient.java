package ssiemens.ss16.netzwerke;

import java.io.*;
import java.net.Socket;

/**
 * Created by Sascha on 17/10/2016.
 */
public class TCPClient {

    public static final String SERVER = "www.google.de";

    static void doRequest() throws IOException {
        try (Socket s = new Socket(SERVER, 80);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            //new InputStreamReader(new URL(SERVER).openStream()

            // send get request
            writer.write("GET / HTTP/1.1 \r\n" +
                    "Host: www.google.de \r\n\r\n");
            writer.flush();

            for (int i = 0; i < 100; i++) {
                System.out.println("Server answer: " + reader.readLine());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        doRequest();
    }
}
