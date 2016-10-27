package ssiemens.ss16.netzwerke.uebung2_leetspeek;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Sascha on 25/10/2016.
 */
public class LeetspeekServer extends Thread {

    private static final int SERVER_PORT = 8082;
    private static final String TARGET_HOST = "www.sueddeutsche.de";

    private final Socket clientSocket;

    public LeetspeekServer(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (Socket c = clientSocket;
             BufferedReader clientReader = new BufferedReader(new InputStreamReader(c.getInputStream()));
             PrintWriter clientWriter = new PrintWriter(c.getOutputStream())
        ) {
            // Get first line of HTTP-Request from Client
            final String firstLineOfClientRequest = clientReader.readLine();

            if (firstLineOfClientRequest.startsWith("GET ")) {
                // Get requested URL
                final int urlStartIndex = 4; // Url Starts after --->    "GET "   <-- 4 charachters
                final int urlEndIndex = firstLineOfClientRequest.lastIndexOf(" ");
                final String requestedUrlFromClient = firstLineOfClientRequest.substring(urlStartIndex, urlEndIndex);
                String clientHttpResponse = "";
                String responseFromTargetHost = "";

                try {
                    HttpURLConnection targetHostConnection = (HttpURLConnection) new URL("http://" + TARGET_HOST + requestedUrlFromClient).openConnection();
                    try (BufferedReader targetHostReader =
                                 new BufferedReader(
                                         new InputStreamReader(
                                                 (targetHostConnection).getInputStream()
                                         ))) {

                        clientHttpResponse = targetHostConnection.getHeaderField(0); // Get first response line with http-version + return code + return message

                        responseFromTargetHost = targetHostReader.readLine();      // first line from response-body
                        if (responseFromTargetHost.contains("<html") || responseFromTargetHost.contains("html>")) {

                            for (String line = responseFromTargetHost;
                                 line != null;
                                 line = targetHostReader.readLine()) {
                                responseFromTargetHost = responseFromTargetHost.concat(line);
                            }

                        }
                    }


                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } // End of target-host connection


                // #############################################
                // ### Manipulate images and leetspeek words ###
                // #############################################

                // Manipulate images
                responseFromTargetHost = responseFromTargetHost.replaceAll("<img src=\"*\"", "<img src=\"http://fi.cs.hm.edu/fi/hm-logo.png\"" );

                // Manipulate words

                Map<String, String> replaceWordToLeedspeek = new HashMap<>();
                replaceWordToLeedspeek.put("MMIX", "mm1x");
                replaceWordToLeedspeek.put("Java", "j4v4");
                replaceWordToLeedspeek.put("Computer","c0mpu73r");
                replaceWordToLeedspeek.put("RISC", "r15c");
                replaceWordToLeedspeek.put("CISC", "c15c");
                replaceWordToLeedspeek.put("Debugger", "d3bu663r");
                replaceWordToLeedspeek.put("Informatik", "1nf0rm471k");
                replaceWordToLeedspeek.put("Student", "57ud3n7");
                replaceWordToLeedspeek.put("Studentin", "57ud3n71n");
                replaceWordToLeedspeek.put("Studierende", "57ud13r3nd3");
                replaceWordToLeedspeek.put("Windows", "w1nd0w5");
                replaceWordToLeedspeek.put("Linux", "l1nux");

                for (String originalWord : replaceWordToLeedspeek.keySet())
                    responseFromTargetHost = responseFromTargetHost.replaceAll(originalWord, replaceWordToLeedspeek.get(originalWord));

                // ###############################
                // ### Send response to client ###
                // ###############################


                // Send Http-Response header
                clientWriter.println(clientHttpResponse);
                clientWriter.println();

                // Send HTML-Content
                clientWriter.println(responseFromTargetHost);
                System.out.println(responseFromTargetHost);
            }


        } catch (IOException e1) {      // End of client-connection
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server started");
        while (true) {
            new LeetspeekServer(serverSocket.accept()).start();
        }
    }
}
