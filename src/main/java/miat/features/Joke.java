package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Joke {
    public static EmbedBuilder randomJoke() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        String responseContent;
        Color jokeAPI = new Color(16,23,71);
        Request request = new Request.Builder().url("https://v2.jokeapi.dev/joke/Any?blacklistFlags=racist&format=txt").build();

        try (Response resp = client.newCall(request).execute()) {
            responseContent = resp.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setAuthor("JokeAPI.dev","https://jokeapi.dev/","https://jokeapi.dev/");
        e.setDescription(responseContent);
        e.setColor(jokeAPI);

        //from HAV0X - wow i suck at programming
        //from HAV0X less than an hour later - it could be worse!
        return e;
    }
}
