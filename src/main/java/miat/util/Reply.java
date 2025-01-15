package miat.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Reply {
    public void noPing(MessageReceivedEvent mc, String content) {
        mc.getChannel().sendMessage(content).setMessageReference(mc.getMessage()).mentionRepliedUser(false).queue();
    }
    public void noPing(MessageReceivedEvent mc, MessageEmbed embed) {
        mc.getChannel().sendMessageEmbeds(embed).setMessageReference(mc.getMessage()).mentionRepliedUser(false).queue();
    }
}
