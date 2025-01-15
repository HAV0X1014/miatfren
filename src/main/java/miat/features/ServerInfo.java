package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;

public class ServerInfo {
    public static void serverInfo(SlashCommandInteraction interaction) {
        String icon = interaction.getGuild().getIcon().getUrl().toString();
        String creationDate = interaction.getGuild().getTimeCreated().toString();
        long ownerID = interaction.getGuild().getOwnerIdLong();
        String systemChannelTag = interaction.getGuild().getSystemChannel().getAsMention();
        String systemChannelID = interaction.getGuild().getSystemChannel().getId();
        int members = interaction.getGuild().getMemberCount();
        int roles = interaction.getGuild().getRoles().size();
        int channels = interaction.getGuild().getChannels().size();
        String boost = interaction.getGuild().getBoostTier().toString();
        int boostCount = interaction.getGuild().getBoostCount();
        String ownerName = interaction.getGuild().getOwner().getEffectiveName();
        String serverID = interaction.getGuild().getId();
        String serverName = interaction.getGuild().getName();
        int customEmojis = interaction.getGuild().getEmojis().size();

        EmbedBuilder e = new EmbedBuilder();
        e.setThumbnail(icon);
        e.setTitle(serverName);
        e.setColor(Color.orange);
        e.addField("Server ID :", serverID,false);
        e.addField("Server Owner :", "<@!" + ownerID + ">\nTag : ``" + ownerName + "``\nID : ``" + ownerID + "``",false);
        e.addField("Creation Date : ", creationDate,false);
        e.addField("System Channel : ", "Channel : " + systemChannelTag + "\nChannel ID : " + systemChannelID,false);
        e.addField("Server Stats : ", "Members : ``" + members + "``\nRoles : ``" + roles + "``\nChannels : ``" + channels + "``\nEmojis : ``" + customEmojis + "``",false);
        e.addField("Boosting : ", "Level : ``" + boost + "``\nBoosts : ``" + boostCount + "``",false);
        interaction.replyEmbeds(e.build()).queue();
    }
}
