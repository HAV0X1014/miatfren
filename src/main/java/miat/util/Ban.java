package miat.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.concurrent.TimeUnit;

public class Ban {
    public static String ban(SlashCommandInteraction interaction) {
        User user2ban = interaction.getOption("user").getAsUser();
        String username2ban = user2ban.getName();
        String replyContent;

        if (CheckPermission.checkPermission(interaction, Permission.BAN_MEMBERS)) {
            if (Whitelist.whitelisted(user2ban.getId())) {
                replyContent = "Failed to ban user " + username2ban + ".";
            } else {
                interaction.getGuild().ban(user2ban,1, TimeUnit.HOURS).queue();
                replyContent = "Banned user " + username2ban + ".";
            }
        }
        else {
            replyContent = "You do not have BAN_MEMBERS permissions, oops!";
        }
        return replyContent;
    }
}
