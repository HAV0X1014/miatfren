package miat.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.concurrent.TimeUnit;

import static miat.MiatMain.self;

public class DeleteMessage {
    public static void deleteMessage(SlashCommandInteraction interaction) {
        //TODO: needs rewrite! original implementation accepts both 1290463825870454784-1303355627283681290 and 1303355627283681290 formats of message ID. how to implement in JDA?
    }
    public static void deleteOwnCommandResponse(MessageReceivedEvent mc) {
        Message message = mc.getMessage();
        if (message.getMessageReference() != null) { //if the command has a reply to something
            if (message.getMessageReference().getMessage().getAuthor().getId().equals(self)) { //if the author of the replied to message is the bot
                if (message.getMessageReference().getMessage().getMessageReference() != null) { //if the (command response) message is replying to something
                    if (message.getMessageReference().getMessage().getMessageReference().resolve().complete().getAuthor().equals(message.getAuthor())) { //if the authors of the removal request and the author of the command that made the bot reply to them are the same, delete the message
                        message.getMessageReference().getMessage().delete().queue();
                        try {
                            message.delete().queue();
                            message.getMessageReference().getMessage().getMessageReference().getMessage().delete().queue();
                        } catch (Exception ignored) {
                        }
                        mc.getMessage().reply("Deleted command and response.").setMessageReference(mc.getMessage()).mentionRepliedUser(false).complete();
                        mc.getChannel().getHistory().retrievePast(1).queue(messages -> {
                            if (!messages.isEmpty()) {
                                Message latestMessage = messages.get(0);
                                latestMessage.delete().queueAfter(4, TimeUnit.SECONDS);
                            }
                        });
                        //deletes the command, command response, and the invoking command
                    } else {
                        new Reply().noPing(mc,"You cannot delete other users' command responses.");
                    }
                } else {
                    new Reply().noPing(mc,"This command does not reply to a user-- user verification failed.");
                }
            } else {
                new Reply().noPing(mc,"You cannot delete other users' messages with this command.");
            }
        } else {
            new Reply().noPing(mc,"Reply to your command response you want deleted.");
        }
    }
}
