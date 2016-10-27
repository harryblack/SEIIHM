package ssiemens.ss16.netzwerke;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by Sascha on 11/10/2016.
 */
public class Uebung1_Ausgabe {
    public static void main(String[] args) throws IOException {


        try (Scanner scanner = new Scanner(System.in);
             FileWriter output = new FileWriter("/home/network/Desktop/output.txt")) {
            String input = scanner.nextLine();
            while (!input.equals("")) {
                output.write(input + "\r\n");
                input = scanner.nextLine();
            }
            output.write("----------- \r\n");
            output.flush();
            input = scanner.nextLine();
            output.write(input + "\r\n");
        }

        System.out.println("Zugriff aufgezeichnet am " + new Date());
    }



}
