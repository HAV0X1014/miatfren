package miat.listener;

import miat.features.*;
import miat.filehandler.ConfigHandler;
import miat.filehandler.Webhook;
import miat.util.*;
import miat.util.Character;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static miat.MiatMain.*;

public class MessageHandler extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent mc) {
        messageCache.put(mc.getMessageId(), mc.getMessage());
        boolean rmCommandBool = false; //this is set to true if there is a command sent that we dont want to activate the AI reply feature.
        //the name is a misnomer, since it is used by a lot more than the rm command

        if (debugmessagelog) {
            System.out.println(mc.getMessage().getContentRaw());
            //the user id its checking is a specific webhook that miat has to ignore, dont freak out
            if (!mc.getMessage().getAuthor().getId().equals(self) && !mc.getMessage().getAuthor().getId().equals("1309646204849618994")) {
                new Webhook().send(ConfigHandler.getString("WebhookURL"), "```" + mc.getMessage().getContentRaw() + "```\n-# At " + new Date() + "\n-# " + mc.getAuthor().toString() + " \n-# " + mc.getGuild().toString() + "\n-# " + mc.getMessage().getChannel().toString());
            }
        }
        String m = mc.getMessage().getContentRaw();

        if (m.startsWith(prefix)) {
            System.out.println(m);
            String[] parts = m.split(" ",2);
            String command = parts[0].toLowerCase().replace(prefix,"");
            switch (command) {
                case "animalfact":
                    new Reply().noPing(mc,AnimalFact.animalFact().build());
                    break;
                case "base64":
                    if (parts.length > 1) {
                        new Reply().noPing(mc, Vase64.vase64(parts[1]).build());
                    } else {
                        new Reply().noPing(mc, "This command requires encode or decode to be specified.");
                    }
                    break;
                case "bestclient":
                    Color seppuku = new Color(153, 0, 238);
                    EmbedBuilder e = new EmbedBuilder()
                            .setTitle("Seppuku")
                            .setDescription("Seppuku is one of the best clients of all time, ever!")
                            .setAuthor("Seppuku", "https://github.com/seppukudevelopment/seppuku", "https://github.com/seppukudevelopment/seppuku/raw/master/res/seppuku_full.png")
                            .addField("Seppuku Download", "https://github.com/seppukudevelopment/seppuku/releases",true)
                            .addField("Github", "https://github.com/seppukudevelopment/seppuku",true)
                            .addField("Website", "https://seppuku.pw",true)
                            .setColor(seppuku)
                            .setFooter("Seppuku", "https://github.com/seppukudevelopment/seppuku")
                            .setImage("https://github.com/seppukudevelopment/seppuku/blob/master/res/seppuku_full.png?raw=true")
                            .setThumbnail("https://github.com/seppukudevelopment/seppuku/blob/master/src/main/resources/assets/seppukumod/textures/seppuku-logo.png?raw=true");
                    new Reply().noPing(mc, e.build());
                    break;
                case "collatz":
                    if (parts.length > 1) {
                        String number = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc, Collatz.collatz(number).build());
                    } else {
                        Random ran = new Random();
                        int number = ran.nextInt(100000000);
                        new Reply().noPing(mc, Collatz.collatz(String.valueOf(number)).build());
                    }
                    break;
                case "fotw":
                case "friendoftheweek":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc, SpiritFriend.friendOfTheWeek(numbers).build());
                    } else {
                        new Reply().noPing(mc, SpiritFriend.friendOfTheWeek(mc.getAuthor().getId()).build());
                    }
                    break;
                case "fotd":
                case "friendoftheday":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc,SpiritFriend.friendOfTheDay(numbers).build());
                    } else {
                        new Reply().noPing(mc,SpiritFriend.friendOfTheDay(mc.getAuthor().getId()).build());
                    }
                    break;
                case "godsays":
                    new Reply().noPing(mc, Godsays.godSays());
                    break;
                case "help":
                    new Reply().noPing(mc, "Help is in the ``/miathelp`` slash command now!");
                    break;
                case "ping":
                    new Reply().noPing(mc,"yes i saw that.");
                    break;
                case "inspiro":
                    new Reply().noPing(mc, Inspiro.inspiro());
                    break;
                case "image":
                case "img":
                case "imagesearch":
                case "search":
                    new Reply().noPing(mc,ImageSearch.search(parts[1]).build());
                    break;
                case "imagegen":
                case "imageprompt":
                case "genimage":
                case "makeimage":
                    rmCommandBool = true;
                    mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDD8C\uFE0F")).queue();
                    Thread imageGen = new Thread(() -> {
                        ImageGen ig = new ImageGen();
                        ig.generate(mc);
                    });
                    imageGen.start();
                    break;
                case "joke":
                    Random random = new Random();
                    int randomNumber = random.nextInt(15);
                    if (randomNumber == 0 || randomNumber == 1) {
                        mc.getMessage().addReaction(Emoji.fromFormatted("\uD83E\uDDBA")).queue();
                        mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDEE0️")).queue();
                        mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDEA7")).queue();
                        mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDC77")).queue();
                        mc.getMessage().reply("https://cdn.discordapp.com/attachments/1100888255483875428/1123410595333537943/under_construction.mp4").queue();
                    } else {
                        new Reply().noPing(mc, Joke.randomJoke().build());
                    }
                    break;
                case "miat":
                    new Reply().noPing(mc, "https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 18) + ".png?raw=true");
                    break;
                case "ml":
                    if (parts.length > 1) {
                        String toggle = parts[1];
                        String id = mc.getAuthor().getId();
                        switch (toggle) {
                            case "on":
                                if (Whitelist.whitelisted(id)) {
                                    debugmessagelog = true;
                                    new Reply().noPing(mc,"Debug Message Log on.");
                                } else {
                                    new Reply().noPing(mc,"You are not on the debug whitelist.");
                                }
                                break;
                            case "off":
                                if (Whitelist.whitelisted(id)) {
                                    debugmessagelog = false;
                                    new Reply().noPing(mc,"Debug Message Log off.");
                                } else {
                                    new Reply().noPing(mc,"You are not on the debug whitelist.");
                                }
                                break;
                        }
                    }
                    break;
                case "purge":
                    String amt = m.replace(prefix + "purge ", "");
                    new Reply().noPing(mc, Purge.purge(mc, amt));
                    break;
                case "qr":
                    String data = m.replace(prefix + "qr ", "");
                    QRCode.qrCodeCreate(mc,data);
                    break;
                case "recipe":
                    new Reply().noPing(mc,"2 tablespoons oil\n" +
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
                    break;
                case "randfr":
                    new Reply().noPing(mc, RandFr.randomFriend());
                    break;
                case "refresh":
                    if (Whitelist.whitelisted(mc.getAuthor().getId())) {
                        System.out.println("Refreshing Config and AI characters...");
                        ConfigHandler.refresh();
                        System.out.println("Refreshed.");
                        new Reply().noPing(mc,"Config and AI characters refreshed.");
                    } else {
                        new Reply().noPing(mc,"You are not on the whitelist.");
                    }
                    break;
                case "remove":
                case "rm":
                    rmCommandBool = true;
                    DeleteMessage.deleteOwnCommandResponse(mc);
                    break;
                case "spiritfriend":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]", "");
                        new Reply().noPing(mc, SpiritFriend.spiritFriend(numbers).build());
                    } else {
                        new Reply().noPing(mc, SpiritFriend.spiritFriend(mc.getAuthor().getId()).build());
                    }
                    break;
                case "setactivity":
                    if (Whitelist.whitelisted(mc.getAuthor().getId())) {
                        m = m.replace(prefix + "setactivity ", "");
                        jda.getPresence().setActivity(Activity.customStatus(m));
                        new Reply().noPing(mc,"Activity has been updated.");
                    } else new Reply().noPing(mc,"You are not on the whitelist.");
                    break;
                case "warning":
                case "warn":
                    if (parts.length > 1) {
                        //if we got a ping, then see if there is an @ mention or if there is other parameters
                        Pattern mention = Pattern.compile("<@\\d+>");
                        Matcher matcher = mention.matcher(parts[1]);
                        if (matcher.find()) {
                            String userToWarn = matcher.group();
                            String warning = parts[1].replace(userToWarn,"").stripLeading();
                            if (warning.isEmpty()) {
                                //if we got a ping but no reason
                                new Reply().noPing(mc, FakeWarn.warnWithUser(mc.getAuthor().getId(), userToWarn).build());
                            } else {
                                //if we got a ping WITH a reason
                                new Reply().noPing(mc, FakeWarn.warnWithUserAndReason(mc.getAuthor().getId(),userToWarn,warning).build());
                            }
                        } else {
                            //if there is no ping
                            new Reply().noPing(mc, FakeWarn.warnWithReason(mc.getAuthor().getId(),parts[1].stripLeading()).build());
                        }
                    } else {
                        //if there is no arguments at all
                        new Reply().noPing(mc, FakeWarn.warnWithNothing(mc.getAuthor().getId()).build());
                    }
                    break;
                case "wiki":
                    new Reply().noPing(mc, Wikipedia.randomArticle());
                    break;
                case "8ball":
                    new Reply().noPing(mc, EightBall.eightBall(m).build());
                    break;
                case "kf3summary":
                    if (parts.length > 1) {
                        KF3Summary.friendStory(parts[1],mc);
                    } else {
                        new Reply().noPing(mc, "No Friend ID supplied, please give a valid KF3 Friend ID.");
                    }
                    break;
                case "memusage":
                case "memory":
                case "usage":
                case "mem":
                case "stats":
                case "uptime":
                    new Reply().noPing(mc,BotStats.stats().build());
                    break;
                case "gemini":
                    if (parts.length > 1) {
                        try (TypeContinuously tc = new TypeContinuously(mc.getChannel())) {
                            new Reply().noPing(mc, Gemini.send(mc).build());
                        }
                    } else {
                        new Reply().noPing(mc, "supply a prompt to send to gemini");
                    }
                    break;
                case "cowsay":
                case "fortune":
                    new Reply().noPing(mc,Fortune.cowsay().build());
                    break;
                default:
                    //AI conversation start code using prefix
                    rmCommandBool = true;
                    String[] characters = parts[0].toLowerCase().replace(prefix,"").split(",");
                    boolean invalidCharacter = false;
                    boolean doConcat = false;
                    StringBuilder invalidCharacterName = new StringBuilder();
                    if (Character.inList(characters[0],mc.getAuthor().getId())) {
                        for (String individial : characters) {
                            if (!Character.inList(individial,mc.getAuthor().getId())) {           //if the name of the author field is not in the list of characters
                                invalidCharacter = true;
                                if (doConcat == true) {
                                    invalidCharacterName.append("``, ``");
                                }
                                invalidCharacterName.append(individial);
                                doConcat = true;
                            }
                        }
                        if (!invalidCharacter) {
                            mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDE80")).queue();
                            String finalM = m;
                            Thread aiThread = new Thread(() -> {
                                TactAI instance = new TactAI();
                                instance.aiRequest(finalM, mc, characters);
                                mc.getMessage().removeReaction(Emoji.fromFormatted("\uD83D\uDE80")).queue();
                            });
                            aiThread.start();
                            break;
                        } else {
                            //this is intentionally a blocking message send so that we will only get this message from the following .getHistory() call.
                            mc.getMessage().reply("Invalid character: ``" + invalidCharacterName + "``.").setMessageReference(mc.getMessage()).mentionRepliedUser(false).complete();
                            mc.getChannel().getHistory().retrievePast(1).queue(messages -> {
                                if (!messages.isEmpty()) {
                                    Message latestMessage = messages.get(0);
                                    latestMessage.delete().queueAfter(5, TimeUnit.SECONDS);
                                }
                            });
                        }
                    }
            }
        }

        //MirAI style chat start (formatted like "character name, @miatfren prompt content")
        if (mc.getMessage().getMentions().getUsers().contains(jda.getSelfUser())) { //if it pings the bot
            if (m.indexOf("<@" + self + ">") > 0) {
                String[] halves = m.split("<@" + self + ">");
                String[] characters = halves[0].split(", ");
                boolean invalidCharacter = false;
                boolean doConcat = false;
                StringBuilder invalidCharacterName = new StringBuilder();
                rmCommandBool = true;
                for (String individial : characters) {
                    if (!Character.inList(individial,mc.getAuthor().getId())) {           //if the name of the author field is not in the list of characters
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
                    mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDE80")).queue();
                    Thread aiThread = new Thread(() -> {
                        TactAI instance = new TactAI();
                        instance.aiRequest(prompt, mc, characters);
                        mc.getMessage().removeReaction(Emoji.fromFormatted("\uD83D\uDE80")).queue();
                    });
                    aiThread.start();
                }
            }
        }

        if (!rmCommandBool) {
            //if there is a reference, if the referenced message was made by us, if the current message pings the bot
            if ((mc.getMessage().getReferencedMessage() != null) && mc.getMessage().getReferencedMessage().getAuthor().getId().equals(self) && mc.getMessage().getMentions().getUsers().contains(jda.getSelfUser())) {
                Message referencedMessage = mc.getMessage().getReferencedMessage();     //set the replied to message to variable
                if (!referencedMessage.getEmbeds().isEmpty()) {                         //if there are embeds
                    if (referencedMessage.getEmbeds().get(0).getAuthor() != null) {     //if the embed has an author field
                        //this block checks if the characters in the replied-to message actually exist
                        String[] characters = Objects.requireNonNull(Objects.requireNonNull(referencedMessage.getEmbeds().get(0).getAuthor()).getName()).split(", ");    //get the names in the author field of the embed
                        boolean invalidCharacter = false;
                        boolean doConcat = false;
                        StringBuilder invalidCharacterName = new StringBuilder();
                        for (String individial : characters) {
                            Character.getDescription(individial,mc.getAuthor().getId());
                            if (!Character.inList(individial,mc.getAuthor().getId())) {           //if the name of the author field is not in the list of characters
                                invalidCharacter = true;
                                if (doConcat == true) {
                                    invalidCharacterName.append(", ");
                                }
                                invalidCharacterName.append(individial);
                                doConcat = true;
                            }
                        }
                        if (invalidCharacter == false) {
                            mc.getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDE80")).queue();
                            String finalM1 = m;
                            Thread aiThread = new Thread(() -> {
                                TactAI instance = new TactAI();
                                instance.aiRequest(finalM1, mc, characters);
                                mc.getMessage().removeReaction(Emoji.fromFormatted("\uD83D\uDE80")).queue();
                            });
                            aiThread.start();
                        } else {
                            //this is intentionally a blocking message send so that we will only get this message from the following .getHistory() call.
                            mc.getMessage().reply("Invalid character: ``" + invalidCharacterName + "``.").setMessageReference(mc.getMessage()).mentionRepliedUser(false).complete();
                            mc.getChannel().getHistory().retrievePast(1).queue(messages -> {
                                if (!messages.isEmpty()) {
                                    Message latestMessage = messages.get(0);
                                    latestMessage.delete().queueAfter(5, TimeUnit.SECONDS);
                                }
                            });
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
        if (m.toLowerCase().contains("nigg") || m.toLowerCase().contains("n1gg") || m.toLowerCase().contains("kotlin user") || m.toLowerCase().contains("khafas") || m.toLowerCase().contains("kafir")) {
            if (nBombCount > 5) {
                mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__\n-# Delete these messages with :x:").queue();
                nBombCount = 0;
            } else {
                mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__").queue();
                nBombCount++;
            }
        }
    }
}
