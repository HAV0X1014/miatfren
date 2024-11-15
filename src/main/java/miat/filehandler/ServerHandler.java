package miat.filehandler;

import miat.util.CheckPermission;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class ServerHandler {
    public static String readConfig(String serverID) {
        String serverFileLocation = "ServerFiles/ServerConfigs/" + serverID + ".json";
        File serverConfig = new File(serverFileLocation);
        if (!serverConfig.exists()) {
            JSONObject configContent = new JSONObject();
            String[] noArrayContent = new String[0];
            configContent.put("DeletedMessageLogChannel", "");
            configContent.put("TranslatorIgnoredChannels", noArrayContent);
            Writer.write(serverFileLocation,configContent.toString(2));
        }
        return Reader.readFull("ServerFiles/ServerConfigs/" + serverID + ".json");
    }

    public static String[] getTranslatorIgnoredChannels(String serverID) {
        JSONObject config = new JSONObject(readConfig(serverID));
        JSONArray ignoredJSON = config.getJSONArray("TranslatorIgnoredChannels");
        String[] ignoredArray = new String[ignoredJSON.length()];
        for (int i = 0; i < ignoredJSON.length(); i++) {
            ignoredArray[i] = ignoredJSON.getString(i);
        }
        return ignoredArray;
    }

    public static String getDeletedMessageLogChannel(String serverID) {
        String serverFileLocation = "ServerFiles/ServerConfigs/" + serverID + ".json";
        File serverConfig = new File(serverFileLocation);

        //If the server does not already have a config made, make a new one with empty values.
        if (!serverConfig.exists()) {
            JSONObject configContent = new JSONObject();
            String[] noArrayContent = new String[0];
            configContent.put("DeletedMessageLogChannel", "");
            configContent.put("TranslatorIgnoredChannels", noArrayContent);
            Writer.write(serverFileLocation,configContent.toString(2));
        }

        String serverConfigString = Reader.readFull(serverFileLocation);
        return new JSONObject(serverConfigString).getString("DeletedMessageLogChannel");
    }

    public static String setLogChannel(SlashCommandInteraction interaction) {
        String configString = readConfig(interaction.getServer().get().getIdAsString());
        String logChannelID = "";
        boolean disable = false;
        if (interaction.getArgumentByName("Channel").isPresent()) logChannelID = interaction.getArgumentByName("Channel").get().getChannelValue().get().getIdAsString();
        if (interaction.getArgumentByName("Disable").isPresent()) disable = interaction.getArgumentByName("Disable").get().getBooleanValue().orElse(false);
        String serverID = interaction.getServer().get().getIdAsString();
        String returnString = "No options to change.";

        if (CheckPermission.checkPermission(interaction, PermissionType.MANAGE_CHANNELS)) {
            String serverFileLocation = "ServerFiles/ServerConfigs/" + serverID + ".json";
            JSONObject config = new JSONObject(configString);
            if (!logChannelID.isEmpty()) {
                config.put("DeletedMessageLogChannel", logChannelID);
                Writer.write(serverFileLocation, config.toString(2));
                returnString = "Log channel set to <#" + logChannelID + ">";
            }
            if (disable) {
                config.put("DeletedMessageLogChannel", "");
                Writer.write(serverFileLocation,config.toString(2));
                returnString = "Log channel removed.";
            }
        } else {
            returnString = "You do not have MANAGE_CHANNELS permissions.";
        }
        return returnString;
    }

    public static String toggleTranslatorIgnoredChannel(SlashCommandInteraction interaction) {
        String configString = readConfig(interaction.getServer().get().getIdAsString());
        String channelID = interaction.getArgumentByName("Channel").get().getChannelValue().get().asTextChannel().get().getIdAsString();
        String serverID = interaction.getServer().get().getIdAsString();

        if (CheckPermission.checkPermission(interaction, PermissionType.MANAGE_CHANNELS)) {
            String serverFileLocation = "ServerFiles/ServerConfigs/" + serverID + ".json";
            JSONObject config = new JSONObject(configString);
            JSONArray array = config.getJSONArray("TranslatorIgnoredChannels");
            //read the existing channelIDs, if the channel we are toggling off exists, remove it from the JSONArray
            for (int i = 0; i < array.length(); i++) {
                if (channelID.equals(array.getString(i))) {
                    array.remove(i);
                    config.put("TranslatorIgnoredChannels", array);
                    Writer.write(serverFileLocation,config.toString(2));
                    return "Translator has been enabled in <#" + channelID + ">.";
                }
            }
            array.put(channelID);
            config.put("TranslatorIgnoredChannels", array);
            Writer.write(serverFileLocation,config.toString(2));
            return "Translator has been disabled in <#" + channelID +">.";
        } else {
            return "You do not have MANAGE_CHANNELS permissions.";
        }
    }
}
