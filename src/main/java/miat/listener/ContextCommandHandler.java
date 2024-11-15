package miat.listener;

import me.bush.translator.Language;
import miat.features.*;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.MessageContextMenuCommandEvent;
import org.javacord.api.interaction.MessageContextMenuInteraction;
import org.javacord.api.listener.interaction.MessageContextMenuCommandListener;

import static miat.MiatInit.deeplEnabled;

public class ContextCommandHandler implements MessageContextMenuCommandListener {
    @Override
    public void onMessageContextMenuCommand(MessageContextMenuCommandEvent event) {
        MessageContextMenuInteraction interaction = event.getMessageContextMenuInteraction();
        String command = interaction.getCommandName();

        switch(command) {
            case "Translate - DeepL":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    if (deeplEnabled) {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(DeepL.deepl(interaction.getTarget().getContent(), "en-US")).setFlags(MessageFlag.EPHEMERAL).update();
                    } else {
                        interactionOriginalResponseUpdater.setContent("DeepL translation is disabled.").setFlags(MessageFlag.EPHEMERAL).update();
                    }
                });
                break;
            case "Translate - Google Translate":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> interactionOriginalResponseUpdater.setContent("").addEmbed(Trnsl.trnsl(interaction.getTarget().getContent(), Language.ENGLISH)).setFlags(MessageFlag.EPHEMERAL).update());
                break;
        }
    }
}
