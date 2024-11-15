package miat.util;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static miat.MiatInit.api;

public class DeleteMessage {
    public static void deleteMessage(SlashCommandInteraction interaction, DiscordApi api) {
        String messageID = interaction.getArgumentStringValueByIndex(0).orElse("Invalid");
        TextChannel channel = interaction.getChannel().get().asTextChannel().get();

        if (CheckPermission.checkPermission(interaction, PermissionType.MANAGE_MESSAGES)) {
            Pattern pattern = Pattern.compile("^[0-9]*?(?=-)");
            Matcher matcher = pattern.matcher(messageID);
            if (matcher.find()) {
                String matchedText = matcher.group();
                channel = api.getTextChannelById(matchedText).get();
            }
            messageID = messageID.replaceAll("^[0-9]*?(-)", "");
            Message message = api.getMessageById(messageID,channel).join();
            if (interaction.getServer().get() == message.getServer().get()) {
                message.delete();
                interaction.createImmediateResponder().setContent("Message Deleted.").setFlags(MessageFlag.EPHEMERAL).respond();
            } else {
                interaction.createImmediateResponder().setContent("You cannot delete messages across servers!").setFlags(MessageFlag.EPHEMERAL).respond();
            }
        } else {
            interaction.createImmediateResponder().setContent("You do not have MANAGE_MESSAGES permissions, oops!").setFlags(MessageFlag.EPHEMERAL).respond();
        }
    }
    public static String deleteOwnCommandResponse(MessageCreateEvent mc) {
        Message message = mc.getMessage();
        if (message.getMessageReference().isPresent()) {
            String repliedToMessage = message.getMessageReference().get().getMessageId().get().toString();
            Message deleteCandidate = api.getMessageById(repliedToMessage, mc.getChannel()).join();
            if (deleteCandidate.getMessageInteraction().isPresent()) {
                if (deleteCandidate.getMessageInteraction().get().getUser().getIdAsString().equals(mc.getMessage().getUserAuthor().get().getIdAsString())) {
                    deleteCandidate.delete();
                    return "Deleted message.";
                } else {
                    return "You cannot delete others' messages with this command";
                }
            }
            if (deleteCandidate.getAuthor().asUser().get().equals(api.getYourself())) {
                if (deleteCandidate.getMessageReference().isPresent()) {
                    String commandIssueMessage = deleteCandidate.getMessageReference().get().getMessageId().get().toString();
                    Message commandIssuer = api.getMessageById(commandIssueMessage, mc.getChannel()).join();
                    if (commandIssuer.getAuthor().equals(message.getAuthor())) {
                        api.getMessageById(repliedToMessage, mc.getChannel()).join().delete();
                        return "Deleted message.";
                    } else {
                        return "You cannot delete others' messages with this command.";
                    }
                } else {
                    return "The command to delete has no author.";
                }
            } else {
                return "You cannot delete messages that didn't come from the bot with this command.";
            }
        } else {
            return "Reply to your command response you want deleted.";
        }
    }
}
