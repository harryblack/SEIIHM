package sweiss.SS16.netzwerkeI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sweiss.SS16.netzwerkeI.tcpclient.TCPClient1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Nelson on 10.11.2016.
 */
public class HueControl {
    public static final String host = "127.0.0.1";

    public static void main(String[] args) {
        HueControl client = new HueControl();
        client.doRequest();
    }


    public void doRequest() {
        try (Socket s = new Socket(host, 80);
             BufferedWriter toServer =
                     new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

             BufferedReader fromServer =
                     new BufferedReader(new InputStreamReader(s.getInputStream())))
        {
            toServer.write("GET /api/newdeveloper HTTP/1.1\r\n");
            toServer.write("Host: " + host + "\r\n");
            toServer.write("\r\n");
            toServer.flush();

            for (String line = fromServer.readLine();
                 !line.equals("") /*&& line.length()>0*/;
                 line = fromServer.readLine()) {
                System.out.println(line);
            }

            String JSONString = new String();
            for (String line = fromServer.readLine();
                 !line.equals("") /*&& line.length()>0*/;
                 line = fromServer.readLine()) {
                JSONString.concat(line);
            }
            System.out.println(JSONString);




        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
