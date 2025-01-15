package miat.features;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import me.bush.translator.Language;
import me.bush.translator.Translation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
            if (sourceEmbed.getAuthor() != null) {translatedEmbed.setAuthor(translator.translateBlocking(sourceEmbed.getAuthor().getName(), targetLang, Language.AUTO).getTranslatedText());}
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
}
