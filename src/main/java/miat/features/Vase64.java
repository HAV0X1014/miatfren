package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Base64;

public class Vase64 {
    public static EmbedBuilder vase64(String parts) {
        EmbedBuilder e = new EmbedBuilder();
        String result = "Unable to process, use encode or decode followed by a string to encode/decode it.";
        if (parts.startsWith("encode ")) {
            parts = parts.replace("encode ", "");
            result = Base64.getEncoder().encodeToString(parts.getBytes());
            e.setFooter("Encode Result");
            e.setColor(Color.RED);
        } else
        if (parts.startsWith("decode ")) {
            parts = parts.replace("decode ", "");
            byte[] decoded = Base64.getDecoder().decode(parts);
            result = new String(decoded);
            e.setFooter("Decode Result");
            e.setColor(Color.BLUE);
        }
        e.setTitle("Base64");
        e.setDescription(result);
        return e;
    }
}
