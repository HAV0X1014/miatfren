package miat.UtilityCommands;

import miat.FileHandlers.ConfigHandler;
import okhttp3.*;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static miat.MiatMain.configFile;

public class GeminiImageCheck {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void check(MessageCreateEvent mc) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        JSONObject payload = new JSONObject();          //holds the whole object of JSON sent to google
        JSONArray contents = new JSONArray();           //holds the text content sent to the AI

        JSONObject userPrompt = new JSONObject();
        userPrompt.put("role", "user");
        JSONArray userParts = new JSONArray();
        JSONObject userText = new JSONObject();
        JSONObject userImage = new JSONObject();
        JSONObject imageData = new JSONObject();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(mc.getMessage().getAttachments().get(0).asImage().join(),"png",baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
        imageData.put("data", base64String);
        imageData.put("mimeType", "image/png");
        userImage.put("inlineData", imageData);

        userText.put("text", "Given an image, rate the content on if it is artwork or an unrelated image (screenshot, food image, game screenshot, real life, etc.) and if it is NSFW or suggestive. Create single-word tags of the image's content in a JSON string. Respond in a JSON format with ratings in a float from 0-1 with 1 being the top. The keys should be \"tags\", \"artwork\", \"nsfw\", \"suggestive\".");

        userParts.put(userText);
        userParts.put(userImage);

        userPrompt.put("parts", userParts);
        contents.put(userPrompt);
        JSONArray safetySettings = new JSONArray();

        /*
        JSONObject catMed = new JSONObject();
        catMed.put("category", "HARM_CATEGORY_MEDICAL");
        catMed.put("threshold", "BLOCK_NONE");

        JSONObject catVio = new JSONObject();
        catVio.put("category", "HARM_CATEGORY_VIOLENCE");
        catVio.put("threshold", "BLOCK_NONE");

        JSONObject catTox = new JSONObject();
        catTox.put("category", "HARM_CATEGORY_TOXICITY");
        catTox.put("threshold", "BLOCK_NONE");

        JSONObject catSex = new JSONObject();
        catSex.put("category", "HARM_CATEGORY_SEXUAL");
        catSex.put("threshold", "BLOCK_NONE");

        JSONObject catDng = new JSONObject();
        catDng.put("category", "HARM_CATEGORY_DANGEROUS");
        catDng.put("threshold", "BLOCK_NONE");

        JSONObject catDer = new JSONObject();
        catDer.put("category", "HARM_CATEGORY_DEROGATORY");
        catDer.put("threshold", "BLOCK_NONE");

        JSONObject catUnsp = new JSONObject();
        catUnsp.put("category", "HARM_CATEGORY_UNSPECIFIED");
        catUnsp.put("threshold", "BLOCK_NONE");
        */
        JSONObject catHar = new JSONObject();
        catHar.put("category", "HARM_CATEGORY_HARASSMENT");
        catHar.put("threshold", "BLOCK_NONE");

        JSONObject catHtSp = new JSONObject();
        catHtSp.put("category", "HARM_CATEGORY_HATE_SPEECH");
        catHtSp.put("threshold", "BLOCK_NONE");

        JSONObject catSxExp = new JSONObject();
        catSxExp.put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT");
        catSxExp.put("threshold", "BLOCK_NONE");

        JSONObject catDngCnt = new JSONObject();
        catDngCnt.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
        catDngCnt.put("threshold", "BLOCK_NONE");

        /*
        safetySettings.put(catMed);
        safetySettings.put(catVio);
        safetySettings.put(catTox);
        safetySettings.put(catSex);
        safetySettings.put(catDng);
        safetySettings.put(catDer);
        safetySettings.put(catUnsp);
         */
        safetySettings.put(catHar);
        safetySettings.put(catHtSp);
        safetySettings.put(catSxExp);
        safetySettings.put(catDngCnt);

        JSONObject generationConfig = new JSONObject();
        generationConfig.put("responseMimeType", "application/json");

        payload.put("safetySettings", safetySettings);
        payload.put("contents", contents);
        payload.put("generationConfig", generationConfig);

        RequestBody requestBody = RequestBody.create(JSON, payload.toString());
        String textOutput;
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + ConfigHandler.getString("GeminiKey", configFile));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            String responseContent;
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            JSONObject response = new JSONObject(responseContent);
            System.out.println(response.toString(1));
            textOutput = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
            System.out.println(textOutput);
            JSONObject textOutputJSON = new JSONObject(textOutput);
            if (Float.parseFloat(textOutputJSON.get("artwork").toString()) > .7) {
                mc.addReactionsToMessage("\uD83D\uDCDD");
            } else {
                mc.addReactionsToMessage("\uD83D\uDCF8");
            }

            if (Float.parseFloat(textOutputJSON.get("nsfw").toString()) > .9) {
                mc.addReactionsToMessage("‼\uFE0F");
            }

            if (Float.parseFloat(textOutputJSON.get("suggestive").toString()) > .7) {
                mc.addReactionsToMessage("\uD83E\uDEE2");
            }

            JSONArray safetyRatings = response.getJSONArray("candidates").getJSONObject(0).getJSONArray("safetyRatings");
            for (int i = 0; i < safetyRatings.length(); i++) {
                JSONObject safetyRating = safetyRatings.getJSONObject(i);
                if (safetyRating.getString("category").equals("HARM_CATEGORY_SEXUALLY_EXPLICIT")) {
                    switch (safetyRating.getString("probability")) {
                        case "HIGH":
                            mc.addReactionsToMessage("‼\uFE0F");
                        case "MEDIUM":
                            mc.addReactionsToMessage("❗");
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
