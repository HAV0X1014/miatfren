package miat.listener;

import me.bush.translator.Language;
import miat.features.*;
import miat.filehandler.*;
import miat.util.*;
import miat.util.Character;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static miat.MiatInit.*;

public class MessageHandler implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent mc) {
        UserHandler.hasConfig(mc.getMessageAuthor().getIdAsString()); //check if the user has a config yet. if not, make one for them.
        String m = mc.getMessageContent();
        String author = mc.getMessageAuthor().toString();
        String server;
        if (mc.getServer().isPresent()) {
            server = mc.getServer().get().toString();
        } else {
            server = "Not a server";
        }
        boolean rmCommandBool = false; //this is set to true if there is a command sent that we dont want to activate the AI reply feature.
        //the name is a misnomer, since it is used by a lot more than the rm command

        if (debugmessagelog) {
            if (!mc.getMessageAuthor().equals(self) && !mc.getMessageAuthor().toString().equals("MessageAuthor (id: 919786500890173441, name: Miat Bot)")) {
                new Webhook().send(ConfigHandler.getString("WebhookURL"), "'" + m + "'\n\n- " + author + "\n- At " + new Date() + " \n- " + server);
            }
        }

        if (m.startsWith(prefix)) {
            System.out.println(m);
            String[] parts = m.split(" ", 2);
            String command = parts[0].toLowerCase().replace(prefix, "");

            switch (command) {
                case "animalfact":
                    new Reply().noPing(mc,AnimalFact.animalFact());
                    break;
                case "base64":
                    if (parts.length > 1) {
                        new Reply().noPing(mc, Vase64.vase64(parts[1]));
                    } else {
                        new Reply().noPing(mc, "This command requires encode or decode to be specified.");
                    }
                    break;
                case "bestclient":
                    Color seppuku = new Color(153, 0, 238);
                    EmbedBuilder e = new EmbedBuilder().setTitle("Seppuku").setDescription("Seppuku is one of the best clients of all time, ever!").setAuthor("Seppuku", "https://github.com/seppukudevelopment/seppuku", "https://github.com/seppukudevelopment/seppuku/raw/master/res/seppuku_full.png").addField("Seppuku Download", "https://github.com/seppukudevelopment/seppuku/releases").addInlineField("Github", "https://github.com/seppukudevelopment/seppuku").addInlineField("Website", "https://seppuku.pw").setColor(seppuku).setFooter("Seppuku", "https://github.com/seppukudevelopment/seppuku").setImage("https://github.com/seppukudevelopment/seppuku/blob/master/res/seppuku_full.png?raw=true").setThumbnail("https://github.com/seppukudevelopment/seppuku/blob/master/src/main/resources/assets/seppukumod/textures/seppuku-logo.png?raw=true");
                    new Reply().noPing(mc, e);
                    break;
                case "collatz":
                    if (parts.length > 1) {
                        String number = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc, Collatz.collatz(number));
                    } else {
                        Random ran = new Random();
                        int number = ran.nextInt(100000000);
                        new Reply().noPing(mc, Collatz.collatz(String.valueOf(number)));
                    }
                    break;
                case "deepl":
                    if (deeplEnabled) {
                        String deepLTextToTranslate = m.replace(prefix + "deepl ", "");
                        new Reply().noPing(mc, DeepL.deepl(deepLTextToTranslate, "en-US"));
                    } else {
                        new Reply().noPing(mc, "DeepL translation is not enabled.");
                    }
                    break;
                case "fotw":
                case "friendoftheweek":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc, SpiritFriend.friendOfTheWeek(numbers));
                    } else {
                        new Reply().noPing(mc, SpiritFriend.friendOfTheWeek(mc.getMessageAuthor().getIdAsString()));
                    }
                    break;
                case "fotd":
                case "friendoftheday":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc,SpiritFriend.friendOfTheDay(numbers));
                    } else {
                        new Reply().noPing(mc,SpiritFriend.friendOfTheDay(mc.getMessageAuthor().getIdAsString()));
                    }
                    break;
                case "godsays":
                    new Reply().noPing(mc, Godsays.godSays());
                    break;
                case "help":
                    new Reply().noPing(mc, "Help is in the ``/miathelp`` slash command now!");
                    break;
                case "inspiro":
                    new Reply().noPing(mc, Inspiro.inspiro());
                    break;
                case "image":
                case "img":
                case "search":
                    new Reply().noPing(mc,ImageSearch.search(parts[1]));
                    break;
                case "imagegen":
                case "imageprompt":
                case "genimage":
                    rmCommandBool = true;
                    mc.addReactionsToMessage("\uD83D\uDD8C\uFE0F");
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
                        mc.addReactionsToMessage("\uD83E\uDDBA");
                        mc.addReactionsToMessage("\uD83D\uDEE0️");
                        mc.addReactionsToMessage("\uD83D\uDEA7");
                        mc.addReactionsToMessage("\uD83D\uDC77");
                        mc.getMessage().reply("https://cdn.discordapp.com/attachments/1100888255483875428/1123410595333537943/under_construction.mp4");
                    } else {
                        new Reply().noPing(mc, RandomJoke.randomJoke());
                    }
                    break;
                case "miat":
                    new Reply().noPing(mc, "https://github.com/balls99dotexe/images/blob/main/miatas/miata" + (int) Math.floor(1 + Math.random() * 18) + ".png?raw=true");
                    break;
                case "magicnumber":
                case "supermagicnumber":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]","");
                        new Reply().noPing(mc, SuperMagicNumber.superMagic(numbers));
                    } else {
                        new Reply().noPing(mc, SuperMagicNumber.superMagic(mc.getMessageAuthor().getIdAsString()));
                    }
                    break;
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
                                    mc.getMessage().reply("You are not on the debug whitelist.");
                                }
                                break;
                            case "off":
                                if (Whitelist.whitelisted(id)) {
                                    debugmessagelog = false;
                                    mc.getMessage().reply("Debug Message Log off.");
                                } else {
                                    mc.getMessage().reply("You are not on the debug whitelist.");
                                }
                                break;
                        }
                    }
                    break;
                case "purge":
                    String amt = m.replace(prefix + "purge ", "");
                    new Reply().noPing(mc, Purge.purge(mc, amt));
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
                case "qr":
                    String data = m.replace(prefix + "qr ", "");
                    new Reply().noPing(mc, QRCode.qrCodeCreate(data));
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
                    if (Whitelist.whitelisted(mc.getMessageAuthor().getIdAsString())) {
                        System.out.println("Refreshing Config and AI characters...");
                        ConfigHandler.refresh();
                        System.out.println("Refreshed.");
                        mc.getChannel().sendMessage("Config and AI characters refreshed.");
                    } else {
                        mc.getChannel().sendMessage("You are not on the whitelist.");
                    }
                    break;
                case "remove":
                case "rm":
                    rmCommandBool = true;
                    new Reply().noPing(mc, DeleteMessage.deleteOwnCommandResponse(mc));
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
                case "spiritfriend":
                    if (parts.length > 1) {
                        String numbers = parts[1].replaceAll("[^0-9]", "");
                        new Reply().noPing(mc, SpiritFriend.spiritFriend(numbers));
                    } else {
                        new Reply().noPing(mc, SpiritFriend.spiritFriend(mc.getMessageAuthor().getIdAsString()));
                    }
                    break;
                case "setactivity":
                    if (Whitelist.whitelisted(mc.getMessageAuthor().getIdAsString())) {
                        m = m.replace(prefix + "setactivity ", "");
                        api.updateActivity(m);
                        new Reply().noPing(mc,"Activity has been updated.");
                    } else new Reply().noPing(mc,"You are not on the whitelist.");
                    break;
                case "translate":
                    String textToTranslate = m.replace(prefix + "translate ", "");
                    new Reply().noPing(mc, Trnsl.trnsl(textToTranslate, Language.ENGLISH));
                    break;
                case "uptime":
                    new Reply().noPing(mc, Uptime.uptime());
                    break;
                case "warning":
                case "warn":
                    //regex to check if there is an @ mention in the message
                    if (parts.length > 1) {
                        //if we got a ping, then see if there is an @ mention or if there is other parameters
                        Pattern mention = Pattern.compile("<@\\d+>");
                        Matcher matcher = mention.matcher(parts[1]);
                        if (matcher.find()) {
                            String userToWarn = matcher.group();
                            String warning = parts[1].replace(userToWarn,"").stripLeading();
                            if (warning.isEmpty()) {
                                //if we got a ping but no reason
                                new Reply().noPing(mc, FakeWarn.warnWithUser(mc.getMessageAuthor().getIdAsString(), userToWarn));
                            } else {
                                //if we got a ping WITH a reason
                                new Reply().noPing(mc, FakeWarn.warnWithUserAndReason(mc.getMessageAuthor().getIdAsString(),userToWarn,warning));
                            }
                        } else {
                            //if there is no ping
                            new Reply().noPing(mc, FakeWarn.warnWithReason(mc.getMessageAuthor().getIdAsString(),parts[1].stripLeading()));
                        }
                    } else {
                        //if there is no arguments at all
                        new Reply().noPing(mc, FakeWarn.warnWithNothing(mc.getMessageAuthor().getIdAsString()));
                    }
                    break;
                case "wiki":
                    new Reply().noPing(mc, Wikipedia.randomArticle());
                    break;
                case "8ball":
                    new Reply().noPing(mc, EightBall.eightBall(m));
                    break;
                case "kf3summary":
                    if (parts.length > 1) {
                        KF3Summary.friendStory(parts[1],mc);
                    } else {
                        new Reply().noPing(mc, "No Friend ID supplied, please give a valid KF3 Friend ID.");
                    }
                    break;
                    /*
                case "kf3id":
                case "kf3name":
                case "namelookup":
                case "lookup":
                    if (parts.length > 1) {
                        KF3Summary.idLookup(parts[1],mc);
                    } else {
                        new Reply().noPing(mc, "No Friend ID or name supplied, please give a KF3 Friend ID or name.");
                    }
                    break;
                     */
                default:
                    //AI conversation start code
                    rmCommandBool = true;
                    String[] characters = parts[0].toLowerCase().replace(prefix,"").split(",");
                    boolean invalidCharacter = false;
                    boolean doConcat = false;
                    StringBuilder invalidCharacterName = new StringBuilder();
                    if (Character.inList(characters[0])) {
                        for (String individial : characters) {
                            if (!Character.inList(individial)) {           //if the name of the author field is not in the list of characters
                                invalidCharacter = true;
                                if (doConcat == true) {
                                    invalidCharacterName.append("``, ``");
                                }
                                invalidCharacterName.append(individial);
                                doConcat = true;
                            }
                        }
                        if (!invalidCharacter) {
                            mc.addReactionsToMessage("\uD83D\uDE80");
                            String finalM = m;
                            Thread aiThread = new Thread(() -> {
                                TactAI instance = new TactAI();
                                instance.aiRequest(finalM, mc, characters);
                                mc.removeOwnReactionByEmojiFromMessage("\uD83D\uDE80");
                            });
                            aiThread.start();
                            break;
                        } else {
                            new Reply().noPing(mc,"Invalid character - ``" + invalidCharacterName + "``.");
                        }
                    }
            }
        }

        //MirAI style chat start (formatted like "character name, @miatfren prompt content")
        if (mc.getMessage().getMentionedUsers().contains(self)) { //if it pings the bot
            if (m.indexOf("<@" + self.getIdAsString() + ">") > 0) {
                String[] halves = m.split("<@" + self.getIdAsString() + ">");
                String[] characters = halves[0].split(", ");
                boolean invalidCharacter = false;
                boolean doConcat = false;
                StringBuilder invalidCharacterName = new StringBuilder();
                rmCommandBool = true;
                for (String individial : characters) {
                    if (!Character.inList(individial)) {           //if the name of the author field is not in the list of characters
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

        if (!rmCommandBool) {
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
                                if (!Character.inList(individial)) {           //if the name of the author field is not in the list of characters
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
                                String finalM1 = m;
                                Thread aiThread = new Thread(() -> {
                                    TactAI instance = new TactAI();
                                    instance.aiRequest(finalM1, mc, characters);
                                    mc.removeOwnReactionByEmojiFromMessage("\uD83C\uDFDE️");
                                });
                                aiThread.start();
                            } else {
                                new Reply().noPing(mc,"Invalid character - ``" + invalidCharacterName +"``.");
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
        if (m.toLowerCase().contains("nigg") || m.toLowerCase().contains("n1gg") || m.toLowerCase().contains("kotlin user") || m.toLowerCase().contains("khafas") || m.toLowerCase().contains("kaffir")) {
            if (nBombCount > 5) {
                mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__\n-# Delete these messages with :x:");
                nBombCount = 0;
            } else {
                mc.getChannel().sendMessage("__**Racial slurs are discouraged!**__");
                nBombCount++;
            }
        }
    }
}
