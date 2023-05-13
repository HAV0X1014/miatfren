package miat.FunFeatures;

import miat.FileHandlers.ReadFirstLine;
import miat.FileHandlers.ReadFull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class KemoYou {
    public static void kemoYou(String character, String prompt, MessageCreateEvent mc) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build();

        String messageID = mc.getMessage().getIdAsString();
        ServerTextChannel channel = mc.getMessage().getServerTextChannel().get();
        EmbedBuilder e = new EmbedBuilder();
        URL url = null;
        try {
            url = new URL("https://api.betterapi.net/youchat?inputs=" + ReadFull.read("ServerFiles/KemoYouPrompts/" + character + ".txt") + prompt + "&key=" + ReadFirstLine.read("ServerFiles/apiKey.txt"));
            Request request = new Request.Builder().url(url).build();

            String responseContent;
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }

            String response = responseContent;
            response = response.replaceAll(".*\"generated_text\":\"", "");
            response = response.replaceAll("\",\"time\":.*", "");
            response = response.replaceAll("\\\\n\\\\n", "\n\n");
            response = response.replaceAll("\\\\n", "\n");

            if (response == null) {
                System.out.println("Something is wrong - KemoYou");
            }

            e.setAuthor(character + ".", "https://you.com/chat", "https://cdn.discordapp.com/attachments/1100888255483875428/1106987473785065492/Daco_1827122.png");
            e.setDescription(response);
            e.setColor(Color.yellow);

            channel.getMessageById(messageID).thenAccept(message -> {
                message.reply(e);
            });

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}