package miat.features;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Godsays {
    public static String godSays() {
        //literally just http get http://godsays.xyz
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url("https://godsays.xyz").build();
        try {
            Response resp = client.newCall(request).execute();
            if (resp.body() != null) {
                return resp.body().string();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Failed to get a quote from God.";
    }
}
