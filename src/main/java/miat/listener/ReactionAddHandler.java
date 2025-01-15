package miat.listener;

import me.bush.translator.Language;
import miat.features.Trnsl;
import miat.filehandler.ServerHandler;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static miat.MiatMain.*;

public class ReactionAddHandler extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent ra) {
        String reactedInChannel = ra.getReaction().getChannelId();
        //if the channel that the emoji was added in is a disallowed channel (set by server admin) then stop processing entirely.
        if (ra.isFromGuild()) {
            for (String disallowedChannel : ServerHandler.getTranslatorIgnoredChannels(ra.getGuild().getId())) {
                if (disallowedChannel.equals(reactedInChannel)) {
                    return;
                }
            }
        }
        String emoji = "";
        try {
            emoji = ra.getEmoji().asUnicode().getAsReactionCode();
        } catch (IllegalStateException ignored) {}

        String messageContent = ra.retrieveMessage().complete().getContentRaw();
        String deleteCandidate = ra.getMessageAuthorId();

        Language targetLang;
        boolean useDeeplRequested = false;
        String dlLang = null;

        //gets the list of reactions on the message, and checks if any of them are the deepL engagement emoji
        for (MessageReaction reaction : ra.retrieveMessage().complete().getReactions()) {
            if (reaction.getEmoji().equals(Emoji.fromFormatted(deepLEmoji))) {
                useDeeplRequested = true;
            }
        }

        switch (emoji) {
            case "❌":
                if (deleteCandidate.equals(self)) { //self delete when X emoji is seen
                    if (messageContent.contains("__**Racial slurs are discouraged!**__")) {
                        ra.retrieveMessage().submit().join().delete().queue();
                    }
                    //self delete checks for both kinds of translation messages, plain text and embed respectively
                    try {
                        if (ra.retrieveMessage().complete().getEmbeds().get(0).getTitle().startsWith("Translated Text")) {
                            ra.retrieveMessage().submit().join().delete().queue();
                        }
                        if (ra.retrieveMessage().complete().getEmbeds().get(0).getFooter().getText().contains("[TRANSLATED -")) {
                            ra.retrieveMessage().submit().join().delete().queue();
                        }
                    } catch (Exception ignored) {}
                }
                return;

            case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
            case "\uD83C\uDDE8\uD83C\uDDE6": //Canada English
            case "\uD83C\uDDFB\uD83C\uDDEE": //US Virgin Islands English
            case "\uD83C\uDDEC\uD83C\uDDFA": //Guam English
                targetLang = Language.ENGLISH;
                dlLang = "en-US";
                break;
            case "\uD83C\uDDE6\uD83C\uDDFA": //Australia English
            case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
            case "\uD83C\uDDF3\uD83C\uDDFF": //New Zealand English
            case "\uD83C\uDDFB\uD83C\uDDEC": //British Virgin Islands English
            case "\uD83C\uDDEC\uD83C\uDDEE": //Gibraltar English
            case "\uD83C\uDDEE\uD83C\uDDF2": //Isle of Man English
                targetLang = Language.ENGLISH;
                dlLang = "en-GB";
                break;
            case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                targetLang = Language.POLISH;
                dlLang = "pl";
                break;
            case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
            case "\uD83C\uDDF8\uD83C\uDDEC": //Singapore Chinese Simplified
                targetLang = Language.CHINESE_SIMPLIFIED;
                dlLang = "zh";
                break;
            case "\uD83C\uDDED\uD83C\uDDF0": //Hong Kong Chinese Traditional
            case "\uD83C\uDDF9\uD83C\uDDFC": //Taiwan Chinese Traditional
                targetLang = Language.CHINESE_TRADITIONAL;
                dlLang = "zh";
                break;
            case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
            case "\uD83C\uDDF5\uD83C\uDDF0": //Pakistan Hindi
                targetLang = Language.HINDI;
                break;
            case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
            case "\uD83C\uDDE8\uD83C\uDDF4": //Colombia Spanish
            case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
            case "\uD83C\uDDE6\uD83C\uDDF7": //Argentina Spanish
            case "\uD83C\uDDF5\uD83C\uDDEA": //Peru Spanish
            case "\uD83C\uDDFB\uD83C\uDDEA": //Venezuela Spanish
            case "\uD83C\uDDE8\uD83C\uDDF1": //Chile Spanish
            case "\uD83C\uDDEC\uD83C\uDDF9": //Guatemala Guatemala
            case "\uD83C\uDDF5\uD83C\uDDF7": //Puerto Rico Spanish
                targetLang = Language.SPANISH;
                dlLang = "es";
                break;
            case "\uD83C\uDDEB\uD83C\uDDF7": //France French
            case "\uD83C\uDDF2\uD83C\uDDEC": //Madagascar French
            case "\uD83C\uDDE8\uD83C\uDDF2": //Cameroon French
            case "\uD83C\uDDE8\uD83C\uDDEE": //Cote d' Ivoire French
            case "\uD83C\uDDF3\uD83C\uDDEA": //Niger French
            case "\uD83C\uDDE7\uD83C\uDDEF": //Benin French
                targetLang = Language.FRENCH;
                dlLang = "fr";
                break;
            case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
            case "\uD83C\uDDE7\uD83C\uDDFE": //Belarus Russian
                targetLang = Language.RUSSIAN;
                dlLang = "ru";
                break;
            case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
            case "\uD83C\uDDF2\uD83C\uDDFF": //Mozambique Portuguese
                targetLang = Language.PORTUGUESE;
                dlLang = "pt-PT";
                break;
            case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                targetLang = Language.PORTUGUESE;
                dlLang = "pt-BR";
                break;
            case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
            case "\uD83C\uDDF1\uD83C\uDDEE": //Liechtenstein German
                targetLang = Language.GERMAN;
                dlLang = "de";
                break;
            case "\uD83C\uDDEF\uD83C\uDDF5": //Japan Japanese
                targetLang = Language.JAPANESE;
                dlLang = "ja";
                break;
            case "\uD83C\uDDF5\uD83C\uDDED": //Philippines Filipino
                targetLang = Language.FILIPINO;
                break;
            case "\uD83C\uDDF0\uD83C\uDDF7": //South Korea Korean
            case "\uD83C\uDDF0\uD83C\uDDF5": //North Korea Korean
                targetLang = Language.KOREAN;
                dlLang = "ko";
                break;
            case "\uD83C\uDDFB\uD83C\uDDF3": //Vietnam Vietnamese
                targetLang = Language.VIETNAMESE;
                break;
            case "\uD83C\uDDEE\uD83C\uDDF9": //Italy Italian
                targetLang = Language.ITALIAN;
                dlLang = "it";
                break;
            case "\uD83C\uDDF2\uD83C\uDDFE": //Malaysia Malay
            case "\uD83C\uDDE7\uD83C\uDDF3": //Brunei Malay
                targetLang = Language.MALAY;
                break;
            default:
                return;
        }

        if (useDeeplRequested && deeplEnabled) {
            //if the deepL toggle emoji is present, and deepL is ok to be used
            if (dlLang == null) { //some languages aren't on DeepL. If the language isn't supported, it will be null because it wasn't set in the switch case
                if (useGoogleAsFallbackForDeepL) { //if DeepL isn't able to translate into the language, use Google as a fallback translator.
                    ra.getChannel().sendMessageEmbeds(Trnsl.google(ra.retrieveMessage().complete(), targetLang).build()).setMessageReference(ra.getMessageId()).mentionRepliedUser(false).queue();
                } else {
                    ra.getChannel().sendMessage("This language is not supported by DeepL.").queue();
                }
            } else {
                //runs if checks pass
                ra.getChannel().sendMessageEmbeds(Trnsl.deepl(ra.retrieveMessage().complete(), dlLang).build()).setMessageReference(ra.getMessageId()).mentionRepliedUser(false).queue();
            }
        } else {
            //runs if the deepL toggle emoji isnt present and/or deepL is not ok to be used
            ra.getChannel().sendMessageEmbeds(Trnsl.google(ra.retrieveMessage().complete(), targetLang).build()).setMessageReference(ra.getMessageId()).mentionRepliedUser(false).queue();
        }
    }
}
