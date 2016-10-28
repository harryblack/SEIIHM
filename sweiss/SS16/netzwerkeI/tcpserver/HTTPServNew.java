package sweiss.SS16.netzwerkeI.tcpserver;

import java.io.*;
import java.net.*;

/**
 * Created by Nelson on 25.10.2016.
 */
public class HTTPServNew {

    public final static int PORT = 8082;
    public final static String targetURL = "www.mmix.cs.hm.edu";


    public static void main(String[] args) {
        HTTPServNew httpServNew = new HTTPServNew();
        httpServNew.startServer();
    }

    private void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Got connection to client!");


                try (BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                ) {
                    String requestLine = fromClient.readLine();

                    if (requestLine.startsWith("GET ")) {
                        String[] partsOfRequest;
                        partsOfRequest = requestLine.split(" ");
                        String targetPath = partsOfRequest[1];

                        System.out.println(requestLine);

                        try {
                            HttpURLConnection connection = (HttpURLConnection)
                                    new URL("http://" + targetURL + targetPath).openConnection();
                            BufferedReader fromTarget = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            {
                                String forClient = new String();
                                forClient = forClient.concat(connection.getHeaderField(0) + "\r\n\r\n");

                                String targetBody = fromTarget.readLine();
                                while (targetBody != null) {
                                    forClient = forClient.concat(targetBody);
                                    targetBody = fromTarget.readLine();
                                }
                                toClient.write(forClient);
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
