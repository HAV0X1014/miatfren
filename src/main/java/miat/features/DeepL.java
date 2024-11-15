package miat.features;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

import static miat.MiatInit.deepLTranslator;

public class DeepL {
    public static EmbedBuilder deepl(String textToTranslate, String targetLang) {
        TextResult translatedText = null;
        try {
            translatedText = deepLTranslator.translateText(textToTranslate, null, targetLang);
        } catch (DeepLException | InterruptedException e) {
            return null;
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Translated Text - DeepL");
        e.setColor(Color.CYAN);
        e.setFooter(translatedText.getDetectedSourceLanguage() + " -> " + targetLang + "\n(Use ❌ to remove this message.)");
        e.setDescription(translatedText.getText());

        return e;
    }
}
