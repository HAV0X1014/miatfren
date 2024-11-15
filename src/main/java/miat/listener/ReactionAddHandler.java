package miat.listener;

import me.bush.translator.Language;
import miat.features.DeepL;
import miat.features.Trnsl;
import miat.filehandler.ServerHandler;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import static miat.MiatInit.*;

public class ReactionAddHandler implements ReactionAddListener {
    @Override
    public void onReactionAdd(ReactionAddEvent ra) {
        if (ra.getServer().isPresent()) {
            String reactedInChannel = ra.getChannel().getIdAsString();
            //if the channel that the emoji was added in is a disallowed channel (set by server admin) then stop processing entirely.
            for (String disallowedChannel : ServerHandler.getTranslatorIgnoredChannels(ra.getServer().get().getIdAsString())) {
                if (disallowedChannel.equals(reactedInChannel)) {
                    return;
                }
            }

            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");
            String allEmoji = ra.requestMessage().join().getReactions().toString();
            String messageContent = ra.requestMessage().join().getContent();
            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            Language targetLang;
            String dlLang = null;

            switch (emoji) {
                case "❌":
                    if (deleteCandidate.equals(self.getIdAsString())) { //self delete when X emoji is seen
                        if (messageContent.contains("__**Racial slurs are discouraged!**__")) {
                            api.getMessageById(ra.getMessageId(), ra.getChannel()).join().delete();
                        }
                        try {
                            if (ra.requestMessage().join().getEmbeds().get(0).getTitle().get().startsWith("Translated Text")) { //only delete if it is a translation message
                                api.getMessageById(ra.getMessageId(), ra.getChannel()).join().delete();
                            }
                        } catch (Exception ignored) {
                        }
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

            if (allEmoji.contains(deepLEmoji)) {
                if (deeplEnabled) { //Check for the DeepL emoji (the one that says to use DeepL if reacted)
                    if (dlLang == null) { //some languages aren't on DeepL. If the language isn't supported, it will be null because it wasn't set in the switch case
                        if (useGoogleAsFallbackForDeepL) { //if DeepL isn't able to translate into the language, use Google as a fallback translator.
                            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel());
                        } else {
                            ra.getChannel().sendMessage("This language is not supported by DeepL.");
                        }
                    } else {
                        new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(DeepL.deepl(messageContent, dlLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel()); //Translate if successful
                    }
                } else {
                    new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel()); //use Google if DeepL isnt enabled.
                }
            } else {
                new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel()); //use Google if there isnt a DeepL emoji.
            }
        }
    }
}
