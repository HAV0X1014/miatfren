package miat.features;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import me.bush.translator.Language;
import me.bush.translator.Translation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;

import static miat.MiatMain.deepLTranslator;
import static miat.MiatMain.translator;

public class Trnsl {
    public static EmbedBuilder google(Message message, Language targetLang) {
        //this feature has been rewritten.
        //it hurts.
        //this feature hurts me.
        //anything for unity.
        EmbedBuilder translatedEmbed = new EmbedBuilder();
        if (!message.getEmbeds().isEmpty()) {
            MessageEmbed sourceEmbed = message.getEmbeds().get(0);
            if (sourceEmbed.getTitle() != null) {translatedEmbed.setTitle(translator.translateBlocking(sourceEmbed.getTitle(), targetLang, Language.AUTO).getTranslatedText());}
            if (sourceEmbed.getAuthor() != null && sourceEmbed.getLength() < 256) {translatedEmbed.setAuthor(translator.translateBlocking(sourceEmbed.getAuthor().getName(), targetLang, Language.AUTO).getTranslatedText());}
            if (sourceEmbed.getDescription() != null) {translatedEmbed.setDescription(translator.translateBlocking(sourceEmbed.getDescription(), targetLang, Language.AUTO).getTranslatedText());}
            //we have to loop over the field count, because there can be multiple
            for (MessageEmbed.Field field : sourceEmbed.getFields()) {
                //copy the values of the embed, but translate the actual content
                translatedEmbed.addField(
                        translator.translateBlocking(field.getName(), targetLang, Language.AUTO).getTranslatedText(),
                        translator.translateBlocking(field.getValue(), targetLang, Language.AUTO).getTranslatedText(),
                        field.isInline()
                );
            }
            if (sourceEmbed.getFooter() != null) {translatedEmbed.setFooter(translator.translateBlocking(sourceEmbed.getFooter().getText(), targetLang, Language.AUTO).getTranslatedText() + " [TRANSLATED - Google]", null);
            } else {
                //always note that the embed is a translated one in the footer
                translatedEmbed.setFooter("[TRANSLATED - Google]");
            }
            translatedEmbed.setColor(new Color(46, 125, 50));
        } else {
            Translation tr = translator.translateBlocking(message.getContentRaw(), targetLang, Language.AUTO);
            translatedEmbed.setTitle("Translated Text - Google Translate");
            translatedEmbed.setColor(new Color(46, 125, 50));
            translatedEmbed.setFooter(tr.getSourceLanguage() + " -> " + tr.getTargetLanguage() + "\n(Use ❌ to remove this message.)");
            translatedEmbed.setDescription(tr.getTranslatedText());
        }
        return translatedEmbed;
    }

    public static EmbedBuilder deepl(Message message, String targetLang) {
        EmbedBuilder translatedEmbed = new EmbedBuilder();
        //check if message has (rich) embed, or if it is plain message
        //embed translation by checking if the message has a value set for each text location
        try {
            if (!message.getEmbeds().isEmpty()) {
                MessageEmbed sourceEmbed = message.getEmbeds().get(0);
                if (sourceEmbed.getTitle() != null) {translatedEmbed.setTitle(deepLTranslator.translateText(sourceEmbed.getTitle(), null, targetLang).getText());}
                if (sourceEmbed.getAuthor() != null) {translatedEmbed.setAuthor(deepLTranslator.translateText(sourceEmbed.getAuthor().getName(), null, targetLang).getText());}
                if (sourceEmbed.getDescription() != null) {translatedEmbed.setDescription(deepLTranslator.translateText(sourceEmbed.getDescription(), null, targetLang).getText());}
                //we have to loop over the field count, because there can be multiple
                for (MessageEmbed.Field field : sourceEmbed.getFields()) {
                    //copy the values of the embed, but translate the actual content
                    translatedEmbed.addField(
                            deepLTranslator.translateText(field.getName(), null, targetLang).getText(),
                            deepLTranslator.translateText(field.getValue(), null, targetLang).getText(),
                            field.isInline()
                    );
                }
                if (sourceEmbed.getFooter() != null) {translatedEmbed.setFooter(deepLTranslator.translateText(sourceEmbed.getFooter().getText(), null, targetLang).getText() + " [TRANSLATED - DeepL]", null);
                } else {
                    //always note that the embed is a translated one in the footer
                    translatedEmbed.setFooter("[TRANSLATED - DeepL]");
                }
                translatedEmbed.setColor(Color.CYAN);
            } else {
                //plain message content translation
                TextResult translatedText = deepLTranslator.translateText(message.getContentRaw(),null,targetLang);
                translatedEmbed.setTitle("Translated Text - DeepL");
                translatedEmbed.setColor(Color.CYAN);
                translatedEmbed.setFooter(translatedText.getDetectedSourceLanguage() + " -> " + targetLang + "\n(Use ❌ to remove this message.)");
                translatedEmbed.setDescription(translatedText.getText());
            }
        } catch (DeepLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return translatedEmbed;
    }

    public static EmbedBuilder googleSend(SlashCommandInteraction interaction) {
        EmbedBuilder e = new EmbedBuilder();
        String requestedLang = interaction.getOption("language").getAsString();
        String text = interaction.getOption("text").getAsString();
        Language targetLang;

        //match the language requested in the interaction to the language code supported by the translator
        //this comes from the autocomplete language list in AutoCompleteHandler
        switch (requestedLang) {
            //this is in order of the languages discord supports in alphabetical order.
            //the case contains what discord calls it.
            case "bulgarian": //Bulgarian Bulgaria
                targetLang = Language.BULGARIAN;
                break;
            case "chinese_simplified": //China Chinese Simplified
                targetLang = Language.CHINESE_SIMPLIFIED;
                break;
            //deepl does not have croatian
            case "croatian": //Croatian Croatia
                targetLang = Language.CROATIAN;
                break;
            case "chinese_traditional": //Taiwan Chinese Traditional
                targetLang = Language.CHINESE_TRADITIONAL;
                break;
            case "czech": //Czech Czech
                targetLang = Language.CZECH;
                break;
            case "danish": //Danish Denmark
                targetLang = Language.DANISH;
                break;
            case "dutch": //Dutch Netherlands
                targetLang = Language.DUTCH;
                break;
            case "english": //United Kingdom English
                targetLang = Language.ENGLISH;
                break;
            case "finnish": //Finnish Finland
                targetLang = Language.FINNISH;
                break;
            case "french": //France French
                targetLang = Language.FRENCH;
                break;
            case "german": //Germany German
                targetLang = Language.GERMAN;
                break;
            case "greek": //Greek Greece
                targetLang = Language.GREEK;
                break;
            //deepl does not have hindi
            case "hindi": //Hindi India
                targetLang = Language.HINDI;
                break;
            case "hungarian": //Hungarian Hungary
                targetLang = Language.HUNGARIAN;
                break;
            case "indonesian": //Indonesian Indonesia
                targetLang = Language.INDONESIAN;
                break;
            case "italian": //Italy Italian
                targetLang = Language.ITALIAN;
                break;
            case "japanese": //Japan Japanese
                targetLang = Language.JAPANESE;
                break;
            case "korean": //South Korea Korean
                targetLang = Language.KOREAN;
                break;
            case "lithuanian": //Lithuanian Lithuania
                targetLang = Language.LITHUANIAN;
                break;
            case "norwegian": //Norwegian Norway
                targetLang = Language.NORWEGIAN;
                break;
            case "polish": //Poland Polish
                targetLang = Language.POLISH;
                break;
            case "portuguese": //Brazil Portuguese
                targetLang = Language.PORTUGUESE;
                break;
            case "romanian": //Romanian Romania
                targetLang = Language.ROMANIAN;
                break;
            case "russian": //Russia Russian
                targetLang = Language.RUSSIAN;
                break;
            case "spanish": //Mexico Spanish
                targetLang = Language.SPANISH;
                break;
            case "swedish": //Swedish Sweden
                targetLang = Language.SWEDISH;
                break;
            //deepl does not have thai
            case "thai": //Thai Thailand
                targetLang = Language.THAI;
                break;
            case "turkish": //Turkish Turkey
                targetLang = Language.TURKISH;
                break;
            case "ukrainian": //Ukrainian Ukraine
                targetLang = Language.UKRAINIAN;
                break;
            //deepl does not have vietnamese
            case "vietnamese": //Vietnamese Vietnam
                targetLang = Language.VIETNAMESE;
                break;
            default:
                e.setDescription("invalid language choice.");
                e.setFooter("[TRANSLATED - Google]");
                return e;
        }
        Translation tr = translator.translateBlocking(text, targetLang, Language.AUTO);
        e.setDescription("**Translated:**\n" + tr.getTranslatedText() + "\n\n**Original:**\n" + text);
        e.setFooter(tr.getSourceLanguage() + " -> " + tr.getTargetLanguage() + " [TRANSLATED - Google]");
        e.setColor(new Color(46, 125, 50));

        return e;
    }

    public static EmbedBuilder deeplSend(SlashCommandInteraction interaction) {
        EmbedBuilder e = new EmbedBuilder();
        String requestedLang = interaction.getOption("language").getAsString();
        String text = interaction.getOption("text").getAsString();
        String dlLang = "en-US";

        //match the language requested in the interaction to the language code supported by the translator
        //this comes from the autocomplete language list in AutoCompleteHandler
        switch (requestedLang) {
            //this is in order of the languages discord supports in alphabetical order.
            //the case contains what discord calls it.
            case "bulgarian": //Bulgarian Bulgaria
                dlLang = "bg";
                break;
            case "chinese": //China Chinese Simplified
                dlLang = "zh";
                break;
            case "czech": //Czech Czech
                dlLang = "cs";
                break;
            case "danish": //Danish Denmark
                dlLang = "da";
                break;
            case "dutch": //Dutch Netherlands
                dlLang = "nl";
                break;
            case "english": //United States English
                dlLang = "en-US";
                break;
            case "finnish": //Finnish Finland
                dlLang = "fi";
                break;
            case "french": //France French
                dlLang = "fr";
                break;
            case "german": //Germany German
                dlLang = "de";
                break;
            case "greek": //Greek Greece
                dlLang = "el";
                break;
            case "hungarian": //Hungarian Hungary
                dlLang = "hu";
                break;
            case "indonesian": //Indonesian Indonesia
                dlLang = "id";
                break;
            case "italian": //Italy Italian
                dlLang = "it";
                break;
            case "japanese": //Japan Japanese
                dlLang = "ja";
                break;
            case "korean": //South Korea Korean
                dlLang = "ko";
                break;
            case "lithuanian": //Lithuanian Lithuania
                dlLang = "lt";
                break;
            case "norwegian": //Norwegian Norway
                dlLang = "nb";
                break;
            case "polish": //Poland Polish
                dlLang = "pl";
                break;
            case "portuguese": //Brazil Portuguese
                dlLang = "pt-BR";
                break;
            case "romanian": //Romanian Romania
                dlLang = "ro";
                break;
            case "russian": //Russia Russian
                dlLang = "ru";
                break;
            case "spanish": //Mexico Spanish
                dlLang = "es";
                break;
            case "swedish": //Swedish Sweden
                dlLang = "sv";
                break;
            case "turkish": //Turkish Turkey
                dlLang = "tr";
                break;
            case "ukrainian": //Ukrainian Ukraine
                dlLang = "uk";
                break;
            default:
                e.setDescription("invalid language choice.");
                e.setFooter("[TRANSLATED - Google]");
                return e;
        }
        TextResult translatedText = null;
        try {
            translatedText = deepLTranslator.translateText(text,null,dlLang);
        } catch (DeepLException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        e.setDescription("**Translated:**\n" + translatedText.getText() + "\n\n**Original:**\n" + text);
        e.setFooter(translatedText.getDetectedSourceLanguage() + " -> " + dlLang + " [TRANSLATED - DeepL]");
        e.setColor(Color.CYAN);

        return e;
    }
}
