package miat.util;

import miat.filehandler.UserHandler;

public class CustomizeAI {
    public static void setCustomPromptSuffix(String suffix, String userID) {
        if (suffix.length() > 1500) {
            suffix = suffix.substring(1,1500);
        }
        UserHandler.write("AIPromptAddition", suffix, userID);
    }
}
