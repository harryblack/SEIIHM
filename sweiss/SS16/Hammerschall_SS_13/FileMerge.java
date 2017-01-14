package sweiss.SS16.Hammerschall_SS_13;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Nelson on 08.12.2016.
 */
public class FileMerge {
    static void fileMerge(String outputFile, ArrayList<String> inputFiles) throws IOException {
        try (
                FileWriter fileWriter = new FileWriter(outputFile, true);
        ) {
            for (int i = 0; i < inputFiles.size(); i++) {
                try (
                        FileReader fileReader = new FileReader(inputFiles.get(i));
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                ) {
                    String line = bufferedReader.readLine();
                    fileWriter.write(line);
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        String out = "output.txt";
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("eins.txt");
        stringArrayList.add("zwei.txt");
        stringArrayList.add("drei.txt");

        fileMerge(out, stringArrayList);

    }
}
