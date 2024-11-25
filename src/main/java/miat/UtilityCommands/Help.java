package miat.UtilityCommands;

import miat.FileHandlers.ConfigHandler;
import miat.FileHandlers.GetCharacter;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;

import static miat.MiatMain.configFile;

public class Help {
    public static EmbedBuilder help(SlashCommandInteraction interaction) {
        String prefix = ConfigHandler.getString("Prefix", configFile);
        String page = interaction.getFullCommandName().toLowerCase();
        Color seppuku = new Color(153,0,238);
        EmbedBuilder e = new EmbedBuilder();

        switch (page) {
            case "miathelp fun":
                e.setTitle("Fun Features");
                e.setDescription("All Slash commands are also Legacy commands unless otherwise listed.");
                e.addInlineField("__**Slash Commands**__", "\n``animalfact``: Get a random Animal Fact from factretriever.com." +
                        "\n\n``createqr``: Create a QR code from any string." +
                        "\n\n``godsays``: Get the latest word from God, courtesy of Terry A. Davis." +
                        "\n\n``inspiro``: Get an \"inspirational\" image." +
                        "\n\n``joke``: Get a random joke from JokeAPI.dev." +
                        "\n\n``pointcheck``: Check your ReWords points, the top 5 earners, or another user's balance. \n-Increase your score by saying specific phrases. (Slash command only)" +
                        "\n\n``randfr``: Get a random Kemono Friends character article." +
                        "\n\n``wiki``: Get a random wikipedia article." +
                        "\n\n``8ball``: Ask the intelligent 8 ball a question." +
                        "\n\n``translate``: Use Google Translate to translate text into english." +
                        "\n\n``deepl``: Use DeepL Translate to translate text into english." +
                        "\n\n``miat``: run it and figure it out");
                e.addInlineField("__**Legacy Commands**__","\n``"+ prefix +"base64 [encode|decode] [text]``: Encodes or Decodes the supplied text." +
                        "\n\n``"+ prefix +"qr``: Create a QR code with any string." +
                        "\n\n``"+ prefix +"bestclient``: Informs you about the best client." +
                        "\n\n``"+ prefix +"search``: Search DuckDuckGo for an image (safe search is on)." +
                        "\n\n``"+ prefix +"collatz``: Perform the collatz conjecture with a specified starting number." +
                        "\n\n``"+ prefix +"magicnumber``: Get your magic number for this week, special to you!" +
                        "\n\n``"+ prefix +"fotw``: Find your Friend of the week, special to you! (mention another user to see their Friend of the week)" +
                        "\n\n``"+ prefix +"fotd``: Find your Friend of the day! Functions the same as ``fotw``." +
                        "\n\n``"+ prefix +"img:``: Use DuckDuckGo image search to find an image. Aliases ``image``, ``search``." +
                        "\n\n``"+ prefix +"recipe``: Outputs a recipe I like to use. Works with any kind of ground meat." +
                        "\n\n``"+ prefix +"[charactername]`` [text]``: For more information about the AI features, use the ``/miathelp ai`` command!" +
                        "\n\nAI features are in the ``/miathelp ai`` command.");
                e.setFooter("Created By : HAV0X (@hav0x) & arsonfrog (@arsonbot)");
                e.setColor(Color.orange);
                break;

            case "miathelp utility":
                e.setTitle("Utility Features");
                e.addInlineField("__**Slash Commands**__","\n_Some utility commands are able to be run by whitelisted users._" +
                "\n\n``ping``: Check if the bot is online or not." +
                "\n\n``uptime``: Check the uptime of the bot." +
                "\n\n``purge``: Purge the desired amount of messages." +
                "\n\n``delete``: Delete the specified message - both message ID types work." +
                "\n\n``pfp``: Get a user's PFP. If no argument is sent, it will get your own." +
                "\n\n``serverinfo``: Get info about the server." +
                "\n\n``setlogchannel``: Set the deleted message log channel." +
                "\n\n``ban``: Ban the specified user." +
                "\n\n``kick``: Kick the specified user." +
                "\n\n``miathelp``: Get help with the bot." +
                "\n\n``invite``: Get an invite for the bot with all permissions needed." +
                "\n\n``addcharacter``: Add a character to the AI character list. **(Whitelisted members only.)**");
                e.addInlineField("__**Legacy Commands**__",
                        "``"+ prefix +"rm``: Removes the replied to message if it is a bot command that is replying to you. Useful for AI conversaitions or bad jokes. Aliases ``remove``" +
                        "\n\n``" + prefix+ "ml [on|off]``: Enable or Disable the Debug Message Log. Default off. **(Whitelisted members only.)**" +
                        "\n\n``" + prefix + "setactivity``: Set the status of the bot. **(Whitelisted members only.)**" +
                        "\n\n``" + prefix + "remove``: Removes the bot's message only if it is replying to you. Reply to the message you want removed." +
                        "\n\n``" + prefix + "refresh``: Refresh the bot's config and AI character list. **(Whitelisted members only.)**");
                e.setFooter("Created By : HAV0X (@hav0x) & arsonfrog (@arsonbot)");
                e.setColor(seppuku);
                break;

            case "miathelp ai":
                e.setTitle("AI features");
                e.setDescription("AI features are locally hosted and run. Specific model used may vary." +
                                "\n\n- To start a chat, use ``" + prefix + "`` followed by the name of the character without any spaces, for example ``" + prefix + "tact what is an airfoil?``." +
                                "\n- To start a chat with multiple characters, separate the names with a comma. For example ``" + prefix + "tact,cleck,topi where is the station?``." +
                                "\nReply to the last message sent by a character to continue a chat." +
                                " Image attachments sent with a message can be seen and understood by the AI. Image data may decrease chat quality." +
                                "\n\n**Characters:**\n" +
                                GetCharacter.getList());
                e.setColor(Color.CYAN);
                break;

            default:
                e.setTitle("Help Command");
                e.addField("Miat","This command will show you the commands this bot has.");
                e.setColor(Color.RED);
                break;
        }
        return e;
    }
}
