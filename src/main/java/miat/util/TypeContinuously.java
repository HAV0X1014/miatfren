package miat.util;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TypeContinuously implements AutoCloseable {
    private final ScheduledExecutorService scheduler;

    public TypeContinuously(MessageChannelUnion channel) {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            channel.sendTyping().queue();
        };
        scheduler.scheduleAtFixedRate(task,0,5, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}
