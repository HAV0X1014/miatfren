package miat.FunFeatures;

import miat.FileHandlers.ReadRandom;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class AnimalFact {
    public static EmbedBuilder animalFact() {
        String fact = ReadRandom.read("ServerFiles/AnimalFacts.txt");
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Random Animal Fact");
        e.setAuthor("Facts from factretriever.com","https://www.factretriever.com/animal-facts","https://cdn.discordapp.com/attachments/1100888255483875428/1106945626761076777/image.png");
        e.setDescription(fact);

        return e;
    }
}
