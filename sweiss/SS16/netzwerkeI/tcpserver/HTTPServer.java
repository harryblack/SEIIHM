package sweiss.SS16.netzwerkeI.tcpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {
    public static final int PORT = 4711;
    public static final String TARGETHOST = "www.google.de";

    /**
     * @param args
     */
    public static void main(String[] args) {
        HTTPServer server = new HTTPServer();
        server.startServer();
    }

    private void startServer() {
        // Instantiate server socket that listens to a port number
        try (ServerSocket servSock = new ServerSocket(PORT)) {

            System.out.println("Server started, waiting for clients...");

            // Let ServerSocket wait for request from client
            try (Socket s = servSock.accept();
                 // Once request was received, instantiate Reader and Writer for client
                 BufferedReader fromClient =
                         new BufferedReader(
                                 new InputStreamReader(s.getInputStream()));
                 BufferedWriter toClient =
                         new BufferedWriter(
                                 new OutputStreamWriter(s.getOutputStream()))) {
                System.out.println("Got client connection!");

                // Read what the client requests
                for (String line = fromClient.readLine();
                     line != null /*&& line.length() > 0*/;
                     line = fromClient.readLine()) {
                    System.out.println("Client says: " + line);
                }


                try (Socket forwardSocket = new Socket(TARGETHOST, 80);
                     BufferedWriter toForwardHost = new BufferedWriter(new OutputStreamWriter(forwardSocket.getOutputStream()));
                     BufferedReader fromForwardHost = new BufferedReader(new InputStreamReader(forwardSocket.getInputStream()))
                ) {
                    toForwardHost.write("GET / HTTP/1.1\r\n");
                    toForwardHost.write("Host: " + TARGETHOST + "\r\n");
                    toForwardHost.write("\r\n");
                    toForwardHost.flush();

                    for (String line = fromForwardHost.readLine();
                         line != null;
                         line = fromForwardHost.readLine()) {
                        toClient.write(line + "\r\n");
                        System.out.println("writing to client: " + line);
                    }

                    //toClient.flush();
                    //toClient.close();
                }

                //String testString = "Hallo Welt!";
//
                //toClient.write("HTTP/1.0 200 OK\r\n");
                //toClient.write("Content-length: " + testString.length() + "\r\n");
                //toClient.write("\r\n");
                //toClient.write(testString);
                //toClient.flush();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
