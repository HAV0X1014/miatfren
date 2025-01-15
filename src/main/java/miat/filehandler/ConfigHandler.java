package miat.filehandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static miat.MiatMain.*;

public class ConfigHandler {
    public static String read(String configLocation) {
        return Reader.readFull(configLocation);
    }
    public static String getString(String option) {
        JSONObject obj = new JSONObject(configFile);
        return obj.getJSONObject("Config").getString(option);
    }
    public static String[] getArray(String option) {
        JSONObject obj = new JSONObject(configFile);
        JSONArray jsonArray = obj.getJSONObject("Config").getJSONArray(option);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list.toArray(new String[0]);
    }
    public static boolean getBoolean(String option) {
        JSONObject obj = new JSONObject(configFile);
        return obj.getJSONObject("Config").getBoolean(option);
    }
    public static void refresh() {
        //in case we want to reload/refresh the current config, re-read and re-assign everything.
        characterList = Reader.readFull("ServerFiles/characters.json");
        configFile = ConfigHandler.read("ServerFiles/config.json");

        deeplEnabled = ConfigHandler.getBoolean("DeepLEnabled");
        prefix = ConfigHandler.getString("Prefix");
        reWordsGoodWordsExactMatch = ConfigHandler.getArray("ReWordsGoodWordsExactMatch");
        reWordsGoodWords = ConfigHandler.getArray("ReWordsGoodWords");
        reWordsBadWordsExactMatch = ConfigHandler.getArray("ReWordsBadWordsExactMatch");
        reWordsBadWords = ConfigHandler.getArray("ReWordsBadWordsExactMatch");
        deepLEmoji = ConfigHandler.getString("DeepLEmoji");
        useGoogleAsFallbackForDeepL = ConfigHandler.getBoolean("UseGoogleTranslateAsFallbackForDeepL");
    }
}