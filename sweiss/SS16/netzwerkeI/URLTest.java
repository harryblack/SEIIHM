package sweiss.SS16.netzwerkeI;

/**
 * Created by Nelson on 27.10.2016.
 */

import java.net.*;
import java.io.*;

public class URLTest {

    public final static int PORT = 8082;
    public final static String targetURL = "www.google.de";

    public static void main(String[] args) throws Exception {
        try (ServerSocket sSocket = new ServerSocket(PORT)) {
            System.out.println("Server waiting for connection");
            Socket clientSocket = sSocket.accept();
            System.out.println("Connection received");

            try (BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            ) {


                HttpURLConnection connection = (HttpURLConnection) new URL("http://" + targetURL + "/").openConnection();
                connection.setRequestMethod("GET");

                System.out.println(connection.getResponseCode());
                System.out.println(connection.getHeaderFields());

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));

                String response = new String();
                String line = in.readLine();
                while (line != null) {
                    response = response.concat(line + "\n");
                    System.out.println(line);
                    line = in.readLine();
                }


                // out.write("GET / HTTP/1.1\r\nHost: " + "www.google.de\r\n");
                // out.write("\r\n");
                // out.close();
            }

        }
    }
}