package miat.util;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

public class CheckPermission {

    public static boolean checkPermission(SlashCommandInteraction interaction, PermissionType permcheck) {
        boolean hasPerm = interaction.getServer().get().hasPermission(interaction.getUser(), permcheck);
            if (Whitelist.whitelisted(interaction.getUser().getIdAsString())) {
                hasPerm = true;
            }
        return hasPerm;
    }

    public static boolean checkPermission(MessageCreateEvent mc, PermissionType permcheck) {
        boolean hasPerm = mc.getServer().get().hasPermission(mc.getMessageAuthor().asUser().get(), permcheck);
            if (Whitelist.whitelisted(mc.getMessageAuthor().getIdAsString())) {
                hasPerm = true;
            }
        return hasPerm;
    }
}