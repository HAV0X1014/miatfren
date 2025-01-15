package miat.listener;

import miat.filehandler.ServerHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static miat.MiatMain.jda;
import static miat.MiatMain.prefix;

public class ServerJoinHandler extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent botjoin) {
        String serverID = botjoin.getGuild().getId();
        ServerHandler.readConfig(serverID);
        EmbedBuilder join = new EmbedBuilder();

        join.setColor(Color.ORANGE);
        join.setAuthor(jda.getSelfUser().getName());
        join.setTitle("Hello, " + jda.getSelfUser().getEffectiveName() + " is here!");
        join.setThumbnail("https://cdn.discordapp.com/attachments/919786447488290816/920839787789836288/miat.jpeg");
        join.addField("Information :", "Slash Commands are supported! \nPrefix : ``" + prefix + "``\nCreator : ``HAV0X`` & ``arsonfrog``" +
                "\nReact to a message with a flag emoji to translate it. Delete the translation with the :x: emoji.",true);
        join.addField("Get Started :", "Help : ``/miathelp [general|fun|utility|ai]``" +
                "\nSet Deleted Message Log Channel (saves attachments on deleted messages!) : ``/setlogchannel``" +
                "\nEnable/Disable emoji flag translation in a specified channel (announcements channel, for example) : ``/toggletranslatorchannel``",true);
        join.setFooter("Thank you for your support and inviting miat fren, have fun!");

        botjoin.getGuild().getSystemChannel().sendMessageEmbeds(join.build()).queue();
    }
}
