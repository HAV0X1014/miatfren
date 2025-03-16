# Miat Fren
A general purpose Discord bot with a focus on fun, created with JDA.

## Features
AI chatting (openAI/llama.cpp), translation features, advanced deleted message logging (saves attachments!), global bot rewards, and much more.

Many commands and features are installable to your account, so you can use them anywhere on Discord! You can even use it in DMs, servers that Miat isn't in, and on mobile.

### Translation
Miat Fren provides a few ways to translate messages.

1. Servers that Miat is in:

- If Miat is in the server, you can react to a message using a flag emoji to translate it to that country's main language. To use DeepL, react with :regional_indicator_d: first. To delete a translation message, react ❌ to the translated message. Server moderators: You can disable the flag translation in a specified channel with `/toggletranslatorchannel`.

- If Miat is in the server, you can send a message in another language by using `/googletranslate` or `/deepltranslate`, selecting the language you want to translate into, and then inputting the text you want to say.

2. Miat is added to your account:

- If Miat is added to your account, you can right-click a message you want to translate, go to "Apps", then choose "Translate - Google" or "Translate - DeepL". It will translate to the language your Discord client is using.

- If Miat is added to your account, you can send a you can send a message in another language by using `/googletranslate` or `/deepltranslate`, selecting the language you want to translate into, and then inputting the text you want to say. If the server does not allow external apps, the message will not be visible to others.

### AI features

- To initiate a chat, use `[` followed by a character's name with no spaces, or the character's name followed by a comma and a ping at the bot.

- - Examples: `[redcrownedcrane what is an airfoil?` or `bald eagle, @Miat Fren how far away is the moon`.
Reply to the last message sent by a character to continue a chat.

- You can modify your experience with `/customizeai` to add a line to the AI's system prompt.

- Use `/addcustomcharacter` to add or edit a character only for you.

## Running

Miat Fren uses Java 11 at minimum.

Compile Miat Fren by cloning/downloading the repo and running `gradlew shadowjar`. Place the compiled JAR in the same directory as the ServerFiles/ and CharacterImages/, complete the config in ServerFiles/config.json and run it with `java -jar miat5.0-5.x-all.jar`.
