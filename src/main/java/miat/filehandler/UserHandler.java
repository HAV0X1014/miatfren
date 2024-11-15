package miat.filehandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserHandler {
    public static void hasConfig(String userID) {
        File config = new File("ServerFiles/UserConfigs/" + userID + ".json");
        if (!config.exists()) {
            JSONObject content = new JSONObject();
            content.put("AIPromptAddition", "");
            content.put("ReWordsScore", 0);
            Writer.write("ServerFiles/UserConfigs/" + userID + ".json", content.toString(1));
        }
    }

    public static String getString(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        return obj.getString(option);
    }

    public static String[] getArray(String option, String userID) {
        hasConfig(userID);
        String userConfig = Reader.readFull("ServerFiles/UserConfigs/" + userID + ".json");
        JSONObject obj = new JSONObject(userConfig);
        JSONArray jsonArray = obj.getJSONArray(option);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list.toArray(new String[list.size()]);
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
