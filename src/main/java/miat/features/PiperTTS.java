package miat.features;

import org.javacord.api.event.message.MessageCreateEvent;

import java.io.*;

public class PiperTTS {
    public static void say(String text, MessageCreateEvent mc) {
        try {
        text = text.replace("'", "").replace("\"", "")
                .replace("\\", "").replace("$", "")
                .replace("`", "");

        String[] command = {"/bin/sh", "-c", "echo \"" + text + "\" | pipertts/piper -m pipertts/voices/silverfox/en_US-silverfox-medium.onnx -f -"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] wavData = outputStream.toByteArray();
        InputStream filestream = new ByteArrayInputStream(wavData);

        mc.getMessage().getChannel().sendMessage(filestream,"tts.wav");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
