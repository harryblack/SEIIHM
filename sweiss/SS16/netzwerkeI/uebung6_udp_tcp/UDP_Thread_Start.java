package sweiss.SS16.netzwerkeI.uebung6_udp_tcp;

import java.io.IOException;

/**
 * Created by Nelson on 28.11.2016.
 */
public class UDP_Thread_Start {
    public static void main(String[] args) throws IOException, InterruptedException {
        new Server_UDP().start();
        Thread.sleep(500);
        new Client_UDP().start();
    }
}
