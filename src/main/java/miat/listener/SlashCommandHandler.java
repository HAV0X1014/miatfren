package miat.listener;

import miat.features.*;
import miat.filehandler.*;
import miat.util.*;
import miat.util.Character;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static miat.MiatMain.*;

public class SlashCommandHandler extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandInteraction interaction = event.getInteraction();
        String command = interaction.getName();
        switch(command) {
            case "ping":
                interaction.reply("theres been 5 iterations of this bot now bruhhhhh").setEphemeral(true).queue();
                break;
            case "stats":
                interaction.replyEmbeds(BotStats.stats().build()).queue();
                break;
            case "purge":
                Purge.purge(interaction);
                break;
            case "delete":
                DeleteMessage.deleteMessage(interaction);
                break;
            case "pfp":
                Pfp.pfp(interaction);
                break;
            case "serverinfo":
                ServerInfo.serverInfo(interaction);
                break;
            case "setlogchannel":
                interaction.reply(ServerHandler.setLogChannel(interaction)).setEphemeral(true).queue();
                break;
            case "ban":
                interaction.reply(Ban.ban(interaction)).setEphemeral(true).queue();
                break;
            case "kick":
                interaction.reply(Kick.kick(interaction)).setEphemeral(true).queue();
                break;
            case "miathelp":
                Help.help(interaction);
                break;
            case "invite":
                interaction.reply("You can install this bot to your account to use it anywhere!\nInstall it to your server or account with this link.\nhttps://discord.com/oauth2/authorize?client_id=" + self + "&permissions=8").setEphemeral(true).queue();
                break;
            //TODO: reimplement slash commands for message translation - DeepL and google

            case "addcharacter": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.setEphemeral(true);
                if (Whitelist.whitelisted(interaction.getUser().getId())) {
                    hook.sendMessage(Character.add(interaction)).queue();
                    System.out.println("Refreshing Config and AI characters...");
                    characterList = Reader.readFull("ServerFiles/characters.json");
                    System.out.println("Refreshed.");
                } else {
                    hook.sendMessage("You are not on the whitelist.").queue();
                }
            }
                break;
            case "addcustomcharacter": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.setEphemeral(true);
                hook.sendMessage(Character.addCustom(interaction)).setEphemeral(true).queue();
            }
                break;
            case "deletecharacter": {
                interaction.reply(Character.deleteCustom(interaction)).setEphemeral(true).queue();
            }
                break;
            case "getcharacter":
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle(Character.getName(interaction.getOption("name").getAsString(),interaction.getUser().getId()));
                em.setDescription("Character Description:\n" +
                        Character.getDescription(interaction.getOption("name").getAsString(),interaction.getUser().getId()) +
                        "\nCharacter Appearance:\n" +
                        Character.getAppearance(interaction.getOption("name").getAsString(),interaction.getUser().getId()));
                interaction.replyEmbeds(em.build()).setEphemeral(true).queue();
                break;
            //fun commands below
            case "pointcheck":
                if (interaction.getOption("user") != null) {
                    ReWords.user(interaction);
                    break;
                }
                if (interaction.getOption("top") != null) {
                    ReWords.top(interaction);
                    break;
                }
                if (interaction.getOptions().isEmpty()) {
                    ReWords.pointCheck(interaction);
                    break;
                }
                break;
            case "wiki": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessage(Wikipedia.randomArticle()).queue();
            }
                break;
            case "miat":
                interaction.reply("https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 13) + ".png?raw=true").queue();
                break;
            case "inspiro": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessage(Inspiro.inspiro()).queue();
            }
                break;
            case "randfr": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessage(RandFr.randomFriend()).queue();
            }
                break;
            case "godsays": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessage(Godsays.godSays()).queue();
            }
                break;
            case "animalfact":
                interaction.replyEmbeds(AnimalFact.animalFact().build()).queue();
                break;
            case "joke": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessageEmbeds(Joke.randomJoke().build()).queue();
            }
                break;
            case "createqr":
                QRCode.qrCodeCreate(interaction,interaction.getOption("data").getAsString());
                break;
            case "8ball":
                interaction.replyEmbeds(EightBall.eightBall(interaction.getOption("question").getAsString()).build()).queue();
                break;
            case "toggletranslatorchannel":
                interaction.reply(ServerHandler.toggleTranslatorIgnoredChannel(interaction)).queue();
                break;
            case "customizeai":
                if ((interaction.getOption("addition") != null) && !interaction.getOption("addition").toString().isEmpty()) {
                    CustomizeAI.setCustomPromptSuffix(interaction.getOption("addition").getAsString(), interaction.getUser().getId());
                    interaction.reply("AI system prompt addition has been updated for you.").queue();
                } else
                if (interaction.getOption("remove") != null) {
                    if (interaction.getOption("remove").getAsBoolean()) {
                        CustomizeAI.setCustomPromptSuffix("", interaction.getUser().getId());
                        interaction.reply("Your custom AI prompt addition has been removed.").queue();
                    } else {
                        interaction.reply("Insufficient arguments to modify custom system prompt addition.").setEphemeral(true).queue();
                    }
                } else {
                    interaction.reply("Insufficient arguments to modify custom system prompt addition.").setEphemeral(true).queue();
                }
                break;
            case "warn":
                //if user + reason, if reason only, if user only, if none.
                if ((interaction.getOption("user") != null) && (interaction.getOption("reason") != null)) {
                    interaction.replyEmbeds(FakeWarn.warnWithUserAndReason(interaction.getUser().getId(), "<@"+ interaction.getOption("user").getAsUser().getId() + ">", interaction.getOption("reason").getAsString()).build()).queue();
                } else if (interaction.getOption("user") != null) {
                    interaction.replyEmbeds(FakeWarn.warnWithUser(interaction.getUser().getId(), "<@"+ interaction.getOption("user").getAsUser().getId() + ">").build()).queue();
                } else if (interaction.getOption("reason") != null) {
                    interaction.replyEmbeds(FakeWarn.warnWithReason(interaction.getUser().getId(), interaction.getOption("reason").getAsString()).build()).queue();
                } else {
                    interaction.replyEmbeds(FakeWarn.warnWithNothing(interaction.getUser().getId()).build()).queue();
                }
                break;
            case "timeout":
                interaction.reply(Timeout.timeout(interaction)).setEphemeral(true).queue();
                break;
            case "fortune":
                interaction.replyEmbeds(Fortune.cowsay().build()).queue();
                break;
            case "gemini": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessageEmbeds(Gemini.send(interaction).build()).queue();
                break;
            }
            case "googletranslate": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessageEmbeds(Trnsl.googleSend(interaction).build()).queue();
                break;
            }
            case "deepltranslate": {
                InteractionHook hook = interaction.getHook();
                interaction.deferReply().queue();
                hook.sendMessageEmbeds(Trnsl.deeplSend(interaction).build()).queue();
                break;
            }
        }
    }
}
