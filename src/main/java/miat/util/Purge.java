package miat.util;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

public class Purge {
    public static void purge(SlashCommandInteraction interaction) {
        String num = interaction.getArgumentStringValueByIndex(0).orElse("Invalid");
        int deleteAmt = Integer.parseInt(num);
        String returnMessage;
        if (deleteAmt < 1) {
            returnMessage = "Purge amount must be greater than 0";
            interaction.createImmediateResponder().setContent(returnMessage).setFlags(MessageFlag.EPHEMERAL).respond();
            return;
        }
        if (CheckPermission.checkPermission(interaction, PermissionType.MANAGE_MESSAGES)) {
            try {
                interaction.getChannel().get().getMessages(deleteAmt).get().deleteAll();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            returnMessage = "Purged " + deleteAmt + " messages.";
        } else {
            returnMessage = "You do not have MANAGE_MESSAGES permissions, oops!";
        }
        interaction.createImmediateResponder().setContent(returnMessage).setFlags(MessageFlag.EPHEMERAL).respond();
    }

    public static String purge(MessageCreateEvent mc, String amt) {
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

        if (CheckPermission.checkPermission(mc,PermissionType.MANAGE_MESSAGES)) {
            try {
                mc.getChannel().getMessages(amount).get().deleteAll();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            returnMessage = "Purged " + amount + " messages.";
        } else {
            returnMessage = "You do not have MANAGE_MESSAGES permissions, oops!";
        }
        return returnMessage;
    }
}
