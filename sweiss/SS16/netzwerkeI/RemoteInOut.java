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

        String testNormal = "ab ab ab";
        System.out.println(testNormal);
        testNormal = testNormal.replaceAll("ab", "f");
        System.out.println(testNormal);


        String testImg = "<img> irgendwas .. 55 </img> und noch was <img> f </img>";
        System.out.println(testImg);
        testImg = testImg.replaceAll("<img[\\S+\\s+]*</img>", "<img src=\"https://en.wikipedia.org/wiki/Munich_University_of_Applied_Sciences#/media/File:Hochschule_M%C3%BCnchen_Logo.jpg\" alt=\"mmix\" width=\"600\" height=\"82\" />");
        System.out.println(testImg);



        //String input = new Scanner(System.in).nextLine();
        //while (!input.contains("\r\n\r\n")){
        //    try (FileWriter output = new FileWriter("test.txt")) {
        //        output.write(input);
        //    }
        //    input = new Scanner(System.in).nextLine();
        //}
//
        //System.out.println("Zugriff aufgezeichnet am " + new Date());
    }
}