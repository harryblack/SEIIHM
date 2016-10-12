package ssiemens.ss16.netzwerke;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by Sascha on 11/10/2016.
 */
public class Uebung1_Ausgabe {
    public static void main(String[] args) throws IOException {
        String input = new Scanner(System.in).next();
        try (FileWriter output = new FileWriter("test.txt")) {
            output.write(input);
        }
        System.out.println(String.format("Zugriff aufgezeichnet am " + new Date()));
    }
}
