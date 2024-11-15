# miatfren
Miat Fren Discord bot, written in Java with Javacord.

A general purpose discord bot with a focus on fun!

If you would like to see miat in action, it is available in this server.
https://discord.gg/dHNund6jey

## Main Features

### openAI API Integration
Using a llama.cpp or other openAI compliant API, you can have AI chats! New characters can be added with `/addcharacter` by whitelisted members, and existing characters can be seen by anyone with `/getcharacter`. Start a conversation with one or more characters with the bot prefix (in my case, "[") and the character's name followed by a comma. For example, `[cleck how are you?` would ask the character named "cleck" how are you, and `[cloud,tarpan,southchinatiger lets go fishing!` would say lets go fishing to the three characters. 

### Translator
Translate messages with flag emojis! React to a message with the country's flag of the language you want, and it will translate the content into that language. To use DeepL, react with a :regional_indicator_d: emoji and then the flag you wish. Remove translation messages with an :x: emoji.

### Random Friend
Use randfr to get a random Friend from japari-library.com! Returns the Friend as a link.

### ReWords Points
ReWords rewards users for saying words and phrases correctly, or can punish them for certain phrases. For every 'good phrase' said correctly, you earn a point and 'bad phrases' lose a point. The phrases are configurable, and supports exact match checking (ex. if "HAV0X" is set as an exact match phrase, "hav0x" will not earn a point.)

## Features
- `animalfact`: Get a random Animal Fact from factretriever.com.
- `createqr`: Create a QR code with any string.
- `inspiro`: Get an "inspirational" image.
- `joke`: Get a random joke from JokeAPI.dev.
- V0Xpoints: Earn points for saying specific phrases correctly.
- `randfr`: Get a random friend from Japari-Library.com.
- `wiki`: Get a random article from Wikipedia
- `miat`: run it and find out.
- `[translate` : Translate a string from nearly any language into English.
- Flag 2 Translate : Add a flag emoji to translate the message into that language.
- `[bestclient`: Informs you about the [best client](https://seppuku.pw/).
- Debug whitelist members are able to execute some utility commands.
- `purge`: Purge the desired amount of messages.
- `delete`: Delete a message by message ID - works across channels, both messageID types work.
- `pfp`: Get the profile picture of a user.
- `serverinfo`: Get advanced info about a server the bot is in.
- Deleted Message Log: Logs deleted messages to the specified channel.
- `ban`: Ban the specified user.
- `kick`: Kick the specified user.

### Building
Use `gradlew shadowjar` to build.

