package sweiss.SS16.netzwerkeI.uebung6_udp_tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by Nelson on 28.11.2016.
 */
public class Server_TCP {
    public static void main(String[] args) throws IOException {
        long bytesReceived = 0;
        char[] chars = new char[1400];
        boolean timedOut = false;

        long startTime = System.currentTimeMillis();
        ServerSocket serverSocket = new ServerSocket(7777);
        System.out.println("Server ready...");
        Socket fromClient = serverSocket.accept();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fromClient.getInputStream()));
        while (!serverSocket.isClosed()) {
            try {
                serverSocket.setSoTimeout(5000);
                String result = bufferedReader.readLine();
                serverSocket.setSoTimeout(0);
                bytesReceived += 1400;
            } catch (SocketTimeoutException s) {
                System.out.println("Transfer rate in kbit/s: " + ((bytesReceived * 0.008) / ((System.currentTimeMillis() - startTime) / 1000)));
                System.out.println("Server packets received: " + bytesReceived / 1400);
                serverSocket.close();
            }
            //catch (IOException e) {
            //    e.printStackTrace();
            //}
            //timedOut = true;
        }

    }
}
