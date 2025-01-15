package miat.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.concurrent.TimeUnit;

public class Purge {
    public static void purge(SlashCommandInteraction interaction) {
        String num = interaction.getOption("messages").getAsString();
        int deleteAmt = Integer.parseInt(num);
        String returnMessage;
        if (deleteAmt < 1) {
            returnMessage = "Purge amount must be greater than 0";
            interaction.reply(returnMessage).setEphemeral(true).queue();
            return;
        }
        if (CheckPermission.checkPermission(interaction, Permission.MESSAGE_MANAGE)) {
            interaction.getChannel().getHistory().retrievePast(deleteAmt).queue(messages -> { interaction.getChannel().purgeMessages(messages);});
            returnMessage = "Purged " + deleteAmt + " messages.";
        } else {
            returnMessage = "You do not have MANAGE_MESSAGES permissions, oops!";
        }
        interaction.reply(returnMessage).setEphemeral(true).queue();
    }

    public static String purge(MessageReceivedEvent mc, String amt) {
        int amount;
        String returnMessage;
        try {
            amount = Integer.parseInt(amt);
        } catch (NumberFormatException e) {
            return "Purge amount must be an integer.";
        }
        if (amount < 1) {
            return "Purge amount must be greater than 0.";
        }
        if (CheckPermission.checkPermission(mc, Permission.MESSAGE_MANAGE)) {
            mc.getChannel().getHistory().retrievePast(amount + 1).queue(messages -> { mc.getChannel().purgeMessages(messages); });
            returnMessage = "Purged " + amount + " messages.";
            mc.getChannel().getHistory().retrievePast(1).queue(messages -> {
                if (!messages.isEmpty()) {
                    Message latestMessage = messages.get(0);
                    latestMessage.delete().queueAfter(5, TimeUnit.SECONDS);
                }
            });
        } else {
            returnMessage = "You do not have MANAGE_MESSAGES permissions, oops!";
        }
        return returnMessage;
    }
}
