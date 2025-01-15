package miat.features;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Wikipedia {
    public static String randomArticle() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url("https://en.wikipedia.org/w/api.php?action=query&format=json&list=random&utf8=1&formatversion=2&rnnamespace=0").build();
        try {
            Response resp = client.newCall(request).execute();
            if (resp.body() != null) {
                String result = resp.body().string();
                String[] things = result.split("\"title\":\"");
                result = things[1];
                result = result.replace("\"}]}}", "");
                result = "https://en.wikipedia.org/wiki/" + result;
                return result.replace(" ", "_");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Failed to get an article from Wikipedia.";
    }
}
