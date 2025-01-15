package miat.features;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class FakeWarn {
    public static EmbedBuilder warnWithUserAndReason(String userID, String userIDtoWarn, String reason) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("<a:warning:1282526602986655808> Warning! <a:warning:1282526602986655808>");
        e.setDescription("<@" + userID + "> has warned " + userIDtoWarn + " for ``" + reason + "``!!!\n" +
                "Mods will deal with this issue \"soon.\"\n||this warning is not real.||\nContinued violations may lead to another warning!");
        e.setFooter("Warning has been issued!");
        e.setColor(Color.RED);
        return e;
    }
    public static EmbedBuilder warnWithUser(String userID, String userIDtoWarn) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("<a:warning:1282526602986655808> Warning! <a:warning:1282526602986655808>");
        e.setDescription("<@" + userID + "> has warned " + userIDtoWarn + " for ``no reason at all``!!!\n" +
                "Mods will deal with this issue soon.\n||this warning is not real.||\nContinued violations may lead to another warning!");
        e.setFooter("Warning has been issued!");
        e.setColor(Color.RED);
        return e;
    }
    public static EmbedBuilder warnWithNothing(String userID) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("<a:warning:1282526602986655808> Warning! <a:warning:1282526602986655808>");
        e.setDescription("<@" + userID + "> has warned someone for something! Good luck finding out what they are mad about!\n" +
                "Mods will deal with this issue soon.\n||this warning is not real.||\nContinued violations may lead to another warning!");
        e.setFooter("Warning has been issued!");
        e.setColor(Color.RED);
        return e;
    }
    public static EmbedBuilder warnWithReason(String userID, String reason) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("<a:warning:1282526602986655808> Warning! <a:warning:1282526602986655808>");
        e.setDescription("<@" + userID + "> has warned someone for ``" + reason + "``! They probably imagined who they are warning!\n" +
                "\\> Mods will deal with this issue soon.\n||this warning is not real.||\nContinued violations may lead to another warning!");
        e.setFooter("Warning has been issued!");
        e.setColor(Color.RED);
        return e;
    }
}
