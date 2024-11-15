package miat.filehandler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class Webhook {
    public void send(String address, String content) {
        OkHttpClient client = new OkHttpClient();                   //make client to send request with
        RequestBody body = RequestBody.create(content.getBytes());  //create the request body with the passed-in string
        Request request = new Request.Builder()                     //build the post request
                .url(address)                                       //send to the passed-in address
                .post(body)                                         //send the content of the passed-in string
                .build();
        try (Response response = client.newCall(request).execute()) {
            //try to send the request
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
