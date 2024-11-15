package miat.features;

import okhttp3.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageSearch {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static EmbedBuilder search(String query) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("DuckDuckGo Image Search");
        e.setDescription("Search query - " + query);
        String imageURL = null;

        String vqdValue = null;
        Pattern pattern = Pattern.compile("vqd=\"(.*?)\"");
        OkHttpClient vqdClient = new OkHttpClient();
        //get the 'token' thingy that allows to look at the proper search results
        RequestBody payload = new FormBody.Builder()
                .add("q", query)
                .build();
        Request vqdRequest = new Request.Builder()
                .url("https://duckduckgo.com")
                .post(payload)
                .build();

        try (Response response = vqdClient.newCall(vqdRequest).execute()) {
            // Parse the JSON response
            String vqdResponse = response.body().string();
            Matcher matcher = pattern.matcher(vqdResponse);
            if (matcher.find()) {
                vqdValue = matcher.group(1);
            }

        } catch (IOException ex) {
            System.err.println("An error occurred while making the request: " + ex.getMessage());
        }

        OkHttpClient client = new OkHttpClient();
        /*
        Hints if you want to edit this code
        base URL that has the GET request sent to it
            https://duckduckgo.com/i.js
        the "?" is what allows params to be passed
        put an "&" between params
        the params and meaning are as follows
        "l" is locale/region, "wt-wt" means no region
        "o" is return format
        "q" is query string
        "vqd" is the search token thingy
        "f" is (in order) timelimit, size, color, type_image, layout, licence_image
        "p" is safesearch level. 1 is on, 1 is moderate (dont ask) and -1 is off.
        Info taken from deedy5's duckduckgo_search python project.
         */

        // Define the URL and Request object for the GET request
        String url = "https://duckduckgo.com/i.js?l=us-en&o=json&q=" + query + "&vqd=" + vqdValue + "&f=,,,&p=1";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("authority","duckduckgo.com")
                .addHeader("accept","application/json, text/javascript, */*; q=0.01")
                .addHeader("sec-fetch-dest","empty")
                .addHeader("x-requested-with","XMLHttpRequest")
                .addHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")
                .addHeader("sec-fetch-site","same-origin")
                .addHeader("sec-fetch-mode","cors")
                .addHeader("referer","https://duckduckgo.com/")
                .addHeader("accept-language","en-US,en;q=0.9")
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Check if the request was successful
            if (!response.isSuccessful()) {
                throw new IOException("Unable to retrieve the data: " + response);
            }
            // Parse the JSON response
            String jsonResponse = response.body().string();
            Pattern imager = Pattern.compile("image\":\"(.*?)\"");
            Matcher matcher = imager.matcher(jsonResponse);
            if (matcher.find()) {
                imageURL = matcher.group(1);
            }
            
        } catch (IOException ex) {
            System.err.println("An error occurred while making the request: " + ex.getMessage());
        }
        e.setImage(imageURL);
        return e;
    }
}
