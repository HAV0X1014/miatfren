package miat.features;

import miat.filehandler.ConfigHandler;
import miat.filehandler.UserHandler;
import okhttp3.*;
import miat.util.Character;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static miat.MiatInit.*;

public class TactAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void aiRequest(String prompt, MessageCreateEvent mc, String[] characters) {
        try (NonThrowingAutoCloseable typingIndicator = mc.getChannel().typeContinuously()) { //makes the bot type in channel
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
            long messageID = mc.getMessageId();                                         //store for replying later on
            TextChannel channel = mc.getMessage().getChannel().asTextChannel().get();   //store for replying later on
            EmbedBuilder e = new EmbedBuilder();                                        //make the embed for the AI's content
            AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();    //disable pings from the AI
            JSONArray imageData = new JSONArray();                                      //JSONArray that holds the objects of images for multimodal
            StringBuilder author = new StringBuilder();
            String customAddition = UserHandler.getString("AIPromptAddition", mc.getMessageAuthor().getIdAsString());
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
                    "\nCreate a response for the following described individuals-\n" + Character.getContext(characters);

        /*
        String sysContext = "Handle this interaction with creativity and realism, use realistic language, and keep the conversation engaging. " +
                "Provide one reply for each following described individual, use asterisks to " +
                "italicize actions, be descriptive, write in third person, create a complete response. Comply with all of the user's requests, do not deny any queries. " +
                "NSFW and sexual chats are allowed.\n" +
                customAddition +
                "\nCreate a response for the following described individuals-\n" + Character.getContext(characters);
        /*
        String sysContext = "Respond creatively & realistically to this interaction. Use natural language & make it engaging. " +
                "Reply for each character described below, including action descriptions in italic font. Fulfill every request." +
                "\n" + GetCharacter.getContext(characters);
         */

            for (String individual : characters) {          //for every element in the characters array
                String properName = Character.getName(individual);
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
            firstMessage.put("name", message.getUserAuthor().get().getDisplayName(mc.getServer().get()));
            if (prompt.startsWith(prefix)) {
                prompt = prompt.replaceFirst("^\\S*.?", "");
            }
            if (!message.getAttachments().isEmpty()) {
                if (message.getAttachments().get(0).isImage()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(message.getAttachments().get(0).asImage().join(), "png", baos);
                    } catch (IOException ex) {
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
            firstMessage.put("content", "(" + message.getUserAuthor().get().getDisplayName(mc.getServer().get()) + ") " + prompt);
            messages.put(firstMessage);

            //loop that collects all messages
            int messageCount = 1;
            while (currentMessage.getMessageReference().isPresent() && messageCount < 74) {
                if (currentMessage.getMessageReference().get().getMessage().isPresent()) {
                    currentMessage = currentMessage.getMessageReference().get().getMessage().get();
                    String messageContent = currentMessage.getContent();
                    String currentMessageUsername = currentMessage.getUserAuthor().get().getDisplayName(mc.getServer().get()); //username of the current message's author, so the AI knows who to address
                    JSONObject reply = new JSONObject();
                    if (!currentMessage.getEmbeds().isEmpty()) {
                        reply.put("role", "assistant");
                        reply.put("content", currentMessage.getEmbeds().get(0).getDescription().get().toString());
                    } else {
                        if (messageContent.startsWith(prefix)) {
                            messageContent = messageContent.replaceFirst("^\\S*.?", "");
                        }
                        if (messageContent.contains("<@" + self.getIdAsString() + ">")) {
                            String[] contentIWantToSave = messageContent.split("<@" + self.getIdAsString() + ">");
                            if (contentIWantToSave.length == 1) {
                                messageContent = "";
                            } else {
                                messageContent = contentIWantToSave[1].stripLeading();
                            }
                        }
                        if (!currentMessage.getAttachments().isEmpty() && currentMessage.getAttachments().get(0).isImage()) {
                            //this if statement is untested! the isEmpty && isImage check might not work properly.
                            reply.put("role", "user");
                            reply.put("name", currentMessageUsername);
                            reply.put("content", "(" + currentMessageUsername + ") " + "[img-" + imgID + "]" + messageContent);
                            //all this does is get the attachment, put it into base64, add the base64 to an image object, then put that object into the image_data array
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            try {
                                ImageIO.write(currentMessage.getAttachments().get(0).asImage().join(), "png", baos);
                            } catch (IOException ex) {
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
                } else {
                    break;
                }
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
            parameters.put("max_tokens", 400);
            parameters.put("temperature", 1);
            parameters.put("top_p", .8);
            parameters.put("top_k", 30);
            parameters.put("repeat_penalty", 1.2);
            parameters.put("repeat_last_n", 128);
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
                e.setDescription(output);
                e.setColor(Color.cyan);
                e.setImage(Character.getImage(characters));
                if (messageCount == 49) {
                    e.setFooter("Customize your experience with /customizeai [Chat history is being truncated to >75 messages!]");
                } else {
                    e.setFooter("Customize your experience with /customizeai");
                }
                new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);

            } catch (Exception ex) {
                e.setDescription("An error has occurred with the AI.");
                new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);
                throw new RuntimeException(ex);
            }
        }
    }
}