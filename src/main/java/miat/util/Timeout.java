package miat.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.time.Duration;
import java.util.Objects;

public class Timeout {
    public static String timeout(SlashCommandInteraction interaction) {
        if (interaction.getGuild() != null) {
            if (CheckPermission.checkPermission(interaction, Permission.MODERATE_MEMBERS)) {
                interaction.getGuild().timeoutFor(UserSnowflake.fromId(Objects.requireNonNull(interaction.getOption("user")).getAsUser().getIdLong()), Duration.ofDays(Objects.requireNonNull(interaction.getOption("days")).getAsInt())).queue();
                return "Timed out " + interaction.getOption("user").getAsUser().getName() + " for " + interaction.getOption("days").getAsInt() + " days.";
            }
            else {
                return "You do not have MODERATE_MEMBERS permissions.";
            }
        } else {
            return "This command only works in servers.";
        }
    }
}
