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
        String clientLang = interaction.getUserLocale().getLocale();
        Language targetLang = Language.ENGLISH;
        String dlLang = "en-US";

        switch (clientLang) {
            //this is in order of the languages discord supports in alphabetical order.
            //the case contains what discord calls it.
            case "bg": //Bulgarian Bulgaria
                targetLang = Language.BULGARIAN;
                dlLang = "bg";
                break;
            case "zh-CN": //China Chinese Simplified
                targetLang = Language.CHINESE_SIMPLIFIED;
                dlLang = "zh";
                break;
            //deepl does not have croatian
            case "hr": //Croatian Croatia
                targetLang = Language.CROATIAN;
                break;
            case "zh_TW": //Taiwan Chinese Traditional
                targetLang = Language.CHINESE_TRADITIONAL;
                dlLang = "zh";
                break;
            case "cs": //Czech Czech
                targetLang = Language.CZECH;
                dlLang = "cs";
                break;
            case "da": //Danish Denmark
                targetLang = Language.DANISH;
                dlLang = "da";
                break;
            case "nl": //Dutch Netherlands
                targetLang = Language.DUTCH;
                dlLang = "nl";
                break;
            case "en-GB": //United Kingdom English
                targetLang = Language.ENGLISH;
                dlLang = "en-GB";
                break;
            case "en-US": //USA English
                targetLang = Language.ENGLISH;
                dlLang = "en-US";
                break;
            case "fi": //Finnish Finland
                targetLang = Language.FINNISH;
                dlLang = "fi";
                break;
            case "fr": //France French
                targetLang = Language.FRENCH;
                dlLang = "fr";
                break;
            case "de": //Germany German
                targetLang = Language.GERMAN;
                dlLang = "de";
                break;
            case "el": //Greek Greece
                targetLang = Language.GREEK;
                dlLang = "el";
                break;
            //deepl does not have hindi
            case "hi": //Hindi India
                targetLang = Language.HINDI;
                break;
            case "hu": //Hungarian Hungary
                targetLang = Language.HUNGARIAN;
                dlLang = "hu";
                break;
            case "id": //Indonesian Indonesia
                targetLang = Language.INDONESIAN;
                dlLang = "id";
                break;
            case "it": //Italy Italian
                targetLang = Language.ITALIAN;
                dlLang = "it";
                break;
            case "ja": //Japan Japanese
                targetLang = Language.JAPANESE;
                dlLang = "ja";
                break;
            case "ko": //South Korea Korean
                targetLang = Language.KOREAN;
                dlLang = "ko";
                break;
            case "lt": //Lithuanian Lithuania
                targetLang = Language.LITHUANIAN;
                dlLang = "lt";
                break;
            case "no": //Norwegian Norway
                targetLang = Language.NORWEGIAN;
                dlLang = "nb";
                break;
            case "pl": //Poland Polish
                targetLang = Language.POLISH;
                dlLang = "pl";
                break;
            case "pt-BR": //Brazil Portuguese
                targetLang = Language.PORTUGUESE;
                dlLang = "pt-BR";
                break;
            case "ro": //Romanian Romania
                targetLang = Language.ROMANIAN;
                dlLang = "ro";
                break;
            case "ru": //Russia Russian
                targetLang = Language.RUSSIAN;
                dlLang = "ru";
                break;
            case "es": //Mexico Spanish
                targetLang = Language.SPANISH;
                dlLang = "es";
                break;
            case "sv": //Swedish Sweden
                targetLang = Language.SWEDISH;
                dlLang = "sv";
                break;
            //deepl does not have thai
            case "th": //Thai Thailand
                targetLang = Language.THAI;
                break;
            case "tr": //Turkish Turkey
                targetLang = Language.TURKISH;
                dlLang = "tr";
                break;
            case "uk": //Ukrainian Ukraine
                targetLang = Language.UKRAINIAN;
                dlLang = "uk";
                break;
            //deepl does not have vietnamese
            case "vi": //Vietnamese Vietnam
                targetLang = Language.VIETNAMESE;
                break;
            default:
                targetLang = Language.ENGLISH;
                dlLang = "en-US";
                break;
        }

        switch (command) {
            case "Translate - DeepL": {
                interaction.deferReply(true).queue();
                InteractionHook hook = interaction.getHook();
                if (deeplEnabled) {
                    hook.sendMessageEmbeds(Trnsl.deepl(interaction.getTarget(), dlLang).build()).queue();
                } else {
                    hook.sendMessage("DeepL Translation is disabled.").queue();
                }
                }
                break;
            case "Translate - Google Translate": {
                interaction.deferReply(true).queue();
                InteractionHook hook = interaction.getHook();
                hook.sendMessageEmbeds(Trnsl.google(interaction.getTarget(), targetLang).build()).queue();
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
