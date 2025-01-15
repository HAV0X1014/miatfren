package miat.filehandler;

import miat.util.CheckPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

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
        String configString = readConfig(Objects.requireNonNull(interaction.getGuild()).getId());
        String logChannelID = "";
        boolean disable = false;
        if (interaction.getOption("channel") != null) logChannelID = interaction.getOption("channel").getAsChannel().getId();
        if (interaction.getOption("disable") != null) disable = interaction.getOption("disable").getAsBoolean();
        String serverID = interaction.getGuild().getId();
        String returnString = "No options to change.";

        if (CheckPermission.checkPermission(interaction, Permission.MANAGE_CHANNEL)) {
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
        String configString = readConfig(interaction.getGuild().getId());
        String channelID = interaction.getOption("channel").getAsChannel().getId();
        String serverID = interaction.getGuild().getId();

        if (CheckPermission.checkPermission(interaction, Permission.MANAGE_CHANNEL)) {
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
