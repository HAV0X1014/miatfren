package miat.filehandler;

import okhttp3.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

public class Webhook {
    private final OkHttpClient client = new OkHttpClient();
    public void send(String address, String content) {
        // Create the request body
        RequestBody formBody = new FormBody.Builder()
                .add("content", content)
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                .build();

        // Execute the request
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}