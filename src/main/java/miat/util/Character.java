package miat.util;

import miat.filehandler.Reader;
import miat.filehandler.Writer;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static miat.MiatInit.characterList;

public class Character {
    public static String getContext(String[] characterNames) {
        JSONObject obj = new JSONObject(characterList);
        StringBuilder context = new StringBuilder();
        boolean kfContextAdded = false;
        String properName;
        for (String individual : characterNames) {
            properName = getName(individual);
            if (kfContextAdded == false && obj.getJSONObject(properName).getBoolean("kfChar")) {
                context.insert(0,"Friends are human girls with animal-like features. There are no male Friends, they are female. They are animals that have transformed " +
                        "into human girls through a substance called Sandstar. While retaining influences from their animal " +
                        "form, such as ears, tail, wings (for bird friends), and personality traits, friends do not have paws, " +
                        "claws, or fur and are fully human. Friends consider themselves as human, but recognize their animal influences. There are friends representing both extant and extinct animal species. " +
                        "Sandstar, a mineral, interacts with animals to create friends in their human form. Japari Park is an island " +
                        "safari that encompasses various biomes and ecosystems, housing the corresponding animal and plant life. Japari Park features an amusement area, housing zones, and biome-specific spots for guests and employees to live in. At the " +
                        "center of the park is a large mountain, which generates Sandstar at its peak with crystalline structures extending " +
                        "beyond it. Celliens are cell-like alien creatures with the sole goal of consuming friends, causing them to revert to " +
                        "their animal form.\n\n");
                kfContextAdded = true;
            }
            context.append(properName).append("- ").append(obj.getJSONObject(properName).getString("description")).append("\n");
        }
        return context.toString();
    }

    public static boolean inList(String characterName) {
        JSONObject obj = new JSONObject(characterList);
        return obj.has(getName(characterName));
    }

    public static String getList() {
        Iterator<String> obj = new JSONObject(characterList).keys();
        ArrayList<String> list = new ArrayList<>();
        while (obj.hasNext()) {
            list.add(obj.next());
        }
        Collections.sort(list);

        return String.join(", ", list);
    }

    public static String getName(String name) {
        //take in the uncapitalized name, and return the proper capitalized and spaced version
        String[] names = getList().split(", ");
        for (String individual : names) {
            if ((individual.replace(" ","").replace("-","")).equalsIgnoreCase((name.replace(" ","").replace("-","")))) {
                //remove spaces and hyphens from both the input name and the name from the list we are testing against,
                //then compare those two in lowercase. it needs to be like this because the 'individual' string cant be modified
                return individual;
            }
        }
        return null;
    }

    public static BufferedImage getImage(String[] names) {
        JSONObject obj = new JSONObject(characterList);
        BufferedImage outputImage = null;
        String properName;
        try {
            String[] fileName = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                properName = getName(names[i]);
                String filePath = obj.getJSONObject(properName).getString("image");
                if (filePath.isEmpty()) {                       //if the "image" value in characters.json is equal to ""
                    fileName[i] = "CharacterImages/NoImage.png";//set it to a stand-in PNG so its not just empty space
                } else {
                    fileName[i] = filePath;
                }
            }
            //background switching depending on hour of day. returns 1-8 depending on timeslot. idk how this works, youchat made it
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
            g2d.setFont(new Font("Noto Sans", Font.PLAIN, 16));
            g2d.setColor(Color.GRAY);
            g2d.drawString(timeSlot + "/12", 5, 20);

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
        return outputImage;
    }

    public static String getDescription(String name) {
        //the difference from getContext() is that this one ONLY gets the description, no KF information is included.
        JSONObject obj = new JSONObject(characterList);
        String properName = getName(name);
        return obj.getJSONObject(properName).getString("description");
    }

    public static String add(SlashCommandInteraction interaction) {
        String name = interaction.getArgumentStringValueByName("name").get();
        String description = interaction.getArgumentStringValueByName("description").get();
        boolean kfChar = interaction.getArgumentBooleanValueByName("kfchar").get();
        String imageURL = interaction.getArgumentStringValueByName("imagepath").orElse("");

        String json = Reader.readFull("ServerFiles/characters.json");
        JSONObject jsonObject = new JSONObject(json);

        JSONObject newEntry = new JSONObject();
        newEntry.put("kfChar", kfChar);
        newEntry.put("description", description);
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
}
