package miat.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class Kick {
    public static String kick(SlashCommandInteraction interaction) {
        User user2kick = interaction.getOption("user").getAsUser();
        String username2kick = user2kick.getName();
        String replyContent;

        if (CheckPermission.checkPermission(interaction, Permission.KICK_MEMBERS)) {
            if (Whitelist.whitelisted(user2kick.getId())) {
                return "Failed to kick user " + username2kick + ".";
            }
            interaction.getGuild().kick(user2kick).queue();
            replyContent = "Kicked user " + username2kick + ".";
        }

        else {
            replyContent = "You do not have KICK_MEMBERS permissions, oops!";
        }
        return replyContent;
    }
}
