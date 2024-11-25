package miat;

import me.bush.translator.Language;
import me.bush.translator.Translator;
import miat.FileHandlers.*;
import miat.FunFeatures.*;
import miat.UtilityCommands.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Random;

public class MiatMain {
    static boolean debugmessagelog;
    public static String configFile = ReadFile.getFull("ServerFiles/config.json");
    public static String characterList = ReadFile.getFull("ServerFiles/characters.json");
    static String token = ConfigHandler.getString("Token", configFile);
    public static String prefix = ConfigHandler.getString("Prefix", configFile);
    static String botName = ConfigHandler.getString("BotName", configFile);
    static Boolean deeplEnabled = ConfigHandler.getBoolean("DeepLEnabled", configFile);
    static String deepLEmoji = ConfigHandler.getString("DeepLEmoji", configFile);
    static Boolean useGoogleAsFallbackForDeepL = ConfigHandler.getBoolean("UseGoogleTranslateAsFallbackForDeepL", configFile);
    static String deepLKey = ConfigHandler.getString("DeepLKey", configFile);
    static String[] geminiChannels = ConfigHandler.getArray("GeminiChannels", configFile);
    static String[] ignoredChannels = ConfigHandler.getArray("TranslatorFlagIgnoredChannels", configFile);
    static String[] reWordsGoodWords = ConfigHandler.getArray("ReWordsGoodWords", configFile);
    static String[] reWordsGoodWordsExactMatch = ConfigHandler.getArray("ReWordsGoodWordsExactMatch", configFile);
    static String[] reWordsBadWords = ConfigHandler.getArray("ReWordsBadWords", configFile);
    static String[] reWordsBadWordsExactMatch = ConfigHandler.getArray("ReWordsBadWordsExactMatch", configFile);
    static Boolean registerSlashCommands = ConfigHandler.getBoolean("RegisterSlashCommands", configFile);
    static String statusText = ConfigHandler.getString("StatusText", configFile);
    static boolean registerApps = ConfigHandler.getBoolean("RegisterApps", configFile);
    public static AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();
    public static User self;
    static int reminder;

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        System.out.println(botName + " logged in.");
        int startTime = (int) (System.currentTimeMillis() / 1000);
        self = api.getYourself();
        String time = new Date().toString();
        Permissions admin = new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build();
        Translator translator = new Translator(); //google translate object
        if (deepLKey == null) deepLKey = "0";
        com.deepl.api.Translator deepLTranslator = new com.deepl.api.Translator(deepLKey); //deepL translator object

        api.updateActivity(ActivityType.COMPETING, statusText);

        if (registerSlashCommands) {
            System.out.println("Registering slash commands. This will take some time...");
            SlashCommand.with("ping", "Check if the bot is up.").createGlobal(api).join();
            SlashCommand.with("uptime", "Get the uptime of the bot.").createGlobal(api).join();
            SlashCommand.with("purge","Delete the specified number of messages.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "Messages", "Amount of messages to delete.", true))).createGlobal(api).join();
            SlashCommand.with("delete","Delete the specified message by ID.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "MessageID", "MessageID of the message you want to delete.", true))).createGlobal(api).join();
            SlashCommand.with("pfp","Get the avatar of a member.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER, "User","The user's PFP you want.", false))).createGlobal(api).join();
            SlashCommand.with("serverinfo","Get info about the server.").createGlobal(api).join();
            SlashCommand.with("setlogchannel","Set the deleted message log channel.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Channel", "The channel you want to log deleted messages to.", true))).createGlobal(api).join();
            SlashCommand.with("ban","Ban the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User", "The user to ban.",true))).createGlobal(api).join();
            SlashCommand.with("kick","Kick the specified user.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.USER,"User","User to kick.", true))).createGlobal(api).join();

            SlashCommand.with("miathelp","Show help for the selected category.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "fun","Shows a list of fun commands.", false), SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"utility","Shows a list of utility commands.", false), SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"ai","Shows info about the AI features.",false))).createGlobal(api).join();
            SlashCommand.with("addcharacter","Add a character to the AI. (Whitelisted)", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "The character's name.",true), SlashCommandOption.create(SlashCommandOptionType.STRING, "description", "The description of the character including world details.", true), SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "kfchar", "True/False for KF character (adds world detail)",true), SlashCommandOption.create(SlashCommandOptionType.STRING, "imagepath", "Filepath to the character's image (CharacterImages/name.png)"))).createGlobal(api).join();
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

            System.out.println("SLASH COMMANDS REGISTERED! Set \"RegisterSlashCommands\" to \"false\" in config.json!");
        }

        if (registerApps) {
            System.out.println("Registering Apps. (The things that show up when you right click a message) This may take a while...");
            MessageContextMenu.with("Translate - Google Translate").createGlobal(api).join();
            MessageContextMenu.with("Translate - DeepL").createGlobal(api).join();
            System.out.println("**Apps Registered!** Set \"RegisterApps\" to false in config.json!");
        }
        /*
        String slashCommandID = "1136471008405098588";
            try {
                api.getGlobalSlashCommandById(Long.parseLong(slashCommandID)).get().delete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        */

        //Application commands (apps)
        api.addMessageContextMenuCommandListener(event -> {
            MessageContextMenuInteraction interaction = event.getMessageContextMenuInteraction();
            String command = interaction.getCommandName();

            switch(command) {
                case "Translate - DeepL":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        if (deeplEnabled) {
                            interactionOriginalResponseUpdater.setContent("").addEmbed(DeepL.deepl(deepLTranslator, interaction.getTarget().getContent(), "en-US")).setFlags(MessageFlag.EPHEMERAL).update();
                        } else {
                            interactionOriginalResponseUpdater.setContent("DeepL translation is disabled.").setFlags(MessageFlag.EPHEMERAL).update();
                        }
                    });
                    break;
                case "Translate - Google Translate":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(Trnsl.trnsl(translator,interaction.getTarget().getContent(), Language.ENGLISH)).setFlags(MessageFlag.EPHEMERAL).update();
                    });
                    break;
            }
        });

        //slash commands
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            String command = interaction.getCommandName();
            switch(command) {
                case "ping":
                    interaction.createImmediateResponder().setContent("Pong. Plain and simple.").setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "uptime":
                    interaction.createImmediateResponder().setContent(Uptime.uptime(startTime)).respond();
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
                    interaction.createImmediateResponder().setContent(SetLogChannel.setlogchannel(interaction)).respond();
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
                    interaction.createImmediateResponder().setContent(api.createBotInvite(admin)).setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "translate":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(Trnsl.trnsl(translator, interaction.getArgumentStringValueByName("source").get(), Language.ENGLISH)).update();
                    });
                    break;
                case "deepl":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        if (deeplEnabled) {
                            interactionOriginalResponseUpdater.setContent("").addEmbed(DeepL.deepl(deepLTranslator, interaction.getArgumentStringValueByName("source").get(), "en-US")).update();
                        } else {
                            interactionOriginalResponseUpdater.setContent("DeepL translation is disabled.").setFlags(MessageFlag.EPHEMERAL).update();
                        }
                    });
                    break;
                case "addcharacter":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        if (Whitelist.whitelisted(interaction.getUser().getIdAsString())) {
                            interactionOriginalResponseUpdater.setContent(AddCharacter.add(interaction)).update();
                            System.out.println("Refreshing Config and AI characters...");
                            characterList = ReadFile.getFull("ServerFiles/characters.json");
                            System.out.println("Refreshed.");
                        } else {
                            interactionOriginalResponseUpdater.setContent("You are not on the whitelist.").setFlags(MessageFlag.EPHEMERAL).update();
                        }
                    });
                    break;
                case "getcharacter":
                    EmbedBuilder em = new EmbedBuilder();
                    em.setTitle(GetCharacter.getName(interaction.getArgumentStringValueByName("name").get()));
                    em.setDescription(GetCharacter.getDescription(interaction.getArgumentStringValueByName("name").get()));
                    interaction.createImmediateResponder().setContent("").addEmbed(em).setFlags(MessageFlag.EPHEMERAL).respond();
                    break;

                //fun commands below
                case "pointcheck":
                    if (interaction.getArgumentUserValueByIndex(0).isPresent()) {
                        ReWordsPointChecker.user(interaction);
                    }
                    if (interaction.getArgumentBooleanValueByIndex(0).isPresent()) {
                        ReWordsPointChecker.top(interaction);
                    }
                    if (!interaction.getOptionByIndex(0).isPresent()) {
                        ReWordsPointChecker.pointCheck(interaction);
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
                        interactionOriginalResponseUpdater.setContent("").addEmbed(QRCodeCreate.qrCodeCreate(data)).update();
                    });
                    break;
                case "8ball":
                    interaction.createImmediateResponder().setContent("").addEmbed(EightBall.eightBall(interaction.getArgumentStringValueByName("question").get())).respond();
                    break;
            }
        });

        //legacy commands and ReWords
        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();
            String author = mc.getMessageAuthor().toString();
            String server = mc.getServer().get().toString();
            boolean rmCommandBool = false;

            if (debugmessagelog) {
                if (!mc.getMessageAuthor().equals(self) && !mc.getMessageAuthor().toString().equals("MessageAuthor (id: 919786500890173441, name: Miat Bot)")) {
                    try {
                        Webhook.send(ConfigHandler.getString("WebhookURL", configFile), "'" + m + "'\n\n- " + author + "\n- At " + time + " \n- " + server);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (m.startsWith(prefix)) {
                System.out.println(m);
            }

            if (m.startsWith(prefix)) {
                String[] parts = m.split(" ", 2);
                String command = parts[0].toLowerCase().replace(prefix, "");

                switch (command) {
                    case "randfr":
                        Respond.replyNoPing(mc,RandFr.randomFriend());
                        break;
                    case "inspiro":
                        Respond.replyNoPing(mc,Inspiro.inspiro());
                        break;
                    case "godsays":
                        Respond.replyNoPing(mc,Godsays.godSays());
                        break;
                    case "miat":
                        Respond.replyNoPing(mc,"https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 18) + ".png?raw=true");
                        break;
                    case "base64":
                        Respond.replyNoPing(mc,Vase64.vase64(m));
                        break;
                    case "help":
                        Respond.replyNoPing(mc,"Help is in the ``/miathelp`` slash command now!");
                        break;
                    case "setactivity":
                        Respond.replyNoPing(mc,SetActivity.setactivity(m, mc, api, prefix));
                        break;
                    case "animalfact":
                        Respond.replyNoPing(mc,AnimalFact.animalFact());
                        break;
                    case "wiki":
                        Respond.replyNoPing(mc,Wikipedia.randomArticle());
                        break;
                    case "uptime":
                        Respond.replyNoPing(mc,Uptime.uptime(startTime));
                        break;
                    case "joke":
                        Random random = new Random();
                        int randomNumber = random.nextInt(15);
                        if (randomNumber == 0 || randomNumber == 1) {
                            mc.addReactionsToMessage("\uD83E\uDDBA");
                            mc.addReactionsToMessage("\uD83D\uDEE0️");
                            mc.addReactionsToMessage("\uD83D\uDEA7");
                            mc.addReactionsToMessage("\uD83D\uDC77");
                            Respond.replyNoPing(mc,"https://cdn.discordapp.com/attachments/1100888255483875428/1123410595333537943/under_construction.mp4");
                        } else {
                            Respond.replyNoPing(mc,RandomJoke.randomJoke());
                        }
                        break;
                    case "qr":
                        String data = m.replace(prefix + "qr ", "");
                        Respond.replyNoPing(mc,QRCodeCreate.qrCodeCreate(data));
                        break;
                    case "bestclient":
                        Color seppuku = new Color(153, 0, 238);
                        EmbedBuilder e = new EmbedBuilder().setTitle("Seppuku").setDescription("Seppuku is one of the best clients of all time, ever!").setAuthor("Seppuku", "https://github.com/seppukudevelopment/seppuku", "https://github.com/seppukudevelopment/seppuku/raw/master/res/seppuku_full.png").addField("Seppuku Download", "https://github.com/seppukudevelopment/seppuku/releases").addInlineField("Github", "https://github.com/seppukudevelopment/seppuku").addInlineField("Website", "https://seppuku.pw").setColor(seppuku).setFooter("Seppuku", "https://github.com/seppukudevelopment/seppuku").setImage("https://github.com/seppukudevelopment/seppuku/blob/master/res/seppuku_full.png?raw=true").setThumbnail("https://github.com/seppukudevelopment/seppuku/blob/master/src/main/resources/assets/seppukumod/textures/seppuku-logo.png?raw=true");
                        Respond.replyNoPing(mc,e);
                        break;
                    case "rm":
                    case "remove":
                        rmCommandBool = true;
                        Respond.replyNoPing(mc,DeleteMessage.deleteOwnCommandResponse(mc));
                        Thread rmMsg = new Thread(() -> {
                            Message notif = mc.getChannel().getMessages(1).join().getNewestMessage().get();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            notif.delete();
                        });
                        rmMsg.start();
                        break;
                    case "purge":
                        String amt = m.replace(prefix + "purge ", "");
                        Respond.replyNoPing(mc,Purge.purge(mc, amt));
                        Thread removeNotice = new Thread(() -> {
                            Message notif = mc.getChannel().getMessages(1).join().getNewestMessage().get();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            notif.delete();
                        });
                        removeNotice.start();
                        break;
                    case "translate":
                        String textToTranslate = m.replace(prefix + "translate ", "");
                        Respond.replyNoPing(mc,Trnsl.trnsl(translator, textToTranslate, Language.ENGLISH));
                        break;
                    case "deepl":
                        if (deeplEnabled) {
                            String deepLTextToTranslate = m.replace(prefix + "deepl ", "");
                            Respond.replyNoPing(mc,DeepL.deepl(deepLTranslator, deepLTextToTranslate, "en-US"));
                        } else {
                            Respond.replyNoPing(mc,"DeepL translation is not enabled.");
                        }
                        break;
                    case "8ball":
                        Respond.replyNoPing(mc,EightBall.eightBall(m));
                        break;
                    case "supermagicnumber":
                    case "magicnumber":
                        if (parts.length > 1) {
                            String numbers = parts[1].replaceAll("[^0-9]","");
                            Respond.replyNoPing(mc,SuperMagicNumber.superMagic(numbers));
                        } else {
                            Respond.replyNoPing(mc,SuperMagicNumber.superMagic(mc.getMessageAuthor().getIdAsString()));
                        }
                        break;
                    case "spiritfriend":
                        if (parts.length > 1) {
                            String numbers = parts[1].replaceAll("[^0-9]", "");
                            Respond.replyNoPing(mc,SpiritFriend.spiritFriend(numbers));
                        } else {
                            Respond.replyNoPing(mc,SpiritFriend.spiritFriend(mc.getMessageAuthor().getIdAsString()));
                        }
                        break;
                    case "fotw":
                    case "friendoftheweek":
                        if (parts.length > 1) {
                            String numbers = parts[1].replaceAll("[^0-9]","");
                            Respond.replyNoPing(mc,SpiritFriend.friendOfTheWeek(numbers));
                        } else {
                            Respond.replyNoPing(mc,SpiritFriend.friendOfTheWeek(mc.getMessageAuthor().getIdAsString()));
                        }
                        break;
                    case "fotd":
                    case "friendoftheday":
                        if (parts.length > 1) {
                            String numbers = parts[1].replaceAll("[^0-9]","");
                            Respond.replyNoPing(mc,SpiritFriend.friendOfTheDay(numbers));
                        } else {
                            Respond.replyNoPing(mc,SpiritFriend.friendOfTheDay(mc.getMessageAuthor().getIdAsString()));
                        }
                        break;
                    case "collatz":
                        if (parts.length > 1) {
                            String number = parts[1].replaceAll("[^0-9]","");
                            Respond.replyNoPing(mc,Collatz.collatz(number));
                        } else {
                            Random ran = new Random();
                            int number = ran.nextInt(100000000);
                            Respond.replyNoPing(mc,Collatz.collatz(String.valueOf(number)));
                        }
                        break;
                    case "refresh":
                        if (Whitelist.whitelisted(mc.getMessageAuthor().getIdAsString())) {
                            System.out.println("Refreshing Config and AI characters...");
                            configFile = ReadFile.getFull("ServerFiles/config.json");
                            characterList = ReadFile.getFull("ServerFiles/characters.json");
                            System.out.println("Refreshed.");
                            mc.getChannel().sendMessage("Config and AI characters refreshed.");
                        } else {
                            mc.getChannel().sendMessage("You are not on the whitelist.");
                        }
                        break;
                    case "image":
                    case "img":
                    case "search":
                        Respond.replyNoPing(mc,ImageSearch.search(parts[1]));
                        break;
                    case "recipe":
                        Respond.replyNoPing(mc,"2 tablespoons oil\n" +
                                "1 medium onion, diced small\n" +
                                "3 cloves garlic, minced\n" +
                                "1 pound lean ground beef\n" +
                                "2 tablespoons tomato paste\n" +
                                "2 & 1/2 tablespoons chili powder\n" +
                                "1 & 1/2 tablespoons ground cumin\n" +
                                "1 & 1/2 teaspoons salt\n" +
                                "1/2 teaspoon black pepper\n" +
                                "1/4 teaspoon cinnamon\n" +
                                "1 & 1/2 cups beef broth\n" +
                                "8 ounce tomato sauce\n" +
                                "15 ounce can crushed tomatoes\n" +
                                "\n" +
                                "1. Warm the oil in a large pot over medium-high heat. Add in the onion and garlic. Cook, stirring occasionally, for 3-4 minutes until the onion becomes translucent. \n" +
                                "2. Add the ground beef to the pot. Cook for 5-6 minutes, breaking it apart, until browned and no pink remains.\n" +
                                "3. Stir in the tomato paste, chili powder, cumin, salt, pepper, and cinnamon until everything is thoroughly combined.\n" +
                                "4. Pour in the broth, tomato sauce, and crushed tomatoes. Stir well.\n" +
                                "5. Bring the liquid to a boil, then reduce heat to a gentle simmer (low to medium-low.) Cook, uncovered, for about 20 minutes, stirring occasionally to prevent sticking. \n" +
                                "6. Remove the pot from the heat and let rest for 5 minutes.");
                    case "ml":
                        if (parts.length > 1) {
                            String toggle = parts[1];
                            String id = mc.getMessageAuthor().getIdAsString();
                            switch (toggle) {
                                case "on":
                                    if (Whitelist.whitelisted(id)) {
                                        debugmessagelog = true;
                                        mc.getMessage().reply("Debug Message Log on.");
                                    } else {
                                        Respond.replyNoPing(mc,"You are not on the debug whitelist.");
                                    }
                                    break;
                                case "off":
                                    if (Whitelist.whitelisted(id)) {
                                        debugmessagelog = false;
                                        mc.getMessage().reply("Debug Message Log off.");
                                    } else {
                                        Respond.replyNoPing(mc,"You are not on the debug whitelist.");
                                    }
                                    break;
                            }
                        }
                        break;
                    default:
                        //AI conversation start code
                        String[] characters = parts[0].toLowerCase().replace(prefix,"").split(",");
                        boolean invalidCharacter = false;
                        boolean doConcat = false;
                        StringBuilder invalidCharacterName = new StringBuilder();
                        if (GetCharacter.inList(characters[0])) {
                            for (String individial : characters) {
                                if (!GetCharacter.inList(individial)) {           //if the name of the author field is not in the list of characters
                                    invalidCharacter = true;
                                    if (doConcat == true) {
                                        invalidCharacterName.append(", ");
                                    }
                                    invalidCharacterName.append(individial);
                                    doConcat = true;
                                }
                            }
                            if (invalidCharacter == false) {
                                rmCommandBool = true;
                                mc.addReactionsToMessage("\uD83D\uDE80");
                                Thread aiThread = new Thread(() -> {
                                    TactAI instance = new TactAI();
                                    instance.aiRequest(m, mc, characters);
                                    mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDE80");
                                });
                                aiThread.start();
                                break;
                            } else {
                                Respond.replyNoPing(mc,"Invalid character - ``" + invalidCharacterName + "``.");
                            }
                        }
                }
            }
            for (String channel : geminiChannels) {
                if (Long.toString(mc.getMessage().getChannel().getId()).equals(channel))
                    if (!mc.getMessage().getAttachments().isEmpty()) {
                        if (mc.getMessage().getAttachments().get(0).isImage()) {
                            Thread geminiThread = new Thread(() -> {
                                GeminiImageCheck instance = new GeminiImageCheck();
                                instance.check(mc);
                            });
                            geminiThread.start();
                    }
                }
            }

            if (mc.getMessage().getMentionedUsers().contains(self)) { //if it pings the bot
                if (m.indexOf("<@" + self.getIdAsString() + ">") > 0) {
                    String[] halves = m.split("<@" + self.getIdAsString() + ">");
                    String[] characters = halves[0].split(", ");
                    boolean invalidCharacter = false;
                    boolean doConcat = false;
                    StringBuilder invalidCharacterName = new StringBuilder();
                    rmCommandBool = true;
                    for (String individial : characters) {
                        if (!GetCharacter.inList(individial)) {           //if the name of the author field is not in the list of characters
                            invalidCharacter = true;
                            if (doConcat == true) {
                                invalidCharacterName.append(", ");
                            }
                            invalidCharacterName.append(individial);
                            doConcat = true;
                        }
                    }
                    String prompt;
                    if (halves.length == 1) {
                        prompt = "";
                    } else {
                        prompt = halves[1];
                    }
                    if (invalidCharacter == false) {
                        mc.addReactionsToMessage("\uD83C\uDFDE️");
                        Thread aiThread = new Thread(() -> {
                            TactAI instance = new TactAI();
                            instance.aiRequest(prompt, mc, characters);
                            mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                        });
                        aiThread.start();
                    }
                }
            }

            if (rmCommandBool == false) {
                if (mc.getMessage().getReferencedMessage().isPresent() && mc.getMessage().getMentionedUsers().contains(self)) {                           //if message has reply to another message
                    Message referencedMessage = mc.getMessage().getReferencedMessage().get();       //set the replied to message to variable
                    if (referencedMessage.getUserAuthor().get().equals(self)) {                     //if the author is the bot
                        if (!referencedMessage.getEmbeds().isEmpty()) {                             //if there are embeds
                            if (referencedMessage.getEmbeds().get(0).getAuthor().isPresent()) {     //if the embed has an author field
                                //this block checks if the characters in the replied-to message actually exist
                                String[] characters = referencedMessage.getEmbeds().get(0).getAuthor().get().getName().split(", ");    //get the names in the author field of the embed
                                boolean invalidCharacter = false;
                                boolean doConcat = false;
                                StringBuilder invalidCharacterName = new StringBuilder();
                                for (String individial : characters) {
                                    if (!GetCharacter.inList(individial)) {           //if the name of the author field is not in the list of characters
                                        invalidCharacter = true;
                                        if (doConcat == true) {
                                            invalidCharacterName.append(", ");
                                        }
                                        invalidCharacterName.append(individial);
                                        doConcat = true;
                                    }
                                }
                                if (invalidCharacter == false) {
                                    mc.addReactionsToMessage("\uD83C\uDFDE️");
                                    Thread aiThread = new Thread(() -> {
                                        TactAI instance = new TactAI();
                                        instance.aiRequest(m, mc, characters);
                                        mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                                    });
                                    aiThread.start();
                                } else {
                                    Respond.replyNoPing(mc,"Invalid character - ``" + invalidCharacterName +"``.");
                                }
                            }
                        }
                    }
                }
            }

            for (String wordsGoodWordsExactMatch : reWordsGoodWordsExactMatch) {
                if (m.contains(wordsGoodWordsExactMatch)) {
                    ReWords.scoreModifier(mc, 1);
                }
            }
            for (String reWordsGoodWord : reWordsGoodWords) {
                if (m.toLowerCase().contains(reWordsGoodWord)) {
                    ReWords.scoreModifier(mc, 1);
                }
            }
            for (String wordsBadWordsExactMatch : reWordsBadWordsExactMatch) {
                if (m.contains(wordsBadWordsExactMatch)) {
                    ReWords.scoreModifier(mc, -1);
                }
            }
            for (String reWordsBadWord : reWordsBadWords) {
                if (m.toLowerCase().contains(reWordsBadWord)) {
                    ReWords.scoreModifier(mc, -1);
                }
            }

            //this is hardcoded and will stay hardcoded because i find it funny when people get told to not say the n word.
            //i dont even care about the word itself, its funny to see people act tough against a bot when they say a word.
            if (m.toLowerCase().contains("nigg") || m.toLowerCase().contains("n1gg") || m.toLowerCase().contains("kotlin user")) {
                if (reminder < 10) {
                    mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__");
                    reminder++;
                } else {
                    mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__\n-# *Delete these reminders by reacting with :x: emoji*");
                    reminder = 1;
                }
            }
        });

        //deleted message logger
        api.addMessageDeleteListener(md -> {
            try {
                if (!md.getMessageAuthor().get().getIdAsString().equals(self)) {
                   Channel logChannel = md.getServer().get().getChannelById(DeletedMessageLogChannel.channelRetriever(md.getServer().get().getIdAsString())).get();

                   EmbedBuilder e = new EmbedBuilder();
                   e.setAuthor(md.getMessageAuthor().get().asUser().get());
                   e.setColor(Color.orange);
                   e.addField("\u200b", "'" + md.getMessageContent().get() + "' \n\n - " + time + "\n" + "Deleted in : " + md.getChannel().toString());
                   logChannel.asServerTextChannel().get().sendMessage(e);
                }
            } catch (NoSuchElementException ignored) {}
        });

        api.addServerJoinListener(botJoin -> {
           String serverID = botJoin.getServer().getIdAsString();
            try {
                FileWriter fw = new FileWriter("ServerFiles/" + serverID + ".txt");
                fw.write("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            User me = api.getYourself();
            EmbedBuilder join = new EmbedBuilder();
            join.setAuthor(me);
            join.setTitle("Hello," + botName + " is here!");
            join.setThumbnail("https://cdn.discordapp.com/attachments/919786447488290816/920839787789836288/miat.jpeg");
            join.addField("Information :", "Slash Commands are supported! \nPrefix : ``" + prefix +"``\nCreator : ``HAV0X`` & ``arsonfrog``");
            join.addField("Get Started :", "Help : ``/miathelp``\nSet Deleted Message Log Channel : ``/setlogchannel``");
            botJoin.getServer().getSystemChannel().get().sendMessage(join);
        });

        api.addReactionAddListener(ra -> {
            /*
            Note to self, this code is what sends the translated text while also replying to the original untranslated message without pinging
            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(translator, messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel())
            setAllowedMentions() makes it to the reply doesnt ping, replyTo() sets the message to be replied to, and send() sets the channel to send the message in.
            why doesn't javacord just have a .replyNoPing() ?????????????????
            */
            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");
            String allEmoji = ra.requestMessage().join().getReactions().toString();
            String messageContent = ra.requestMessage().join().getContent();
            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            Language targetLang;
            String dlLang = null;

            switch(emoji) {
                case "❌":
                    if (deleteCandidate.equals(self.getIdAsString())) { //self delete when X emoji is seen
                        if (messageContent.contains("__**Racial slurs are discouraged!**__")) {
                            api.getMessageById(ra.getMessageId(),ra.getChannel()).join().delete();
                        }
                        try {
                            if (ra.requestMessage().join().getEmbeds().get(0).getTitle().get().startsWith("Translated Text")) { //only delete if it is a translation message
                                api.getMessageById(ra.getMessageId(), ra.getChannel()).join().delete();
                            }
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                    return;

                case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
                case "\uD83C\uDDE8\uD83C\uDDE6": //Canada English
                case "\uD83C\uDDFB\uD83C\uDDEE": //US Virgin Islands English
                case "\uD83C\uDDEC\uD83C\uDDFA": //Guam English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-US";
                    break;
                case "\uD83C\uDDE6\uD83C\uDDFA": //Australia English
                case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
                case "\uD83C\uDDF3\uD83C\uDDFF": //New Zealand English
                case "\uD83C\uDDFB\uD83C\uDDEC": //British Virgin Islands English
                case "\uD83C\uDDEC\uD83C\uDDEE": //Gibraltar English
                case "\uD83C\uDDEE\uD83C\uDDF2": //Isle of Man English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-GB";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                    targetLang = Language.POLISH;
                    dlLang = "pl";
                    break;
                case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
                case "\uD83C\uDDF8\uD83C\uDDEC": //Singapore Chinese Simplified
                    targetLang = Language.CHINESE_SIMPLIFIED;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDED\uD83C\uDDF0": //Hong Kong Chinese Traditional
                case "\uD83C\uDDF9\uD83C\uDDFC": //Taiwan Chinese Traditional
                    targetLang = Language.CHINESE_TRADITIONAL;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
                case "\uD83C\uDDF5\uD83C\uDDF0": //Pakistan Hindi
                    targetLang = Language.HINDI;
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
                case "\uD83C\uDDE8\uD83C\uDDF4": //Colombia Spanish
                case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
                case "\uD83C\uDDE6\uD83C\uDDF7": //Argentina Spanish
                case "\uD83C\uDDF5\uD83C\uDDEA": //Peru Spanish
                case "\uD83C\uDDFB\uD83C\uDDEA": //Venezuela Spanish
                case "\uD83C\uDDE8\uD83C\uDDF1": //Chile Spanish
                case "\uD83C\uDDEC\uD83C\uDDF9": //Guatemala Guatemala
                case "\uD83C\uDDF5\uD83C\uDDF7": //Puerto Rico Spanish
                    targetLang = Language.SPANISH;
                    dlLang = "es";
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7": //France French
                case "\uD83C\uDDF2\uD83C\uDDEC": //Madagascar French
                case "\uD83C\uDDE8\uD83C\uDDF2": //Cameroon French
                case "\uD83C\uDDE8\uD83C\uDDEE": //Cote d' Ivoire French
                case "\uD83C\uDDF3\uD83C\uDDEA": //Niger French
                case "\uD83C\uDDE7\uD83C\uDDEF": //Benin French
                    targetLang = Language.FRENCH;
                    dlLang = "fr";
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
                case "\uD83C\uDDE7\uD83C\uDDFE": //Belarus Russian
                    targetLang = Language.RUSSIAN;
                    dlLang = "ru";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
                case "\uD83C\uDDF2\uD83C\uDDFF": //Mozambique Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-PT";
                    break;
                case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-BR";
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
                case "\uD83C\uDDF1\uD83C\uDDEE": //Liechtenstein German
                    targetLang = Language.GERMAN;
                    dlLang = "de";
                    break;
                case "\uD83C\uDDEF\uD83C\uDDF5": //Japan Japanese
                    targetLang = Language.JAPANESE;
                    dlLang = "ja";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDED": //Philippines Filipino
                    targetLang = Language.FILIPINO;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF7": //South Korea Korean
                case "\uD83C\uDDF0\uD83C\uDDF5": //North Korea Korean
                    targetLang = Language.KOREAN;
                    dlLang = "ko";
                    break;
                case "\uD83C\uDDFB\uD83C\uDDF3": //Vietnam Vietnamese
                    targetLang = Language.VIETNAMESE;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF9": //Italy Italian
                    targetLang = Language.ITALIAN;
                    dlLang = "it";
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFE": //Malaysia Malay
                case "\uD83C\uDDE7\uD83C\uDDF3": //Brunei Malay
                    targetLang = Language.MALAY;
                    break;
                default:
                    return;
            }
            if (!Arrays.toString(ignoredChannels).contains(ra.requestMessage().join().getChannel().getIdAsString())) { //if IgnoredChannels does NOT include the reaction channel, continue
                if (allEmoji.contains(deepLEmoji)) {
                    if (deeplEnabled) { //Check for the DeepL emoji (the one that says to use DeepL if reacted)
                        if (dlLang == null) { //some languages aren't on DeepL. If the language isn't supported, it will be null because it wasn't set in the switch case
                            if (useGoogleAsFallbackForDeepL) { //if DeepL isn't able to translate into the language, use Google as a fallback translator.
                                AllowedMentions none = new AllowedMentionsBuilder().setMentionRepliedUser(true).build();
                                new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(translator, messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel());
                            } else {
                                ra.getChannel().sendMessage("This language is not supported by DeepL.");
                            }
                        } else {
                            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(DeepL.deepl(deepLTranslator, messageContent, dlLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel()); //Translate if successful
                        }
                    } else {
                        new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(translator, messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel()); //use Google if DeepL isnt enabled.
                    }
                } else {
                    new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(Trnsl.trnsl(translator, messageContent, targetLang)).replyTo(ra.requestMessage().join()).send(ra.requestMessage().join().getChannel()); //use Google if there isnt a DeepL emoji.
                }
            }
        });
    }
}