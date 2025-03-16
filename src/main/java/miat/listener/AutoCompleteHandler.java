package miat.listener;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoCompleteHandler extends ListenerAdapter {
    String[] googleLanguages = new String[]{"bulgarian","chinese_simplified","chinese_traditional","croatian","czech","danish",
    "dutch","english","finnish","french","german","greek","hindi","hungarian","indonesian","italian","japanese","korean",
    "lithuanian","norwegian","polish","portuguese","romanian","russian","spanish","swedish","thai","turkish","ukrainian",
    "vietnamese"}; //starting with the languages that the discord client supports first

    String[] deeplLanguages = new String[]{"bulgarian","chinese","czech","danish","dutch","english","finnish","french",
    "german","greek","hungarian","indonesian","italian","japanese","korean","lithuanian","norwegian","polish","portuguese",
    "romanian","russian","spanish","swedish","turkish","ukrainian"};

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().contains("googletranslate") && event.getFocusedOption().getName().equals("language")) {
            List<Command.Choice> options = Stream.of(googleLanguages)
                    .filter(language -> language.startsWith(event.getFocusedOption().getValue())).limit(25) // only display words that start with the user's current input
                    .map(language -> new Command.Choice(language, language)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }

        if (event.getName().contains("deepltranslate") && event.getFocusedOption().getName().equals("language")) {
            List<Command.Choice> options = Stream.of(deeplLanguages)
                    .filter(language -> language.startsWith(event.getFocusedOption().getValue())).limit(25) // only display words that start with the user's current input
                    .map(language -> new Command.Choice(language, language)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
