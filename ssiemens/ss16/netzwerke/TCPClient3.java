package ssiemens.ss16.netzwerke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class TCPClient3 {
    public static final String host = "www.google.de";

    public static void main(String[] args) {
        TCPClient3 client = new TCPClient3();
        client.doRequest();
    }


    public void doRequest() {
        HttpURLConnection con;
        try {
            con = (HttpURLConnection)
                    new URL("http://" + host + "/").openConnection();
            try (

                    InputStream input = con.getInputStream();

                    BufferedReader fromServer =
                            new BufferedReader(
                                    new InputStreamReader(
                                            input))) {

                for (String line = fromServer.readLine();
                     line != null /*&& line.length()>0*/;
                     line = fromServer.readLine()) {
                    System.out.println(line);
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


    }


}
