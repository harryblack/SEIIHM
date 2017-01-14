package sweiss.SS16.netzwerkeI.tcpserver;

import java.io.*;
import java.net.*;

/**
 * Created by Nelson on 25.10.2016.
 */
public class HTTPServNew extends Thread {

    public static void main(String[] args) throws IOException {
        HTTPServNew httpServNew;
        ServerSocket sSocket = new ServerSocket(PORT);
        while (true) {
            new HTTPServNew(sSocket.accept()).start();
        }
    }

    public final static int PORT = 8082;
    public final static String targetURL = "www.mmix.cs.hm.edu";
    private final Socket cSocket;


    public HTTPServNew(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override
    public void start() {
        System.out.println("Got connection to client!");

        try (BufferedReader fromClient = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
             BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(cSocket.getOutputStream()));
        ) {
            final String requestLine = fromClient.readLine();

            if (requestLine.startsWith("GET ")) {
                String[] partsOfRequest;
                partsOfRequest = requestLine.split(" ");
                String targetPath = partsOfRequest[1];

                System.out.println(requestLine);
                System.out.println("http://" + targetURL + targetPath);

                try {
                    HttpURLConnection connection = (HttpURLConnection)
                            new URL("http://" + targetURL + targetPath).openConnection();
                    BufferedReader fromTarget = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    {
                        String forClient = new String();
                        forClient = forClient.concat(connection.getHeaderField(0) + "\r\n\r\n");

                        String targetBody = fromTarget.readLine();
                        while (targetBody != null) {
                            //targetBody = targetBody.replaceAll("<img src=[\\S+\\s+]*/>", "<img src=\"https://en.wikipedia.org/wiki/Munich_University_of_Applied_Sciences#/media/File:Hochschule_M%C3%BCnchen_Logo.jpg\" alt=\"mmix\" width=\"600\" height=\"82\" />");

                            forClient = forClient.concat(targetBody);

                            targetBody = fromTarget.readLine();
                        }
                        //forClient.replaceAll("<img src=\"*\"", "<img src=\"https://en.wikipedia.org/wiki/Munich_University_of_Applied_Sciences#/media/File:Hochschule_M%C3%BCnchen_Logo.jpg\"");
                        toClient.write(forClient);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

