package sweiss.SS16.Hammerschall_SS_13;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nelson on 02.01.2017.
 */
public class Part {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 2000);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
        }
    }
}
