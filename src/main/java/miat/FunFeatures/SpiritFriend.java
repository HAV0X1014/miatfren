package miat.FunFeatures;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

public class SpiritFriend {
    public static EmbedBuilder spiritFriend(String userID) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Your spirit Friend!");

        String filePath = "ServerFiles/friendsNexon1.json";
        String fullText = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText += line;
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        BigInteger bigInt = new BigInteger(userID);
        BigInteger result = bigInt.mod(BigInteger.valueOf(469))
                .multiply(BigInteger.valueOf(7))
                .add(BigInteger.valueOf(7529)) //topi gave me this number. this is the one true number. this is the number
                .mod(BigInteger.valueOf(469));
        int searchID = Integer.parseInt(String.valueOf(result));

        JSONArray array = new JSONArray(fullText);
        String nameEN = null;
        boolean isFound = false;

        while (!isFound) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                if (obj.getInt("nexonID") == searchID) {
                    nameEN = obj.getString("nameEN");
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                searchID++;
            }
        }
        e.setDescription(nameEN + "\n\nThis is your spirit Friend!");
        return e;
    }
    public static EmbedBuilder friendOfTheWeek(String userID) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Your personal Friend of the week!");

        String filePath = "ServerFiles/friendsNexon1.json";
        StringBuilder fullText = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText.append(line);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        BigInteger bigInt = new BigInteger(userID);
        BigInteger result = bigInt.mod(BigInteger.valueOf(469))
                .multiply(BigInteger.valueOf(LocalDate.now().get(WeekFields.ISO.weekOfWeekBasedYear())))
                .add(BigInteger.valueOf(7))
                .mod(BigInteger.valueOf(469));
        int searchID = Integer.parseInt(String.valueOf(result));

        JSONArray array = new JSONArray(fullText.toString());
        String nameEN = null;
        boolean isFound = false;

        while (!isFound) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                if (obj.getInt("nexonID") == searchID) {
                    nameEN = obj.getString("nameEN");
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                searchID++;
            }
        }
        e.setDescription(nameEN + "\n\nYou'll get a new Friend next week, so check back soon!");
        return e;
    }
    public static EmbedBuilder friendOfTheDay(String userID) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Your personal Friend of the day!");

        String filePath = "ServerFiles/friendsNexon1.json";
        StringBuilder fullText = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText.append(line);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        BigInteger bigInt = new BigInteger(userID);
        BigInteger result = bigInt.mod(BigInteger.valueOf(469))
                .multiply(BigInteger.valueOf(LocalDate.now().getDayOfYear()))
                .add(BigInteger.valueOf(7))
                .mod(BigInteger.valueOf(469));
        int searchID = Integer.parseInt(String.valueOf(result));

        JSONArray array = new JSONArray(fullText.toString());
        String nameEN = null;
        boolean isFound = false;

        while (!isFound) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                if (obj.getInt("nexonID") == searchID) {
                    nameEN = obj.getString("nameEN");
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                searchID++;
            }
        }
        e.setDescription(nameEN + "\n\nCome back tomorrow for your new Friend, they'll be waiting for you!");
        return e;
    }
}
