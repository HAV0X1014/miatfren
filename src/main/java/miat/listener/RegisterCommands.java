package miat.listener;

import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;

import static miat.MiatInit.api;

public class RegisterCommands {
    public static void registerCommands() {
        System.out.println("Registering slash commands. This will take some time...");
        /*SlashCommand.with("ping", "Check if the bot is up.").createGlobal(api).join();
        SlashCommand.with("uptime", "Get the uptime of the bot.").createGlobal(api).join();
        SlashCommand.with("purge","Delete the specified number of messages.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Messages", "Amount of messages to delete.", true))).createGlobal(api).join();
        SlashCommand.with("delete","Delete the specified message by ID.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "MessageID", "MessageID of the message you want to delete.", true))).createGlobal(api).join();
        SlashCommand.with("pfp","Get the avatar of a member.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "User","The user's PFP you want.", false))).createGlobal(api).join();
        SlashCommand.with("serverinfo","Get info about the server.").createGlobal(api).join();
        SlashCommand.with("setlogchannel","Set the deleted message log channel.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Channel", "The channel you want to log deleted messages to.", false), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,"Disable","Disables the currently set log channel", false))).createGlobal(api).join();
        SlashCommand.with("ban","Ban the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User", "The user to ban.",true))).createGlobal(api).join();
        SlashCommand.with("kick","Kick the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User","User to kick.", true))).createGlobal(api).join();

        SlashCommand.with("miathelp","Show help for the selected category.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "fun","Shows a list of fun commands.", false), SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"utility","Shows a list of utility commands.", false), SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"ai","Shows info about the AI features.",false))).createGlobal(api).join();

        */
        SlashCommand.with("addcharacter","Add a character to the AI. (Whitelisted)", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "The character's name.",true), SlashCommandOption.create(SlashCommandOptionType.STRING, "description", "The description of the character including world details.", true), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "kfchar", "True/False for KF character (adds world detail)",true), SlashCommandOption.create(SlashCommandOptionType.STRING, "imagepath", "Filepath to the character's image (CharacterImages/name.png)"))).createGlobal(api).join();
        /*
        SlashCommand.with("getcharacter","Get the character description for the requested character.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "The name of the character you want.", true))).createGlobal(api).join();
        SlashCommand.with("invite","Get an invite link for this bot with permissions needed to function.").createGlobal(api).join();
        SlashCommand.with("translate","Translate text with Google Translate into the desired language.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "source", "The text you want to translate.", true), SlashCommandOption.create(SlashCommandOptionType.STRING, "target", "The language you want the text in. (Currently not implemented)", false))).createGlobal(api).join();
        SlashCommand.with("deepl","Translate text with DeepL Translator into the desired language.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "source", "The text you want to translate.", true), SlashCommandOption.create(SlashCommandOptionType.STRING, "target", "The language you want the text in. (Currently not implemented)", false))).createGlobal(api).join();

        SlashCommand.with("wiki","Get a random Wikipedia article.").createGlobal(api).join();
        SlashCommand.with("pointcheck","Check your ReWords points, the top overall, or another user's points.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "User to check. (Use <@ (userID) > to check across servers).",false), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,"top","See the top 5 earners."))).createGlobal(api).join();
        SlashCommand.with("inspiro","Get an \"inspirational\" post").createGlobal(api).join();
        SlashCommand.with("randfr","Get a random Kemono Friends character article from Japari Library.").createGlobal(api).join();
        SlashCommand.with("godsays","Get the latest word from god, courtesy of Terry A. Davis.").createGlobal(api).join();
        SlashCommand.with("miat","Get an image of a Miat(a).").createGlobal(api).join();
        SlashCommand.with("animalfact","Get a random animal fact.").createGlobal(api).join();
        SlashCommand.with("joke","Get a random joke from jokeapi.dev.").createGlobal(api).join();
        SlashCommand.with("createqr","Create a QR code with goqr.me.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING,"Data","Data to encode into the QR code.", true))).createGlobal(api).join();
        SlashCommand.with("8ball","Ask a question to the intelligent 8ball.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING,"question", "The question you want answered.",true))).createGlobal(api).join();
        SlashCommand.with("toggletranslatorchannel","Toggle the translator on/off for the specified channel.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Channel", "Channel to toggle the translator in.", true))).createGlobal(api).join();
        SlashCommand.with("customizeai", "Customize the AI's default prompt only for you.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Addition", "Text to add to the end of the AI's System prompt.", false), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN,"Remove","Remove current AI modification."))).createGlobal(api).join();
        SlashCommand.with("warn", "Send a (fake) moderator warning at a user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "User", "The user you want to warn", false), SlashCommandOption.create(SlashCommandOptionType.STRING, "Reason", "The reason why the user is being warned.", false))).createGlobal(api).join();

        System.out.println("SLASH COMMANDS REGISTERED! Set \"RegisterSlashCommands\" to \"false\" in config.json!");

        System.out.println("Registering Apps. (The things that show up when you right click a message) This may take a while...");
        MessageContextMenu.with("Translate - Google Translate").createGlobal(api).join();
        MessageContextMenu.with("Translate - DeepL").createGlobal(api).join();
        System.out.println("**Apps Registered!** Set \"RegisterApps\" to false in config.json!");
        */
    }
}
