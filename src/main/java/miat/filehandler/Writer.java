package miat.filehandler;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    public static void write(String path, String fileContent) {
        try {
            FileWriter fw = new FileWriter(path);
            fw.write(fileContent);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
