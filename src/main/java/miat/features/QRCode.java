package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.FileUpload;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class QRCode {
    public static void qrCodeCreate(MessageReceivedEvent mc, String data) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        URL url;
        InputStream responseContent;
        FileUpload fu;
        try {
            url = new URL("https://api.qrserver.com/v1/create-qr-code/?data=" + data + "&size=512x512&qzone=1");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder().url(url).build();

        try (Response resp = client.newCall(request).execute()) {
            responseContent = resp.body().byteStream();
            fu = FileUpload.fromData(responseContent.readAllBytes(), "qrcode.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setAuthor("Made with goqr.me","https://goqr.me","https://goqr.me");
        e.setImage("attachment://qrcode.png");
        e.setColor(Color.WHITE);

        mc.getChannel().sendFiles(fu).setEmbeds(e.build()).setMessageReference(mc.getMessage()).mentionRepliedUser(false).queue();
    }
    public static void qrCodeCreate(SlashCommandInteraction interaction, String data) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        URL url;
        InputStream responseContent;
        FileUpload fu;
        try {
            url = new URL("https://api.qrserver.com/v1/create-qr-code/?data=" + data + "&size=512x512&qzone=1");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder().url(url).build();

        try (Response resp = client.newCall(request).execute()) {
            responseContent = resp.body().byteStream();
            fu = FileUpload.fromData(responseContent.readAllBytes(), "qrcode.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setAuthor("Made with goqr.me","https://goqr.me","https://goqr.me");
        e.setImage("attachment://qrcode.png");
        e.setColor(Color.WHITE);

        interaction.replyEmbeds(e.build()).addFiles(fu).queue();
    }
}
