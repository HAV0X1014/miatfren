package miat.features;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class Pfp {
    public static void pfp(SlashCommandInteraction interaction) {
        if (interaction.getOption("user") != null) {
            interaction.reply(interaction.getOption("user").getAsUser().getAvatarUrl()).queue();
        } else {
            interaction.reply(interaction.getUser().getAvatarUrl()).queue();
        }
    }
}
