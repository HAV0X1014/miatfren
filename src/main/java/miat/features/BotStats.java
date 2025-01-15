package miat.features;

import miat.util.Uptime;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

import static miat.MiatMain.jda;
import static miat.MiatMain.messageCache;

public class BotStats {
    public static EmbedBuilder stats() {
        long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Bot stats");
        e.setThumbnail(jda.getSelfUser().getAvatarUrl());
        e.setDescription("Total mem usage: " + usedMem + "MB" +
                "\nAmount of cached messages: " + messageCache.estimatedSize() +
                "\nNumber of servers: " + jda.getGuilds().size() +
                "\nUptime: " + Uptime.uptime() +
                "\nInvite this bot to your server, or add it to your account!" +
                "\n-# Click the bot's name, and \"+ Add App\"");
        e.setColor(Color.ORANGE);
        return e;
    }
}
