package miat.util;

import miat.filehandler.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;

public class Help {
    public static void help(SlashCommandInteraction interaction) {
        String prefix = ConfigHandler.getString("Prefix");
        String page = interaction.getFullCommandName().toLowerCase();
        Color seppuku = new Color(153,0,238);
        EmbedBuilder e = new EmbedBuilder();

        switch (page) {
            case "miathelp fun":
                e.setTitle("Fun Features");
                e.setDescription("All Slash commands are also Legacy commands unless otherwise listed.");
                e.addField("__**Slash Commands**__", "\n``animalfact``: Get a random Animal Fact from factretriever.com." +
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
                        "\n\n``warn [reason|ping to user] [reason]``: Send a (fake) moderator warning at a user." +
                        "\n\n``miat``: run it and figure it out"
                        ,true);
                e.addField("__**Legacy Commands**__","\n``"+ prefix +"base64 [encode|decode] [text]``: Encodes or Decodes the supplied text." +
                        "\n\n``"+ prefix +"qr``: Create a QR code with any string." +
                        "\n\n``"+ prefix +"bestclient``: Informs you about the best client." +
                        "\n\n``"+ prefix +"search``: Search DuckDuckGo for an image (safe search is on)." +
                        "\n\n``"+ prefix +"collatz``: Perform the collatz conjecture with a specified starting number." +
                        "\n\n``"+ prefix +"fotw``: Find your Friend of the week, special to you! (mention another user to see their Friend of the week)" +
                        "\n\n``"+ prefix +"fotd``: Find your Friend of the day! Functions the same as `fotw`." +
                        "\n\n``"+ prefix +"img:``: Use DuckDuckGo image search to find an image. Aliases `image`, `search`." +
                        "\n\n``"+ prefix +"recipe``: Outputs a recipe I like to use. Works with any kind of ground meat." +
                        "\n\n``"+ prefix +"[charactername] [text]``: For more information about the AI features, use the `/miathelp ai` command!" +
                        "\n\nAI features are in the ``/miathelp ai`` command."
                        ,true);
                e.setFooter("Created By : HAV0X (@hav0x) & arsonfrog (@arsonbot)");
                e.setColor(Color.orange);
                interaction.replyEmbeds(e.build()).queue();
                break;

            case "miathelp utility":
                e.setTitle("Utility Features");
                e.addField("__**Slash Commands**__","\n_Some utility commands are able to be run by whitelisted users._" +
                        "\n\n``ping``: Check if the bot is online or not." +
                        "\n\n``stats``: Check the uptime, memory usage, and other info of the bot. Legacy aliases, `uptime`, `mem`" +
                        "\n\n``purge``: Purge the desired amount of messages." +
                        "\n\n``pfp``: Get a user's PFP. If no argument is sent, it will get your own." +
                        "\n\n``serverinfo``: Get info about the server." +
                        "\n\n``setlogchannel``: Set the deleted message log channel." +
                        "\n\n``ban``: Ban the specified user." +
                        "\n\n``kick``: Kick the specified user." +
                        "\n\n``miathelp``: Get help with the bot." +
                        "\n\n``invite``: Get an invite for the bot with all permissions needed." +
                        "\n\n``addcharacter``: Add a character to the AI character list. **(Whitelisted members only.)**" +
                        "\n\n``toggletranslatorchannel``: Enable/disable flag emoji translation for the specified channel."
                        ,true);
                e.addField("__**Legacy Commands**__",
                        "``"+ prefix +"rm``: Removes the replied to message if it is a bot command that is replying to you. Useful for AI conversations or bad jokes. Aliases ``remove``" +
                                "\n\n``" + prefix + "ml [on|off]``: Enable or Disable the Debug Message Log. Default off. **(Whitelisted members only.)**" +
                                "\n\n``" + prefix + "setactivity``: Set the status of the bot. **(Whitelisted members only.)**" +
                                "\n\n``" + prefix + "refresh``: Refresh the bot's config and AI character list. **(Whitelisted members only.)**"
                                ,true);
                e.setFooter("Created By : HAV0X (@hav0x) & arsonfrog (@arsonbot)");
                e.setColor(seppuku);
                interaction.replyEmbeds(e.build()).queue();
                break;

            case "miathelp ai":
                e.setTitle("AI features");
                e.setDescription("AI features are locally hosted and run. Specific model used may vary." +
                        "\n- To initiate a chat, use `" + prefix + "` followed by a character's name with no spaces, or the character's name followed by a comma and a ping at the bot." +
                        "\n  - Examples: `" + prefix + "redcrownedcrane what is an airfoil?` or `bald eagle, @Miat Fren how far away is the moon`." +
                        "\nReply to the last message sent by a character to continue a chat." +
                        "\n\n- You can modify your experience with `/customizeai` to add a line to the AI's system prompt." +
                        "\n- Use `/addcustomcharacter` to add or edit a character only for you. `/deletecharacter` will remove the character." +
                        "\n~~Image attachments sent with a message can be seen and understood by the AI. Image data may decrease chat quality.~~" +
                        "\n\n**Characters:**\n" +
                        Character.getBaseCharacterList() + "\n**Your custom characters:**\n" + Character.getCustomCharacterList(interaction.getUser().getId()));
                e.setFooter("Created By : HAV0X (@hav0x) & arsonfrog (@arsonbot)");
                e.setColor(Color.CYAN);
                interaction.replyEmbeds(e.build()).queue();
                break;

            case "miathelp general":
                e.setTitle("Miat Fren!");
                e.setDescription("Miat Fren is a general purpose bot with a focus on fun! Miat has plenty of features " +
                        "for use in servers and on your own account! Add miat fren to your own server or to your account " +
                        "by clicking the bot's name, and then **\"+ Add App\"**.\n" +
                        "- Miat can be used in your DMs! Not every feature works, but most commands and interactions work " +
                        "in DMs, so you can privately test out commands or have AI chats.\n" +
                        "- You can configure miat for your server with the `/setlogchannel` and `/toggletranslatorchannel` " +
                        "commands. The deleted message logger saves the content of the message, who sent it, and the " +
                        "attachments on the deleted message! The `/toggletranslatorchannel` command enables/disables " +
                        "the feature of translating a message when a flag emoji is reacted to it. This is useful for " +
                        "announcement channels or other places where you don't want messages to be translated.\n" +
                        "- To use the message translator, react to a message with a flag emoji of the language you want, like " +
                        ":flag_us: for English, :flag_fr: for French, and :flag_jp: for Japanese. The image attached " +
                        "shows an example of the translator in use. The translator also works on embeds, like bot messages " +
                        "and X links. Use the :x: emoji to delete these translation messages.\n\n" +
                        "For additional info about specific features, use `/miathelp fun`, `utility`, and `ai`.");
                e.setImage("https://media.discordapp.net/attachments/1100888255483875428/1135339151139545158/example.png?ex=674300bb&is=6741af3b&hm");
                e.setColor(Color.GRAY);
                e.setFooter("Created By : HAV0X (@hav0x) & arsonfrog (@arsonbot)");
                interaction.replyEmbeds(e.build()).queue();
                break;
            default:
                e.setTitle("Help Command");
                e.addField("Miat","This command will show you the commands this bot has.",false);
                e.setColor(Color.RED);
                interaction.replyEmbeds(e.build()).queue();
                break;
        }
    }
}
