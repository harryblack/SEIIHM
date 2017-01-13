package ssiemens.ss16.netzwerke.basics;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Sascha on 17/10/2016.
 */
public class TCPClient {

    public static final String SERVER = "www.heise.de";

    static void doRequest() throws IOException {
        try (Socket s = new Socket(SERVER, 80);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
             InputStreamReader test = new InputStreamReader(s.getInputStream());
             BufferedReader reader = new BufferedReader(test)) {
            System.out.println(test.getEncoding());

            //new InputStreamReader(new URL(SERVER).openStream()



            // send get request
            writer.write("GET / HTTP/1.1 \r\n" +
                    "Host: www.heise.de \r\n\r\n");
            writer.flush();

            for (int i = 0; i < 20; i++) {
                System.out.println("Server answer: " + reader.readLine());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        doRequest();
    }
}
