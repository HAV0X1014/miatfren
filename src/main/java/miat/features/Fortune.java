package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Fortune {
    public static EmbedBuilder cowsay() {
        EmbedBuilder e = new EmbedBuilder();
        StringBuilder result = new StringBuilder();
        try {
            String[] command = {"/bin/sh", "-c", "fortune | cowsay"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            result.append("```");
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            result.append("```");
            reader.close();
            process.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.setDescription(result.toString());
        return e;
    }
}
