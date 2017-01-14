package sweiss.SS16.Hammerschall_SS_13;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Nelson on 03.01.2017.
 */
public class ClientServer {

    void go(int serverport, int clientport, String message) throws IOException {
        runClient(clientport);
        runServer(serverport, message);
    }

    private void runClient(final int clientport) throws IOException {
        Thread clientThread = new Thread() {
            @Override
            public void run() {
                System.out.println("runClient started");
                try {
                    while (true) {
                        Socket clientSocket = new Socket("localhost", clientport);
                        System.out.println("socket created");
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                            String st;
                            while ((st = br.readLine()) != null) {
                                System.out.println(st);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        clientThread.start();
    }

    private void runServer(final int serverPort, final String message) throws IOException {
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(serverPort);
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("got connection from client");
                        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
                            bw.write(message);
                            bw.flush();
                            System.out.println("wrote message " + this.getName());
                        }
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }


            }
        };
        serverThread.start();
    }

    public static void main(String[] args) throws IOException {
        new ClientServer().go(2000, 2001, "Message1");
        new ClientServer().go(2001, 2000, "Message2");
    }
}

