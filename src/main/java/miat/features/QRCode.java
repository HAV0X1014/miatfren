package miat.features;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class QRCode {
    public static EmbedBuilder qrCodeCreate(String data) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        URL url = null;
        BufferedImage qrcode;
        InputStream responseContent;
        try {
            url = new URL("https://api.qrserver.com/v1/create-qr-code/?data=" + data + "&size=512x512&qzone=1");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder().url(url).build();

        try (Response resp = client.newCall(request).execute()) {
            responseContent = resp.body().byteStream();
            qrcode = ImageIO.read(responseContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setAuthor("Made with goqr.me","https://goqr.me","");
        e.setImage(qrcode);
        e.setColor(Color.WHITE);

        return e;
    }
}
