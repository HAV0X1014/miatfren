package miat.features;

import miat.filehandler.Reader;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AnimalFact {
    public static EmbedBuilder animalFact() {
        String fact = Reader.getRandomLine("ServerFiles/AnimalFacts.txt");
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Random Animal Fact");
        e.setColor(Color.BLUE);
        e.setAuthor("Facts from factretriever.com","https://www.factretriever.com/animal-facts","https://cdn.discordapp.com/attachments/1100888255483875428/1106945626761076777/image.png");
        e.setDescription(fact);
        return e;
    }
}
