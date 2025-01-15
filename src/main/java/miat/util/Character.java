package miat.util;

import miat.filehandler.Reader;
import miat.filehandler.UserHandler;
import miat.filehandler.Writer;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static miat.MiatMain.characterList;

public class Character {
    private static JSONObject getAllCharacters(String userId) {
        JSONObject allCharacters = new JSONObject(characterList); // Copy main character list
        JSONObject userCharacters = UserHandler.getObject("CustomCharacters", userId);
        // Merge user-added characters into allCharacters, will overwrite if existing in main
        for (String key : userCharacters.keySet()) {
            allCharacters.put(key, userCharacters.getJSONObject(key));
        }
        return allCharacters;
    }

    public static String getContext(String[] characterNames, String userId) {
        JSONObject obj = getAllCharacters(userId);
        StringBuilder context = new StringBuilder();
        boolean kfContextAdded = false;
        String properName;
        for (String individual : characterNames) {
            properName = getName(individual, userId);
            //if (properName == null) continue; something gemini added, wtf?
            if (!kfContextAdded && obj.getJSONObject(properName).getBoolean("kfChar")) {
                context.insert(0,
                "World:\n" +
                    "Japari Park is an island safari featuring various biomes and ecosystems, with corresponding animal and plant life, as well as an amusement area, housing, and urban areas. " +
                    "At the center of the park is a large mountain that generates a mineral called Sandstar, which has the ability to transform animals into human girls known as Friends.\n" +
                    "Friends:\n" +
                    "A Friend is a human girl with animal-like features, created when an animal interacts with Sandstar. " +
                    "They retain traits from their original form, such as ears, tails, wings, and personality traits, but do not have paws, claws, or fur. " +
                    "Friends consider themselves human and acknowledge their animal influences, with representatives from both extant and extinct species.\n" +
                    "Additional details:\n" +
                    "All Friends are female, with no male counterparts. " +
                    "An occasional threat to Friends are Celliens, cell-like alien creatures that aim to consume Friends, causing them to revert to their original animal form." +
                    "\n\n"
                );
                kfContextAdded = true;
            }
            context.append(properName).append(" - ").append(obj.getJSONObject(properName).getString("description")).append(" ")
                    .append(obj.getJSONObject(properName).getString("appearance")).append("\n");
        }
        return context.toString();
    }

    public static boolean inList(String characterName, String userId) {
        JSONObject obj = getAllCharacters(userId);
        return obj.has(getName(characterName, userId));
    }

    public static String getList(String userId) {
        JSONObject obj = getAllCharacters(userId);
        //gets a sorted list of characters
        Iterator<String> keys = obj.keys();
        ArrayList<String> list = new ArrayList<>();
        while (keys.hasNext()) {
            list.add(keys.next());
        }
        Collections.sort(list);
        return String.join(", ", list);
    }

    public static String getName(String name, String userId) {
        String[] names = getList(userId).split(", ");
        for (String individual : names) {
            if ((individual.replace(" ", "").replace("-", "")).equalsIgnoreCase((name.replace(" ", "").replace("-", "")))) {
                //remove spaces and hyphens from both the input name and the name from the list we are testing against,
                //then compare those two in lowercase. it needs to be like this because the 'individual' string cant be modified
                return individual;
            }
        }
        return null;
    }

    public static byte[] getImage(String[] names, String userId) {
        JSONObject obj = getAllCharacters(userId);
        BufferedImage outputImage = null;
        String properName;
        try {
            String[] fileName = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                properName = getName(names[i], userId);
                if (properName == null) {
                    fileName[i] = "CharacterImages/NoImage.png";
                } else {
                    String filePath = obj.getJSONObject(properName).getString("image");
                    if (filePath.isEmpty()) {                       //if the "image" value in characters.json is equal to ""
                        fileName[i] = "CharacterImages/NoImage.png";//set it to a stand-in PNG so its not just empty space
                    } else {
                        fileName[i] = filePath;
                    }
                }
            }
            //background switching depending on hour of day. returns 1-12 depending on timeslot. idk how this works, youchat made it
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            int timeSlot = (hour / 2) + 1; // Divide the hour by 3 and add 1 to get the time slot

            BufferedImage backgroundImage = ImageIO.read(new File("CharacterImages/background" + timeSlot + ".png"));
            BufferedImage[] images = new BufferedImage[fileName.length];
            for (int i = 0; i < images.length; i++) {
                images[i] = ImageIO.read(new File(fileName[i]));
            }
            int backgroundWidth = backgroundImage.getWidth();
            int backgroundHeight = backgroundImage.getHeight();

            int numImages = Math.min(images.length, 6); // Limit the number of images to a maximum of 5

            int spacing = backgroundWidth / (numImages + 1); // Calculate the horizontal spacing between images
            int maxImageWidth = backgroundWidth / (numImages + 1); // Maximum width for each image based on spacing
            int maxImageHeight = backgroundHeight; // Maximum height for each image based on background height

            int[] imageXPositions = new int[numImages];
            for (int i = 0; i < numImages; i++) {
                imageXPositions[i] = (i + 1) * spacing - (maxImageWidth / 2); // Calculate the X position of each image
            }

            outputImage = new BufferedImage(backgroundWidth, backgroundHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(backgroundImage, 0, 0, null);
            g2d.setFont(new Font("Noto Sans", Font.PLAIN, 24));
            g2d.setColor(Color.GRAY);
            g2d.drawString(timeSlot + "/12", 15, 35);

            for (int i = 0; i < numImages; i++) {
                // Scale the image to fit within the available space while maintaining the aspect ratio
                int imageWidth = images[i].getWidth();
                int imageHeight = images[i].getHeight();
                double scalingFactor = Math.min((double) maxImageWidth / imageWidth, (double) maxImageHeight / imageHeight);
                int scaledImageWidth = (int) (imageWidth * scalingFactor);
                int scaledImageHeight = (int) (imageHeight * scalingFactor);
                Image scaledImage = images[i].getScaledInstance(scaledImageWidth, scaledImageHeight, Image.SCALE_SMOOTH);

                // Calculate the position to center the scaled image
                int scaledImageX = imageXPositions[i] + (maxImageWidth - scaledImageWidth) / 2;
                int scaledImageY = (maxImageHeight - scaledImageHeight) / 2;

                g2d.drawImage(scaledImage, scaledImageX, scaledImageY, null);
            }
            g2d.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(outputImage,"jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public static String getDescription(String name, String userId) {
        JSONObject obj = getAllCharacters(userId);
        String properName = getName(name, userId);
        return obj.getJSONObject(properName).getString("description");
    }

    public static String getAppearance(String name, String userId) {
        JSONObject obj = getAllCharacters(userId);
        String properName = getName(name, userId);
        return obj.getJSONObject(properName).getString("appearance");
    }

    public static String add(SlashCommandInteraction interaction) {
        String name = interaction.getOption("name").getAsString();
        String description = interaction.getOption("description").getAsString();
        String appearance = interaction.getOption("appearance").getAsString();
        boolean kfChar = interaction.getOption("kfchar").getAsBoolean();
        String imageURL;
        if (interaction.getOption("imagepath") != null) {
            imageURL = interaction.getOption("imagepath").getAsString();
        } else {
            imageURL = "";
        }
        String json = Reader.readFull("ServerFiles/characters.json");
        JSONObject jsonObject = new JSONObject(json);

        JSONObject newEntry = new JSONObject();
        newEntry.put("kfChar", kfChar);
        newEntry.put("description", description);
        newEntry.put("appearance", appearance);
        if (jsonObject.has(name)) {         //if the entry for the character already exists
            if (imageURL.isEmpty()) {      //if we are not going to write a new image into the character entry
                String existingImage = "error";
                try {
                    existingImage = jsonObject.getJSONObject(name).getString("image");
                } catch (JSONException je) {
                    throw new JSONException(je);
                }
                newEntry.put("image", existingImage);
            } else {                        //if we *are* going to write a new image in, just overwrite whats there
                newEntry.put("image", imageURL);
            }
        } else {                            //if it doesnt exist (newly created character)
            newEntry.put("image", imageURL);
        }
        jsonObject.put(name,newEntry);

        Writer.write("ServerFiles/characters.json", jsonObject.toString(2));
        return "Character ``" + name + "`` added.";
    }

    public static String addCustom(SlashCommandInteraction interaction) {
        String name = interaction.getOption("name").getAsString();
        String description = interaction.getOption("description").getAsString();
        String appearance = interaction.getOption("appearance").getAsString();
        boolean kfChar = interaction.getOption("kfchar").getAsBoolean();
        String imageURL = ""; //always blank because users arent allowed to put image urls

        String json = Reader.readFull("ServerFiles/UserConfigs/" + interaction.getUser().getId() + ".json");
        JSONObject jsonObject = new JSONObject(json);
        //if the user config doesnt have the custom characters object yet, then make one
        if (!jsonObject.has("CustomCharacters")) {
            jsonObject.put("CustomCharacters", new JSONObject());
        }

        JSONObject newEntry = new JSONObject();
        newEntry.put("kfChar", kfChar);
        newEntry.put("description", description);
        newEntry.put("appearance", appearance);
        if (jsonObject.getJSONObject("CustomCharacters").has(name)) {         //if the entry for the character already exists
            if (imageURL.isEmpty()) {      //if we are not going to write a new image into the character entry
                String existingImage = "error";
                try {
                    existingImage = jsonObject.getJSONObject("CustomCharacters").getJSONObject(name).getString("image");
                } catch (JSONException je) {
                    throw new JSONException(je);
                }
                newEntry.put("image", existingImage);
            } else {                        //if we *are* going to write a new image in, just overwrite whats there
                newEntry.put("image", imageURL);
            }
        } else {                            //if it doesnt exist (newly created character)
            newEntry.put("image", imageURL);
        }
        jsonObject.getJSONObject("CustomCharacters").put(name,newEntry);
        //put it back in the user's config
        Writer.write("ServerFiles/UserConfigs/" + interaction.getUser().getId() + ".json", jsonObject.toString(1));
        return "Custom character ``" + name + "`` for " + interaction.getUser().getId() + " added.";
    }

    public static String deleteCustom(SlashCommandInteraction interaction) {
        String name = interaction.getOption("name").getAsString();
        String json = Reader.readFull("ServerFiles/UserConfigs/" + interaction.getUser().getId() + ".json");
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.getJSONObject("CustomCharacters").has(name)) {
            jsonObject.getJSONObject("CustomCharacters").remove(name);
            Writer.write("ServerFiles/UserConfigs/" + interaction.getUser().getId() + ".json", jsonObject.toString(1));
            return "Deleted character ``" + name + "``.";
        } else {
            return "``" + name + "`` is not deletable, or is not a valid character.";
        }
    }
}
