package miat;

import me.bush.translator.Translator;
import miat.filehandler.ConfigHandler;
import miat.listener.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.user.User;

public class MiatInit {
    public static DiscordApi api;
    public static String configFile;
    public static String characterList;
    public static final int startTime = (int) (System.currentTimeMillis() / 1000);
    public static User self;
    public static String prefix;
    public static boolean debugmessagelog = false;
    public static AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();
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

    public void init() {
        //all assigning of static variables related to the config are in this class so it can be called later.
        ConfigHandler.refresh();
        //deepl is the exception
        String deepLKey = ConfigHandler.getString("DeepLKey");
        deepLTranslator = new com.deepl.api.Translator(deepLKey);

        System.out.println("Logging in...");
        api = new DiscordApiBuilder().setToken(ConfigHandler.getString("Token")).setAllIntents().login().join();
        self = api.getYourself();
        System.out.println(self.getName() + " logged in.");
        api.updateActivity(ConfigHandler.getString("StatusText"));

        api.addMessageCreateListener(new MessageHandler());
        api.addSlashCommandCreateListener(new SlashCommandHandler());
        api.addMessageContextMenuCommandListener(new ContextCommandHandler());
        api.addMessageDeleteListener(new MessageDeleteHandler());
        api.addReactionAddListener(new ReactionAddHandler());
        api.addServerJoinListener(new ServerJoinHandler());
        if (ConfigHandler.getBoolean("RegisterCommands")) {
            RegisterCommands.registerCommands();
        }
    }
}
