package sweiss.SS16.netzwerkeI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by Sascha on 11/10/2016.
 */
public class RemoteInOut {
    public static void main(String[] args) throws IOException {
        String input = new Scanner(System.in).nextLine();
        while (!input.contains("\r\n\r\n")){
            try (FileWriter output = new FileWriter("test.txt")) {
                output.write(input);
            }
            input = new Scanner(System.in).nextLine();
        }

        System.out.println("Zugriff aufgezeichnet am " + new Date());
    }
}