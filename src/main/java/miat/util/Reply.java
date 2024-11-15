package miat.util;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import static miat.MiatInit.noReplyPing;

public class Reply {
    public void noPing(MessageCreateEvent mc, String content) {
        new MessageBuilder().setAllowedMentions(noReplyPing).setContent(content).replyTo(mc.getMessageId()).send(mc.getChannel());
    }
    //With embed
    public void noPing(MessageCreateEvent mc, EmbedBuilder embed) {
        new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(embed).replyTo(mc.getMessageId()).send(mc.getChannel());
    }
}
