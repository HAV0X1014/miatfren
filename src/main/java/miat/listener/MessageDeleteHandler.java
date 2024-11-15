package miat.listener;

import miat.filehandler.ServerHandler;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.listener.message.MessageDeleteListener;

import java.awt.*;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import static miat.MiatInit.self;

public class MessageDeleteHandler implements MessageDeleteListener {
    @Override
    public void onMessageDelete(MessageDeleteEvent md) {
        try {
            if (!md.getMessageAuthor().get().asUser().get().equals(self)) {
                Channel logChannel = md.getServer().get().getChannelById(ServerHandler.getDeletedMessageLogChannel(md.getServer().get().getIdAsString())).orElseThrow();

                EmbedBuilder e = new EmbedBuilder();
                e.setAuthor(md.getMessageAuthor().get().asUser().get());
                e.setColor(Color.LIGHT_GRAY);
                e.setDescription("-# <@"+ md.getMessageAuthor().get().getIdAsString() + "> Deleted message in <#" + md.getChannel().getIdAsString() + ">\n" + md.getMessageContent().get());
                if (md.getMessageAttachments().isPresent()) {
                    MessageBuilder mb = new MessageBuilder();
                    for (int i = 0; i < md.getMessageAttachments().get().size(); i++) {
                        try {
                            mb.addAttachment(md.getMessageAttachments().get().get(i).asByteArray().get(), md.getMessageAttachments().get().get(i).getFileName());
                            e.setFooter("Attachments included.");
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    mb.setEmbed(e);
                    mb.send(logChannel.asServerTextChannel().get());
                } else {
                    logChannel.asServerTextChannel().get().sendMessage(e);
                }
            }
        } catch (NoSuchElementException ignored) {}
    }
}
