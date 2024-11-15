package miat.features;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

public class SuperMagicNumber {
    public static EmbedBuilder superMagic(String userID) {
        EmbedBuilder e = new EmbedBuilder();

        File imageFile = new File("ServerFiles/super happy exciting blank background.jpg");
        try {
            BufferedImage image = ImageIO.read(imageFile);
            Graphics2D g2d = image.createGraphics();
            Font font = new Font("Arial", Font.BOLD, 72);
            Color color = Color.BLACK;
            g2d.setFont(font);
            g2d.setColor(color);

            BigInteger bigInt = new BigInteger(userID);
            BigInteger result = bigInt.mod(BigInteger.valueOf(8193))
                    .multiply(BigInteger.valueOf(LocalDate.now().get(WeekFields.ISO.weekOfWeekBasedYear())))
                    .add(BigInteger.valueOf(11))
                    .mod(BigInteger.valueOf(8193));

            String text = String.valueOf(result);
            g2d.drawString(text, 150,210);
            e.setTitle("Your Magic Number for this week!");
            e.setDescription("This is your special magic number. It is all yours!");
            e.setImage(image);
            return e;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
