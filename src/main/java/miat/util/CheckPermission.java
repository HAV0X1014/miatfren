package miat.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class CheckPermission {
    public static boolean checkPermission(SlashCommandInteraction interaction, Permission permcheck) {
        boolean hasPerm = false;
        if (interaction.getMember() != null) {
            hasPerm = interaction.getMember().hasPermission(permcheck);
            if (Whitelist.whitelisted(interaction.getUser().getId())) {
                hasPerm = true;
            }
        }
        return hasPerm;
    }

    public static boolean checkPermission(MessageReceivedEvent mc, Permission permcheck) {
        boolean hasPerm = false;
        if (mc.getMember() != null) {
            hasPerm = mc.getMember().hasPermission(permcheck);
            if (Whitelist.whitelisted(mc.getAuthor().getId())) {
                hasPerm = true;
            }
        }
        return hasPerm;
    }
}
