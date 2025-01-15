package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Random;

public class EightBall {
    public static EmbedBuilder eightBall(String content) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("8 Ball says...");
        String fortune = null;
        int random = new Random().nextInt(10);

        if (content.toLowerCase().contains("should ") || content.toLowerCase().contains("shall ")) {
            switch (random) {
                case 0:
                    fortune = "Consider it.";
                    break;
                case 1:
                    fortune = "Sleep on it, and decide tomorrow.";
                    break;
                case 2:
                    fortune = "Sleep on it, let your dreams decide";
                    break;
                case 3:
                    fortune = "Yes, if the day is right.";
                    break;
                case 4:
                    fortune = "If you have to ask, the answer is no.";
                    break;
                case 5:
                    fortune = "You only live once.";
                    break;
                case 6:
                    fortune = "Absolutely not.";
                    break;
                case 7:
                    fortune = "Short story - no. Long story - NOOOOOOOOOOOOO.";
                    break;
                case 8:
                    fortune = "Proceeding would not be advisable.";
                    break;
                case 9:
                    fortune = "I wouldn't, if I were you.";
                    break;
            }
        }
        if (content.toLowerCase().contains("will ") || content.toLowerCase().contains("is ")) {
            switch (random) {
                case 0:
                    fortune = "Certainly.";
                    break;
                case 1:
                    fortune = "Definitely. Count on it with all of your arms.";
                    break;
                case 2:
                    fortune = "More than likely.";
                    break;
                case 3:
                    fortune = "If the sun rises, it shall happen.";
                    break;
                case 4:
                    fortune = "Only if you make it happen.";
                    break;
                case 5:
                    fortune = "Even if you try, no.";
                    break;
                case 6:
                    fortune = "Give up on it now, it will make it easier.";
                    break;
                case 7:
                    fortune = "Absolutely not.";
                    break;
                case 8:
                    fortune = "Ask someone who is more familiar with this.";
                    break;
                case 9:
                    fortune = "It might.";
                    break;
            }
        }
        if (content.toLowerCase().contains("can ") || content.toLowerCase().contains("could ") || content.toLowerCase().contains("may ")) {
            switch (random) {
                case 0:
                    fortune = "It will probably happen for you.";
                    break;
                case 1:
                    fortune = "Only you can decide it.";
                    break;
                case 2:
                    fortune = "You can if you're crazy.";
                    break;
                case 3:
                    fortune = "Your physical stature makes me think... yes!";
                    break;
                case 4:
                    fortune = "Physically, yes. Mentally, no.";
                    break;
                case 5:
                    fortune = "Short story - no. Long story - NOOOOOOOOOOOOO.";
                    break;
                case 6:
                    fortune = "Commit to the bit and don't throw a fit";
                    break;
                case 7:
                    fortune = "No.";
                    break;
                case 8:
                    fortune = "Hopefully the world ends, cos that's not coming to fruition.";
                    break;
                case 9:
                    fortune = ":(";
                    break;
            }
        }
        if (content.toLowerCase().contains("when ") || content.toLowerCase().contains("might ")) {
            switch (random) {
                case 0:
                    fortune = "Immediately.";
                    break;
                case 1:
                    fortune = "In a little bit";
                    break;
                case 2:
                    fortune = "The next time it rains.";
                    break;
                case 3:
                    fortune = "When it comes.";
                    break;
                case 4:
                    fortune = "Never";
                    break;
                case 5:
                    fortune = "Yesterday.";
                    break;
                case 6:
                    fortune = "Tomorrow.";
                    break;
                case 7:
                    fortune = "In a fortnight.";
                    break;
                case 8:
                    fortune = "1 Month.";
                    break;
                case 9:
                    fortune = "100 Years.";
                    break;
            }
        }
        if (content.toLowerCase().contains("am i ")) {
            switch (random) {
                case 0:
                    fortune = "Yes.";
                    break;
                case 1:
                    fortune = "Yeah, me too!";
                    break;
                case 2:
                    fortune = "Narcissist... But yes.";
                    break;
                case 3:
                    fortune = "No.";
                    break;
                case 4:
                    fortune = "Knowing you... No.";
                    break;
                case 5:
                    fortune = "If you're asking, the answer is no.";
                    break;
                case 6:
                    fortune = "Depends. Do you have the will to?";
                    break;
                case 7:
                    fortune = "I'm sure you can answer this yourself.";
                    break;
                case 8:
                    fortune = "Ask yourself.";
                    break;
                case 9:
                    fortune = "It's great you want to know!";
                    break;
            }
        }
        if (content.toLowerCase().contains("do ") || content.toLowerCase().contains("does ") || content.toLowerCase().contains("did ")) {
            switch (random) {
                case 0:
                    fortune = "Sources say yes.";
                    break;
                case 1:
                    fortune = "Sources say POTATO";
                    break;
                case 2:
                    fortune = "Ask again.";
                    break;
                case 3:
                    fortune = "BEAVER";
                    break;
                case 4:
                    fortune = "Yes, I think the chances are high.";
                    break;
                case 5:
                    fortune = "It should fall into place.";
                    break;
                case 6:
                    fortune = "Make it happen.";
                    break;
                case 7:
                    fortune = "Potentially.";
                    break;
                case 8:
                    fortune = "Uh oh.";
                    break;
                case 9:
                    fortune = "Ask someone else.";
                    break;
            }
        }
        if (fortune == null) {
            fortune = "Consider asking a **_question_**\n-# The question to the 8 ball needs to contain a keyword for context!";
        }
        e.setDescription(fortune);
        e.setThumbnail("https://cdn.discordapp.com/attachments/1100888255483875428/1307494990418677832/8ball.png");
        e.setColor(8592808);
        return e;
    }
}
