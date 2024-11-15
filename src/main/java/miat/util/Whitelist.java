package miat.util;

import miat.filehandler.ConfigHandler;

import java.util.Arrays;

public class Whitelist {
    public static boolean whitelisted(String userID) {
        String[] whitelistedMembersArray = ConfigHandler.getArray("Whitelist");
        boolean whitelisted = false;
        String whitelistedMembers = Arrays.toString(whitelistedMembersArray);
        if (whitelistedMembers.contains(userID)) {
            whitelisted = true;
        }
        return whitelisted;
    }
}