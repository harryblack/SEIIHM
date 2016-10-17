package ssiemens.ss16.netzwerke;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sascha on 17/10/2016.
 */
public class TCPServer {
    private static final int PORT = 4711;

    public static void main(String[] args) throws IOException {
        new TCPServer().startServer();

    }

    private void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            Socket clientConnetion = serverSocket.accept();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientConnetion.getInputStream()));
                 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientConnetion.getOutputStream()))) {
                System.out.println("Client says: " + bufferedReader.readLine());
                bufferedWriter.write("HTTP/1.0 200 OK\r\n\r\n" +
                "<HTML> Hallo! </HTML>");
                bufferedWriter.flush();
            }
        }
    }
}
