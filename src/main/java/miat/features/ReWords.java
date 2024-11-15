package miat.features;

import miat.filehandler.UserHandler;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReWords {
    public static void scoreModifier(MessageCreateEvent mc, int updown) {
        String userID = mc.getMessageAuthor().getIdAsString();
        int score = UserHandler.getInt("ReWordsScore",userID);
        score = score + updown;
        UserHandler.write("ReWordsScore", score, userID);
    }
    public static void pointCheck(SlashCommandInteraction interaction) {
        String userID = interaction.getUser().getIdAsString();
        int score = UserHandler.getInt("ReWordsScore",userID);
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Point Check");
        e.addField("\u200b","<@" + userID + ">\nScore : ``" + score + "``!\nIncrease it by saying the secret phrases right!");
        e.setColor(Color.red);

        interaction.createImmediateResponder().setContent("").addEmbed(e).respond();
    }

    public static void user(SlashCommandInteraction interaction) {
        String userID = interaction.getArgumentUserValueByIndex(0).get().getIdAsString();
        int score = UserHandler.getInt("ReWordsScore",userID);
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Point Check");
        e.addField("\u200b", "<@" + userID + ">\nScore : ``" + score + "``\nIncrease it by saying the secret phrases right!");
        e.setColor(Color.red);

        interaction.createImmediateResponder().setContent("").addEmbed(e).respond();
    }

    public static void top(SlashCommandInteraction interaction) {
        File userFilesDir = new File("ServerFiles/UserConfigs");
        Map<String, Integer> userScores = new HashMap<>();
        File[] fileList = userFilesDir.listFiles((dir,name) -> name.endsWith(".json"));

        if (fileList != null) {
            for (File config : fileList) {
                String userID = config.getName().replace(".json","");
                int score = UserHandler.getInt("ReWordsScore", userID);
                userScores.put(userID, score);
            }
        }

        Map<String, Integer> sortedUserScores = userScores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        String[] topScorers = new String[5];

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedUserScores.entrySet()) {
            if (count < 5) {
                String userID = entry.getKey();
                Integer score = entry.getValue();
                topScorers[count] = "\\#" + (count + 1) + " <@" + userID + ">: " + score;
                count++;
            } else {
                break; // Exit the loop after processing the first 5 entries
            }
        }
        StringBuilder result = new StringBuilder();
        for (String entry : topScorers) {
            result.append(entry).append("\n");
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Top ReWords Scorers");
        e.setDescription(result.toString());
        e.setFooter("Earn points by saying the secret phrases correctly!");
        e.setColor(Color.ORANGE);

        interaction.createImmediateResponder().setContent("").addEmbed(e).respond();
    }
}
