package miat;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.bush.translator.Translator;
import miat.filehandler.ConfigHandler;
import miat.listener.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class MiatMain {
    public static String configFile;
    public static String characterList;
    public static final int startTime = (int) (System.currentTimeMillis() / 1000);
    public static String self;
    public static String prefix;
    public static boolean debugmessagelog = false;
    public static JDA jda;
    public static boolean deeplEnabled;
    public static com.deepl.api.Translator deepLTranslator;
    public static Translator translator = new Translator();
    public static String[] reWordsGoodWordsExactMatch;
    public static String[] reWordsGoodWords;
    public static String[] reWordsBadWordsExactMatch;
    public static String[] reWordsBadWords;
    public static String deepLEmoji;
    public static Boolean useGoogleAsFallbackForDeepL;
    public static int nBombCount = 0;
    public static Cache<String, Message> messageCache;
    public static void main(String[] args) throws InterruptedException {
        ConfigHandler.refresh();
        deepLTranslator = new com.deepl.api.Translator(ConfigHandler.getString("DeepLKey"));

        jda = JDABuilder.createDefault(ConfigHandler.getString("Token"), EnumSet.allOf(GatewayIntent.class))
                .addEventListeners(new MessageHandler())
                .addEventListeners(new SlashCommandHandler())
                .addEventListeners(new MessageDeleteHandler())
                .addEventListeners(new ContextCommandHandler())
                .addEventListeners(new ReactionAddHandler())
                .addEventListeners(new ServerJoinHandler())
                .setActivity(Activity.customStatus(ConfigHandler.getString("StatusText")))
                .build();

        messageCache = Caffeine.newBuilder()
                .maximumSize(30000)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();

        jda.awaitReady();
        self = jda.getSelfUser().getId();
        System.out.println("Logged in as " + jda.getSelfUser().getName());
        if (ConfigHandler.getBoolean("RegisterCommands")) {
            RegisterCommands.register();
        }

        //further init, like populating caches
        for (int i = 0; i < jda.getTextChannels().size(); i++) {            //get every text channel
            if (jda.getTextChannels().get(i).getType().isGuild()) {         //check if its from a guild
                try {
                    jda.getTextChannels().get(i).getHistory().retrievePast(50).queue(messages -> {  //get the first 50 messages in the channel, add them to cache
                        for (Message message : messages) {
                            messageCache.put(message.getId(), message);
                        }
                    });
                } catch (Exception ignored) {}
                Thread.sleep(35);                                       //sleep so we dont hit the ratelimit
            }
        }
        System.out.println("Message caching and further setup completed.");
    }
}
