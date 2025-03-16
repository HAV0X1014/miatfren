package miat.filehandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class UserHandler {
    public static void hasConfig(String userID) {
        File config = new File("ServerFiles/UserConfigs/" + userID + ".json");
        if (!config.exists()) {
            JSONObject content = new JSONObject();
            content.put("AIPromptAddition", "");
            content.put("ReWordsScore", 0);
            content.put("CustomCharacters", new JSONObject());
            Writer.write("ServerFiles/UserConfigs/" + userID + ".json", content.toString(1));
        }
    }

    public static String getString(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        return obj.getString(option);
    }

    public static JSONArray getArray(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        if (!obj.has(option)) {
            write(option,new JSONArray(),userID);
        }
        return obj.getJSONArray(option);
    }

    public static JSONObject getObject(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        if (!obj.has(option)) {
            write(option,new JSONObject(),userID);
            obj.put(option,new JSONObject()); //version that stays in memory to avoid an error
        }
        return obj.getJSONObject(option);
    }

    public static boolean getBoolean(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        return obj.getBoolean(option);
    }
    public static int getInt(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        return obj.getInt(option);
    }
    public static void write(String key, Object option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject config = new JSONObject(userConfig);
        config.put(key,option);
        Writer.write("ServerFiles/UserConfigs/" + userID + ".json",config.toString(1));
    }
}
