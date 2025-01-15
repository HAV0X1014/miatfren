package miat.filehandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Reader {
    public static String readFull(String filePath) {
        StringBuilder fullText = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fullText.toString();
    }

    public static String getRandomLine(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();

            Random random = new Random();

            return lines.get(random.nextInt(lines.size()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFirstLine(String filepath) {
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
