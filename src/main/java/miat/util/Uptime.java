package miat.util;

import java.time.Duration;

import static miat.MiatInit.startTime;

public class Uptime {
    public static String uptime(){
        int systemTime = (int) (System.currentTimeMillis() / 1000);
        long upTimeSeconds = systemTime - startTime;
        Duration duration = Duration.ofSeconds(upTimeSeconds);
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        String upTime = String.format("``%04d`` hours, ``%02d`` minutes", hours, minutes);
        return upTime;
    }
}
