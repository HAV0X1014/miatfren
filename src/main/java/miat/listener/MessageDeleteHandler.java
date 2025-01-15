package miat.listener;

import miat.filehandler.ServerHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static miat.MiatMain.*;

public class MessageDeleteHandler extends ListenerAdapter {
    @Override
    public void onMessageDelete(MessageDeleteEvent md) {
        try {
            Message message = messageCache.asMap().get(md.getMessageId());
            if (md.isFromGuild() && message != null) {         //if the delete event is not from a guild, and is null then we dont care
                if (!ServerHandler.getDeletedMessageLogChannel(message.getGuildId()).isBlank()) {       //check if the server has a log channel set
                    if (!message.getAuthor().getId().equals(self)) {                                    //dont log the message if it was from us
                        String logChannel = ServerHandler.getDeletedMessageLogChannel(message.getGuild().getId());

                        EmbedBuilder e = new EmbedBuilder();
                        e.setAuthor(message.getAuthor().getEffectiveName());
                        e.setColor(Color.LIGHT_GRAY);
                        e.setDescription("-# <@" + message.getAuthor().getId() + "> Deleted message in <#" + message.getChannelId() + ">\n" + message.getContentRaw());
                        Collection<FileUpload> fileUploads = new ArrayList<>(List.of());
                        if (!message.getAttachments().isEmpty()) {
                            for (int i = 0; i < message.getAttachments().size(); i++) {
                                fileUploads.add(FileUpload.fromData(message.getAttachments().get(i).getProxy().download().join(), message.getAttachments().get(i).getFileName()));
                                if (message.getAttachments().get(i).isImage()) {
                                    e.setImage("attachment://" + message.getAttachments().get(i).getFileName());
                                }
                            }
                            e.setFooter("Attachments included.");
                            jda.getChannelById(TextChannel.class, logChannel)
                                    .sendMessageEmbeds(e.build())
                                    .setFiles(fileUploads)
                                    .queue();
                        } else {
                            jda.getChannelById(TextChannel.class, logChannel)
                                    .sendMessageEmbeds(e.build())
                                    .queue();
                        }
                    }
                }
            }
        } finally {
            //remove the message to (hopefully) save memory
            messageCache.invalidate(md.getMessageId());
        }
    }
}
