package miat.listener;

import miat.features.*;
import miat.filehandler.Reader;
import miat.filehandler.ServerHandler;
import miat.util.*;
import me.bush.translator.Language;
import miat.util.Character;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import static miat.MiatInit.*;

public class SlashCommandHandler implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String command = interaction.getCommandName();
        switch(command) {
            case "ping":
                interaction.createImmediateResponder().setContent("Pong. Plain and simple.").setFlags(MessageFlag.EPHEMERAL).respond();
                break;
            case "uptime":
                interaction.createImmediateResponder().setContent(Uptime.uptime()).respond();
                break;
            case "purge":
                Purge.purge(interaction);
                break;
            case "delete":
                DeleteMessage.deleteMessage(interaction, api);
                break;
            case "pfp":
                Pfp.pfp(interaction);
                break;
            case "serverinfo":
                interaction.createImmediateResponder().setContent("").addEmbed(ServerInfo.serverInfo(interaction, api)).respond();
                break;
            case "setlogchannel":
                interaction.createImmediateResponder().setContent(ServerHandler.setLogChannel(interaction)).respond();
                break;
            case "ban":
                interaction.createImmediateResponder().setContent(Ban.ban(interaction)).setFlags(MessageFlag.EPHEMERAL).respond();
                break;
            case "kick":
                interaction.createImmediateResponder().setContent(Kick.kick(interaction)).setFlags(MessageFlag.EPHEMERAL).respond();
                break;
            case "miathelp":
                interaction.createImmediateResponder().setContent("").addEmbed(Help.help(interaction)).respond();
                break;
            case "invite":
                interaction.createImmediateResponder().setContent(api.createBotInvite(new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build())).setFlags(MessageFlag.EPHEMERAL).respond();
                break;
            case "translate":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("").addEmbed(Trnsl.trnsl(interaction.getArgumentStringValueByName("source").get(), Language.ENGLISH)).update();
                });
                break;
            case "deepl":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    if (deeplEnabled) {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(DeepL.deepl(interaction.getArgumentStringValueByName("source").get(), "en-US")).update();
                    } else {
                        interactionOriginalResponseUpdater.setContent("DeepL translation is disabled.").setFlags(MessageFlag.EPHEMERAL).update();
                    }
                });
                break;
            case "addcharacter":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    if (Whitelist.whitelisted(interaction.getUser().getIdAsString())) {
                        interactionOriginalResponseUpdater.setContent(Character.add(interaction)).update();
                        System.out.println("Refreshing Config and AI characters...");
                        characterList = Reader.readFull("ServerFiles/characters.json");
                        System.out.println("Refreshed.");
                    } else {
                        interactionOriginalResponseUpdater.setContent("You are not on the whitelist.").setFlags(MessageFlag.EPHEMERAL).update();
                    }
                });
                break;
            case "getcharacter":
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle(Character.getName(interaction.getArgumentStringValueByName("name").get()));
                em.setDescription(Character.getDescription(interaction.getArgumentStringValueByName("name").get()));
                interaction.createImmediateResponder().setContent("").addEmbed(em).setFlags(MessageFlag.EPHEMERAL).respond();
                break;

            //fun commands below
            case "pointcheck":
                if (interaction.getArgumentUserValueByIndex(0).isPresent()) {
                    ReWords.user(interaction);
                }
                if (interaction.getArgumentBooleanValueByIndex(0).isPresent()) {
                    ReWords.top(interaction);
                }
                if (!interaction.getOptionByIndex(0).isPresent()) {
                    ReWords.pointCheck(interaction);
                }
                break;
            case "wiki":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(Wikipedia.randomArticle()).update();
                });
                break;
            case "miat":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 13) + ".png?raw=true").update();
                });
                break;
            case "inspiro":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(Inspiro.inspiro()).update();
                });
                break;
            case "randfr":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(RandFr.randomFriend()).update();
                });
                break;
            case "godsays":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(Godsays.godSays()).update();
                });
                break;
            case "animalfact":
                interaction.createImmediateResponder().setContent("").addEmbed(AnimalFact.animalFact()).respond();
                break;
            case "joke":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("").addEmbed(RandomJoke.randomJoke()).update();
                });
                break;
            case "createqr":
                String data = interaction.getArgumentStringValueByIndex(0).get();
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("").addEmbed(QRCode.qrCodeCreate(data)).update();
                });
                break;
            case "8ball":
                interaction.createImmediateResponder().setContent("").addEmbed(EightBall.eightBall(interaction.getArgumentStringValueByName("question").get())).respond();
                break;
            case "toggletranslatorchannel":
                interaction.createImmediateResponder().setContent(ServerHandler.toggleTranslatorIgnoredChannel(interaction)).respond();
                break;
            case "customizeai":
                if (interaction.getArgumentStringValueByName("Addition").isPresent() && !interaction.getArgumentStringValueByName("Addition").toString().isEmpty()) {
                    CustomizeAI.setCustomPromptSuffix(interaction.getArgumentStringValueByName("Addition").get().toString(), interaction.getUser().getIdAsString());
                    interaction.createImmediateResponder().setContent("AI system prompt addition has been updated for you.").respond();
                } else
                if (interaction.getArgumentBooleanValueByName("Remove").isPresent()) {
                    if (interaction.getArgumentBooleanValueByName("Remove").get().equals(true)) {
                        CustomizeAI.setCustomPromptSuffix("", interaction.getUser().getIdAsString());
                        interaction.createImmediateResponder().setContent("Your custom AI prompt addition has been removed.").respond();
                    } else {
                        interaction.createImmediateResponder().setContent("Insufficient arguments to modify custom system prompt addition.").setFlags(MessageFlag.EPHEMERAL).respond();
                    }
                } else {
                    interaction.createImmediateResponder().setContent("Insufficient arguments to modify custom system prompt addition.").setFlags(MessageFlag.EPHEMERAL).respond();
                }
                break;
            case "warn":
                //if user + reason, if reason only, if user only, if none.
                if (interaction.getArgumentUserValueByName("User").isPresent() && interaction.getArgumentStringValueByName("Reason").isPresent()) {
                    interaction.createImmediateResponder().setContent("").addEmbed(FakeWarn.warnWithUserAndReason(interaction.getUser().getIdAsString(), "<@"+ interaction.getArgumentUserValueByName("User").get().getIdAsString() + ">", interaction.getArgumentStringValueByName("Reason").get().toString())).respond();
                } else if (interaction.getArgumentUserValueByName("User").isPresent()) {
                    interaction.createImmediateResponder().setContent("").addEmbed(FakeWarn.warnWithUser(interaction.getUser().getIdAsString(), "<@"+ interaction.getArgumentUserValueByName("User").get().getIdAsString() + ">")).respond();
                } else if (interaction.getArgumentStringValueByName("Reason").isPresent()) {
                    interaction.createImmediateResponder().setContent("").addEmbed(FakeWarn.warnWithReason(interaction.getUser().getIdAsString(), interaction.getArgumentStringValueByName("Reason").get())).respond();
                } else {
                    interaction.createImmediateResponder().setContent("").addEmbed(FakeWarn.warnWithNothing(interaction.getUser().getIdAsString())).respond();
                }
                break;
        }
    }
}
