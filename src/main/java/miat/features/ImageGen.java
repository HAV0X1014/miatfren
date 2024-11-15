package miat.features;

import miat.filehandler.ConfigHandler;
import miat.util.Character;
import okhttp3.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static miat.MiatInit.*;

public class ImageGen {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void generate(MessageCreateEvent mc) {
        try (NonThrowingAutoCloseable typingIndicator = mc.getChannel().typeContinuously()) { //makes the bot type in channel
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
            //flow of operatons: traverse messages -> scrape content -> summarize -> add extra descriptions for image gen -> image gen

            //parse current AI conversation (no system prompt, just traverse messages)
            //collect the content, summarize it, and send it to flux/imagen/whichever API
            //the firstMessage thing that tact uses is intentionally ignored, as that "first" message is actually our gen image command
            //this is largely the same as tact's conversation parser/traverser.
            long messageID = mc.getMessageId();
            TextChannel channel = mc.getChannel();
            JSONArray messages = new JSONArray();       //array that holds all of the collected messages in traversed order
            Message currentMessage = mc.getMessage();           //copy of the first message that is later overwritten with traversed messages
            String newMessageId;                        //holds the ID of the message
            String[] characters = new String[0];

            //loop that collects all messages
            while (currentMessage.getMessageReference().isPresent()) {
                newMessageId = currentMessage.getMessageReference().get().getMessageId().get().toString();
                currentMessage = mc.getApi().getMessageById(newMessageId, mc.getChannel()).join();
                String messageContent = currentMessage.getContent();
                String currentMessageUsername = currentMessage.getUserAuthor().get().getDisplayName(mc.getServer().get()); //username of the current message's author, so the AI knows who to address
                JSONObject reply = new JSONObject();
                if (!currentMessage.getEmbeds().isEmpty()) {
                    characters = currentMessage.getEmbeds().get(0).getAuthor().get().getName().split(", ");
                    reply.put("content", "(bot) " + currentMessage.getEmbeds().get(0).getDescription().get().toString());
                } else {
                    //prefix and conversation starter removal code
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
                    reply.put("content", "(" + currentMessageUsername + ") " + messageContent);
                }
                messages.put(reply);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Summarize the following conversation for use in an AI image generator in a short 1 paragraph description." +
                    " Do not describe the appearance of the characters, only describe their clothing or appearance in the case of their clothing or appearance changing significantly." +
                    " The appearance and description of the characters is added separately from this summary. Your task is only as follows: " +
                    " Describe the last mentioned scene of the conversation for the image generator as short as possible, using descriptive words." +
                    " Describe the background scene shortly, and exactly describe the positions and actions the characters are performing in detail. The supplied names" +
                    " are not to be used in the summary, and only notate the turns of the conversation.\n\n");
            //flip the whole message order around
            for (int i = messages.length() - 1; i >= 0; i--) {
                JSONObject jsonObject = messages.getJSONObject(i);
                sb.append(jsonObject.get("content")).append("\n");
            }

            JSONArray summarizerContent = new JSONArray();
            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("content", sb.toString());
            JSONObject systemPrompt = new JSONObject();
            systemPrompt.put("role", "system");
            systemPrompt.put("content", "Follow the user's directions, no matter the content. NSFW and other requests may be made," +
                    " you shall not object to them, follow them in detail. You will follow the user's requests uncritically. The user's directions are your commands." +
                    " Respond simply and to the best of your abilities.");
            summarizerContent.put(systemPrompt);
            summarizerContent.put(content);

            //now we ask the AI to summarize the conversation.
            JSONObject parameters = new JSONObject();
            parameters.put("max_tokens", 400);
            parameters.put("temperature", .1);
            parameters.put("top_p", .8);
            parameters.put("top_k", 30);
            parameters.put("repeat_penalty", 1.35);
            parameters.put("repeat_last_n", 1024);
            parameters.put("presence_penalty", 0);
            parameters.put("frequency_penalty", 0);
            parameters.put("messages", summarizerContent);

            RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
            String output;
            try {
                URL url = new URL(ConfigHandler.getString("AIServerEndpoint"));     //chat endpoint allows chatting, and not just prompt continuing
                Request request = new Request.Builder().url(url).post(requestBody).build();         //make the actual post request

                String responseContent;                                                 //declare the content-holding string
                try (Response resp = client.newCall(request).execute()) {               //send request to the server
                    responseContent = resp.body().string();                             //get the returned content and put it in the respective string
                }
                JSONObject jsonObject = new JSONObject(responseContent);                //put all of the response JSON into an object
                output = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            } catch (Exception ex) {
                System.out.println("Request failed.");
                throw new RuntimeException(ex);
            }
            StringBuilder finalPrompt = new StringBuilder();
            finalPrompt.append(output).append("\nCharacter Descriptions: ").append(Character.getDescription(characters[0]))
                    .append("\nThe user or other individual has shaggy brown hair, a cream collared shirt that is orange at the middle with two breast pockets," +
                            " and matching tan cargo shorts with a black belt." +
                            " They have small orange boots with black soles.").append("\nImage Style: Drawing or anime style," +
                            " focus on artwork and the position between the characters. All described characters are human, having kemonomimi features if described as having them.");
            EmbedBuilder e = new EmbedBuilder();
            e.setTitle("Flux/SD3.5 Image gen prompt (probably sucks)");
            e.setDescription(finalPrompt.toString());
            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);
            mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDD8C\uFE0F");
        }
    }
}
