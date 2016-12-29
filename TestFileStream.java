import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sascha on 28/12/2016.
 */
public class TestFileStream {
    public static void main(String[] args) throws IOException {
        // check argument length
        if (args.length != 2) {
            throw new IllegalArgumentException("Need two parameters: java FileSender <filename> <ip-address or dns-hostname> .");
        }
        // parse arguments
        final String fileName = args[0];
        final String targetHost = args[1];

        // create file-object
        final Path fileToCopy = Paths.get(fileName);

        // check if file exists
        if (!Files.exists(fileToCopy))
            throw new IllegalArgumentException("File \"" + fileToCopy.getFileName() + "\" not found!");

        // get size of file (number in bytes)
        final int sizeOfFile = (int) Files.size(fileToCopy);
        if (sizeOfFile == 0) throw new IllegalArgumentException("File has zero bytes!");
        System.out.println("Transfer \"" + fileName + "\" with " + sizeOfFile + " bytes.");

        // create byte-stream of file-to-copy
        FileInputStream fileInputStream = new FileInputStream(fileName);
        byte[] bytes = new byte[1400];

        int bytesRead = fileInputStream.read(bytes);
        System.out.println(bytesRead);
        bytesRead = fileInputStream.read(bytes);
        System.out.println(bytesRead);
    }
}
