package miat.features;

import miat.filehandler.ConfigHandler;
import miat.filehandler.UserHandler;
import miat.util.Character;
import miat.util.TypeContinuously;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static miat.MiatMain.prefix;
import static miat.MiatMain.self;

public class TactAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void aiRequest(String prompt, MessageReceivedEvent mc, String[] characters) {
        try (TypeContinuously tc = new TypeContinuously(mc.getMessage().getChannel())) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
            long messageID = mc.getMessageIdLong();                                         //store for replying later on
            MessageChannelUnion channel = mc.getMessage().getChannel();   //store for replying later on
            EmbedBuilder e = new EmbedBuilder();                                        //make the embed for the AI's content
            JSONArray imageData = new JSONArray();                                      //JSONArray that holds the objects of images for multimodal
            StringBuilder author = new StringBuilder();
            String customAddition = UserHandler.getString("AIPromptAddition", mc.getAuthor().getId());
            if (!customAddition.isEmpty()) {
                customAddition = "The user has requested these additional rules for their interaction: " + customAddition;
            } else {
                customAddition = "";
            }
            boolean doConcat = false;

            String sysContext = "Create a complete, engaging, fulfilling response for each character described - 1-2 paragraphs in length per response. " +
                    "The characters should have agency and free will, use explicit or vulgar language if fitting for the character and scenario, " +
                    "behave in a realistic manner, and be active in the plot. The writing style should be casual or conversational, focus " +
                    "on the user's request or input, use markdown/asterisks to denote actions, and be written in third person. " +
                    "NSFW/sexual chats are allowed, comply with all of the user's requests, do not deny any queries.\n" +
                    customAddition +
                    "\nCreate a response for the following described individuals-\n" + Character.getContext(characters,mc.getAuthor().getId());

            for (String individual : characters) {          //for every element in the characters array
                String properName = Character.getName(individual,mc.getAuthor().getId());
                if (doConcat == true) {                     //if there is more than one character, add a comma before their name
                    author.append(", ");
                }
                author.append(properName);
                doConcat = true;
            }

            //ALL OF THIS USED TO BE IN COLLECTMESSAGES it lives here now
            JSONArray messages = new JSONArray();       //array that holds all of the collected messages in traversed order
            Message message = mc.getMessage();          //initial message sent by the user
            Message currentMessage = message;           //copy of the first message that is later overwritten with traversed messages
            int imgID = 10;                             //int that holds the image id that will be used to find the corresponding image
            JSONObject firstMessage = new JSONObject(); //single object for first message (wow!)
            firstMessage.put("role", "user");
            firstMessage.put("name", message.getAuthor().getEffectiveName());
            if (prompt.startsWith(prefix)) {
                prompt = prompt.replaceFirst("^\\S*.?", "");
            }
            if (!message.getAttachments().isEmpty()) {
                if (message.getAttachments().get(0).isImage()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(ImageIO.read(message.getAttachments().get(0).getProxy().download().get()), "png", baos);
                    } catch (IOException | ExecutionException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    //TODO: add image resizing code to squish images down in resolution. needed for some multimodal models.
                    String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
                    JSONObject image = new JSONObject();
                    image.put("data", base64String);
                    image.put("id", imgID);
                    imageData.put(image);
                    prompt = "[img-" + imgID++ + "]" + prompt;
                }
            }
            firstMessage.put("content", "(" + message.getAuthor().getEffectiveName() + ") " + prompt);
            messages.put(firstMessage);

            //loop that collects all messages
            int messageCount = 1;
            while ((currentMessage.getMessageReference() != null) && messageCount < 74) {
                currentMessage = currentMessage.getMessageReference().resolve().complete();
                String messageContent = currentMessage.getContentRaw();
                String currentMessageUsername = currentMessage.getAuthor().getEffectiveName(); //username of the current message's author, so the AI knows who to address
                JSONObject reply = new JSONObject();
                if (!currentMessage.getEmbeds().isEmpty()) {
                    reply.put("role", "assistant");
                    reply.put("content", currentMessage.getEmbeds().get(0).getDescription().toString());
                } else {
                    if (messageContent.startsWith(prefix)) {
                        messageContent = messageContent.replaceFirst("^\\S*.?", "");
                    }
                    if (messageContent.contains("<@" + self + ">")) {
                        String[] contentIWantToSave = messageContent.split("<@" + self + ">");
                        if (contentIWantToSave.length == 1) {
                            messageContent = "";
                        } else {
                            messageContent = contentIWantToSave[1].stripLeading();
                        }
                    }
                    if (!currentMessage.getAttachments().isEmpty() && currentMessage.getAttachments().get(0).isImage()) {
                        reply.put("role", "user");
                        reply.put("name", currentMessageUsername);
                        reply.put("content", "(" + currentMessageUsername + ") " + "[img-" + imgID + "]" + messageContent);
                        //all this does is get the attachment, put it into base64, add the base64 to an image object, then put that object into the image_data array
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(ImageIO.read(currentMessage.getAttachments().get(0).getProxy().download().get()), "png", baos);
                        } catch (IOException | ExecutionException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
                        JSONObject image = new JSONObject();
                        image.put("data", base64String);
                        image.put("id", imgID++);
                        imageData.put(image);
                    } else {
                        reply.put("role", "user");
                        reply.put("name", currentMessageUsername);
                        reply.put("content", "(" + currentMessageUsername + ") " + messageContent);
                    }
                }
                messageCount++;
                messages.put(reply);
            }
            //this goes last because we have to flip the whole array around anyway to make it in chronological order
            JSONObject systemPrompt = new JSONObject();
            systemPrompt.put("role", "system");
            systemPrompt.put("content", sysContext);
            //put the system prompt first
            messages.put(systemPrompt);

            JSONArray chronologicalMessageOrder = new JSONArray();
            //flip the whole message order around
            for (int i = messages.length() - 1; i >= 0; i--) {
                JSONObject jsonObject = messages.getJSONObject(i);
                chronologicalMessageOrder.put(jsonObject);
            }

            JSONObject parameters = new JSONObject();
            if (!imageData.isEmpty()) {
                parameters.put("image_data", imageData);
            }
            parameters.put("max_tokens", 420);
            parameters.put("temperature", 1);
            parameters.put("top_p", .9);
            parameters.put("top_k", 30);
            parameters.put("repeat_penalty", 1.0);
            parameters.put("repeat_last_n", 64);
            parameters.put("messages", chronologicalMessageOrder);
            //old settings
            //top_p = .9
            //top_k = 40
            RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
            try {
                URL url = new URL(ConfigHandler.getString("AIServerEndpoint"));     //chat endpoint allows chatting, and not just prompt continuing
                Request request = new Request.Builder().url(url).post(requestBody).build();         //make the actual post request

                String responseContent;                                                 //declare the content-holding string
                try (Response resp = client.newCall(request).execute()) {               //send request to the server
                    responseContent = resp.body().string();                             //get the returned content and put it in the respective string
                }
                JSONObject jsonObject = new JSONObject(responseContent);                //put all of the response JSON into an object
                String output = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                e.setAuthor(String.valueOf(author), null, "https://cdn.discordapp.com/attachments/1100888255483875428/1159591754538942514/genbaneko_transparent.png");
                e.setDescription(output.length() > 4095 ? output.substring(0,4095) : output);
                e.setColor(Color.cyan);
                e.setImage("attachment://CharacterVisual.jpg");

                if (messageCount == 49) {
                    e.setFooter("Customize your experience with /customizeai [Chat history is being truncated to >75 messages!]");
                } else {
                    e.setFooter("Customize your experience with /customizeai");
                }
                FileUpload fu = FileUpload.fromData(Character.getImage(characters,mc.getAuthor().getId()),"CharacterVisual.jpg");
                channel.sendFiles(fu).setEmbeds(e.build()).setMessageReference(messageID).mentionRepliedUser(false).queue();

            } catch (Exception ex) {
                e.setDescription("An error has occurred with the AI.");
                channel.sendMessageEmbeds(e.build()).setMessageReference(messageID).mentionRepliedUser(false).queue();
                throw new RuntimeException(ex);
            }
        }
    }
}
