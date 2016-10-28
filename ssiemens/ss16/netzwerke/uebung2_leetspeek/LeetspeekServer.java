package ssiemens.ss16.netzwerke.uebung2_leetspeek;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Sascha on 25/10/2016.
 */
public class LeetspeekServer extends Thread {
    private static final int SERVER_PORT = 8082;
    private static final Map<String, String> replaceWordToLeedspeek = new HashMap<>();

    // Initialize the leedspeek words, which should be replaced in the html-content
    static {
        replaceWordToLeedspeek.put("MMIX", "mm1x");
        replaceWordToLeedspeek.put("Java", "j4v4");
        replaceWordToLeedspeek.put("Computer", "c0mpu73r");
        replaceWordToLeedspeek.put("RISC", "r15c");
        replaceWordToLeedspeek.put("CISC", "c15c");
        replaceWordToLeedspeek.put("Debugger", "d3bu663r");
        replaceWordToLeedspeek.put("Informatik", "1nf0rm471k");
        replaceWordToLeedspeek.put("Student", "57ud3n7");
        replaceWordToLeedspeek.put("Studentin", "57ud3n71n");
        replaceWordToLeedspeek.put("Studierende", "57ud13r3nd3");
        replaceWordToLeedspeek.put("Windows", "w1nd0w5");
        replaceWordToLeedspeek.put("Linux", "l1nux");
    }


    private final String targetHost;
    private final Socket clientSocket;

    public LeetspeekServer(Socket clientSocket, String targetHost) throws IOException {
        this.clientSocket = clientSocket;
        this.targetHost = targetHost;
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

                // ##############################
                // ### Connect to Target Host ###
                // ##############################
                try {
                    HttpURLConnection targetHostConnection = (HttpURLConnection) new URL("http://" + targetHost + requestedUrlFromClient).openConnection();
                    try (BufferedReader targetHostReader =
                                 new BufferedReader(
                                         new InputStreamReader(
                                                 (targetHostConnection).getInputStream()
                                         ))) {
                        // Get first response line with http-version + return code + return message
                        clientHttpResponse = targetHostConnection.getHeaderField(0);

                        // Get body-content (most html content)
                        for (String line = responseFromTargetHost;
                             line != null;
                             line = targetHostReader.readLine()) {
                            responseFromTargetHost = responseFromTargetHost.concat(line);
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



                if (responseFromTargetHost.contains("<html") && responseFromTargetHost.contains("html>")) {
                    // Manipulate images
                    responseFromTargetHost = responseFromTargetHost.replaceAll("<img src=\"*\"", "<img src=\"http://fi.cs.hm.edu/fi/hm-logo.png\"");

                    // Manipulate words
                    for (String originalWord : replaceWordToLeedspeek.keySet())
                        responseFromTargetHost = responseFromTargetHost.replaceAll(originalWord, replaceWordToLeedspeek.get(originalWord));
                }


                // ###############################
                // ### Send response to client ###
                // ###############################

                // Send Http-Response header
                clientWriter.println(clientHttpResponse);       // Send HTTP response code
                clientWriter.println();                         // End of HTTP response

                // Send HTML-Content
                clientWriter.println(responseFromTargetHost);
            }

        } catch (IOException e1) {      // End of client-connection
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException("Only one parameter allowed. Example: www.targethost.net");


        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server started");
        while (true) {
            new LeetspeekServer(serverSocket.accept(), args[0]).start();
        }
    }
}
