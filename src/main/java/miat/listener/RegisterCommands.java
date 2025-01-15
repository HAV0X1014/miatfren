package miat.listener;

import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;

import static miat.MiatMain.jda;

public class RegisterCommands {
    public static void register() {
        System.out.println("trying to register user-added apps and slash commands...");
        ArrayList<CommandData> cd = new ArrayList<>();

        //context commands (user added, guild added, plus wherever the bot is)
        cd.add(Commands.context(Command.Type.MESSAGE,"Translate - Google Translate")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.context(Command.Type.MESSAGE,"Translate - DeepL")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.context(Command.Type.MESSAGE, "8 Ball")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        //slash commands (wherever the bot is only)
        cd.add(Commands.slash("ping","Check if the bot is up."));

        cd.add(Commands.slash("stats","Get the current memory usage, uptime, and other info of the bot.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("purge","Delete the specified number of messages.")
                .addOption(OptionType.INTEGER,"messages","The amount of messages to purge.",true));

        //cd.add(Commands.slash("delete","this command is bugged currently"));

        cd.add(Commands.slash("pfp","Get the profile picture of the specified user.")
                .addOption(OptionType.USER,"user","The user to get.",false)
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        //this command does not work globally
        cd.add(Commands.slash("serverinfo","Get detailed information about the server.")
                .setIntegrationTypes(IntegrationType.GUILD_INSTALL)
                .setContexts(InteractionContextType.GUILD));

        cd.add(Commands.slash("setlogchannel","Set the deleted message log channel.")
                .addOption(OptionType.CHANNEL,"channel","The channel to log deleted messages in.",false)
                .addOption(OptionType.BOOLEAN,"disable","Disable the currently set log channel.",false));

        cd.add(Commands.slash("ban","Ban the selected user.")
                .addOption(OptionType.USER,"user","The user to ban.",true));

        cd.add(Commands.slash("kick","Kick the selected user.")
                .addOption(OptionType.USER,"user","The user to kick.",true));

        cd.add(Commands.slash("timeout","Times out the selected user for the specified duration in days.")
                .addOption(OptionType.USER,"user","The user to timeout.", true)
                .addOption(OptionType.INTEGER,"days","The amount of days.",true));

        //subcommands have to be done like this
        ArrayList<SubcommandData> subcommands = new ArrayList<>();
        subcommands.add(new SubcommandData("fun","Shows the list of fun commands"));
        subcommands.add(new SubcommandData("utility","Shows the list of utility commands."));
        subcommands.add(new SubcommandData("ai","Shows info about the AI features."));
        subcommands.add(new SubcommandData("general","Shows general info about the bot."));
        cd.add(Commands.slash("miathelp","Show help for the selected category.")
                .addSubcommands(subcommands));

        cd.add(Commands.slash("addcharacter","Add/edit a global character. (Whitelisted)")
                .addOption(OptionType.STRING,"name","The name of the character.",true)
                .addOption(OptionType.STRING,"description","The description of the character.",true)
                .addOption(OptionType.STRING,"appearance","The appearance of the character.",true)
                .addOption(OptionType.BOOLEAN,"kfchar","True/False for KF character (adds world detail)",true)
                .addOption(OptionType.STRING,"imagepath","Filepath to the character's image (usually CharacterImages/name.png)"));

        cd.add(Commands.slash("addcustomcharacter","Add/edit a custom character for the AI.")
                .addOption(OptionType.STRING,"name","The name of the character.",true)
                .addOption(OptionType.STRING,"description","The description of the character.",true)
                .addOption(OptionType.STRING,"appearance","The appearance of the character.",true)
                .addOption(OptionType.BOOLEAN,"kfchar","True/False for KF character (adds world detail)",true)
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("deletecharacter","Deletes your specified custom character.")
                .addOption(OptionType.STRING,"name","The name of the character to delete.",true)
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("getcharacter","Get the description of the requested character.")
                .addOption(OptionType.STRING,"name","The name of the character you want.",true)
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("invite","Get an invite link for this bot with permissions needed for it to function.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("wiki","Get a random Wikipedia article.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("pointcheck","Check your ReWords points, the top overall, or another user's points")
                .addOption(OptionType.USER,"user","User to check.",false)
                .addOption(OptionType.BOOLEAN,"top","See the top 5 earners.",false));

        cd.add(Commands.slash("inspiro","Get an \"inspirational\" image.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("randfr","Get a random Kemono Friends character article from Japari Library.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("godsays","Get the latest word from god, courtesy of Terry A. Davis.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("miat","Get an image of a Mazda or something.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("animalfact","Get a random animal fact.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("joke","Get a random joke from jokeapi.dev"));

        cd.add(Commands.slash("createqr","Create a QR code with goqr.me")
                .addOption(OptionType.STRING,"data","Data to encode into the QR code.",true)
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("8ball","Ask a question to the intelligent 8 ball.")
                .addOption(OptionType.STRING,"question","The question you would like an opinion on.",true));

        cd.add(Commands.slash("toggletranslatorchannel","Toggle the translator on/off for the specified channel.")
                .addOption(OptionType.CHANNEL,"channel","Channel to toggle the translator in.", true));

        cd.add(Commands.slash("customizeai","Customize the AI's system prompt only for you.")
                .addOption(OptionType.STRING,"addition","Instructions to add to the end of the AI's system prompt.",false)
                .addOption(OptionType.BOOLEAN,"remove","Remove your currently set customization?",false));

        cd.add(Commands.slash("warn","Send a (fake) moderator warning at a user.")
                .addOption(OptionType.USER,"user","The user to warn",false)
                .addOption(OptionType.STRING,"reason","The reason for the warning.",false)
                .setIntegrationTypes(IntegrationType.GUILD_INSTALL)
                .setContexts(InteractionContextType.GUILD));

        cd.add(Commands.slash("fortune","Get a fortune, quote, or another text blurb from a cow.")
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        cd.add(Commands.slash("gemini","Ask Google Gemini a question. (queries are filtered and recorded by google.)")
                .addOption(OptionType.STRING,"prompt","Prompt to ask gemini. (Do not ask NSFW or objectionable content.)",true)
                .setIntegrationTypes(IntegrationType.ALL)
                .setContexts(InteractionContextType.ALL));

        jda.updateCommands().addCommands(cd).queue();
        System.out.println("**Apps Registered!** Set \"RegisterApps\" to false in config.json");
    }
}
