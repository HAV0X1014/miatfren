package miat.features;

import miat.filehandler.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Gemini {
    /*
    * this feature isnt meant to be that good, just functional.
    * sometimes when llama 3 or whichever model im using breaks i want a fallback
    * that will actually answer my questions
     */
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static EmbedBuilder send(MessageReceivedEvent mc) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        JSONObject payload = new JSONObject();          //holds the whole object of JSON sent to google
        JSONArray contents = new JSONArray();           //holds the text content sent to the AI
        //how gemini works is that the turns HAVE to be user -> model -> user -> model -> user and ALWAYS start and end with user
        //its really stupid, and making the JSON for this is a pain in the ass.

        JSONObject userPrompt = new JSONObject();
        userPrompt.put("role", "user");
        JSONArray userParts = new JSONArray();
        JSONObject userText = new JSONObject();
        JSONObject userImage = new JSONObject();
        JSONObject imageData = new JSONObject();
        userText.put("text", mc.getMessage().getContentRaw().strip().replaceFirst("\\S*.?",""));

        if (!mc.getMessage().getAttachments().isEmpty()) {
            if (mc.getMessage().getAttachments().get(0).isImage()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(ImageIO.read(mc.getMessage().getAttachments().get(0).getProxy().download().get()), "png", baos);
                } catch (IOException | ExecutionException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
                imageData.put("data", base64String);
                imageData.put("mimeType", "image/png");
                userImage.put("inlineData", imageData);
                userParts.put(userImage);
            }
        }

        userParts.put(userText);
        userPrompt.put("parts", userParts);
        contents.put(userPrompt);
        //this hunk of shit puts the text into googles special little array with an object inside or something
        //i hate google's stupid fucking API formatting

        JSONArray safetySettings = new JSONArray();
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

        safetySettings.put(catHar);
        safetySettings.put(catHtSp);
        safetySettings.put(catSxExp);
        safetySettings.put(catDngCnt);

        payload.put("safetySettings", safetySettings);
        payload.put("contents",contents);

        RequestBody requestBody = RequestBody.create(JSON, payload.toString());
        String output;
        String responseContent = "";

        EmbedBuilder em = new EmbedBuilder();
        em.setTitle("Response from Google Gemini");
        em.setFooter("(only one response, no conversations yet.)");
        try {
            //to change model, change the gemini-1.5-flash-latest thing to whichever model you want.
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + ConfigHandler.getString("GeminiKey"));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            //you know what i hate more than googles INPUT json?????? OUTPUT JSON!!!!
            JSONObject response = new JSONObject(responseContent);
            output = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
            em.setDescription(output.length() > 4095 ? output.substring(0,4095) : output);
        } catch (Exception e) {
            System.out.println("[Gemini API error.]\n" + responseContent);
            em.setDescription("-# There was an error, probably NSFW content or another inbuilt censorship of gemini was hit.\n" + responseContent);
            e.printStackTrace();
        } finally {
            return em;
        }
    }
    public static EmbedBuilder send(SlashCommandInteraction interaction) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        JSONObject payload = new JSONObject();          //holds the whole object of JSON sent to google
        JSONArray contents = new JSONArray();           //holds the text content sent to the AI
        //how gemini works is that the turns HAVE to be user -> model -> user -> model -> user and ALWAYS start and end with user
        //its really stupid, and making the JSON for this is a pain in the ass.

        JSONObject userPrompt = new JSONObject();
        userPrompt.put("role", "user");
        JSONArray userParts = new JSONArray();
        JSONObject userText = new JSONObject();
        userText.put("text", interaction.getOption("prompt").getAsString());
        userParts.put(userText);
        userPrompt.put("parts", userParts);
        contents.put(userPrompt);
        //this hunk of shit puts the text into googles special little array with an object inside or something
        //i hate google's stupid fucking API formatting

        JSONArray safetySettings = new JSONArray();
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

        safetySettings.put(catHar);
        safetySettings.put(catHtSp);
        safetySettings.put(catSxExp);
        safetySettings.put(catDngCnt);

        payload.put("safetySettings", safetySettings);
        payload.put("contents",contents);

        RequestBody requestBody = RequestBody.create(JSON, payload.toString());
        String output;
        String responseContent = "";

        EmbedBuilder em = new EmbedBuilder();
        em.setTitle("Response from Google Gemini");
        em.setFooter("(only one response, no conversations yet.)");
        try {
            //to change model, change the gemini-1.5-flash-latest thing to whichever model you want.
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + ConfigHandler.getString("GeminiKey"));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            //you know what i hate more than googles INPUT json?????? OUTPUT JSON!!!!
            JSONObject response = new JSONObject(responseContent);
            output = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
            em.setDescription(output.length() > 4095 ? output.substring(0,4095) : output);
        } catch (Exception e) {
            System.out.println("[Gemini API error.]\n" + responseContent);
            em.setDescription("-# There was an error, probably NSFW content or another inbuilt censorship of gemini was hit.\n" + responseContent);
            e.printStackTrace();
        } finally {
            return em;
        }
    }
}
