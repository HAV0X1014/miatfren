package miat.listener;

import me.bush.translator.Language;
import miat.features.EightBall;
import miat.features.Trnsl;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import static miat.MiatMain.deeplEnabled;

public class ContextCommandHandler extends ListenerAdapter {
    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent interaction) {
        String command = interaction.getCommandString();

        switch (command) {
            case "Translate - DeepL": {
                interaction.deferReply(true).queue();
                InteractionHook hook = interaction.getHook();
                if (deeplEnabled) {
                    hook.sendMessageEmbeds(Trnsl.deepl(interaction.getTarget(), "en-US").build()).queue();
                } else {
                    hook.sendMessage("DeepL Translation is disabled.").queue();
                }
                }
                break;
            case "Translate - Google Translate": {
                interaction.deferReply(true).queue();
                InteractionHook hook = interaction.getHook();
                hook.sendMessageEmbeds(Trnsl.google(interaction.getTarget(), Language.ENGLISH).build()).queue();
                }
                break;
            case "8 Ball":
                interaction.replyEmbeds(EightBall.eightBall(interaction.getTarget().getContentRaw()).build()).queue();
                break;
            default:
                interaction.reply("unknown command, ping HAV0X").setEphemeral(true).queue();
        }
    }
}
