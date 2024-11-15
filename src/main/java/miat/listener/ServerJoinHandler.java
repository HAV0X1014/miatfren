package miat.listener;

import miat.filehandler.ServerHandler;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

import static miat.MiatInit.*;

public class ServerJoinHandler implements ServerJoinListener {
    @Override
    public void onServerJoin(ServerJoinEvent botJoin) {
        String serverID = botJoin.getServer().getIdAsString();
        ServerHandler.readConfig(serverID);
        User me = api.getYourself();
        EmbedBuilder join = new EmbedBuilder();
        join.setAuthor(me);
        join.setTitle("Hello," + self.getName() + " is here!");
        join.setThumbnail("https://cdn.discordapp.com/attachments/919786447488290816/920839787789836288/miat.jpeg");
        join.addField("Information :", "Slash Commands are supported! \nPrefix : ``" + prefix + "``\nCreator : ``HAV0X`` & ``arsonfrog``");
        join.addField("Get Started :", "Help : ``/miathelp [fun|utility|ai]``" +
                "\nSet Deleted Message Log Channel : ``/setlogchannel``" +
                "\nEnable/Disable emoji flag translation in a specified channel (announcements channel, for example) : ``/toggletranslatorchannel``");
        botJoin.getServer().getSystemChannel().get().sendMessage(join);
    }
}
