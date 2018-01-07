package me.johnnywoof.discordlogger;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public interface NativeEnvironment {

    void log(Level level, String message);

    void saveDefaultConfig();

    ConfigSettings getDiscordLoggerConfig();

    void runAsync(Runnable runnable);

    void runAsyncDelayed(Runnable runnable, long delay, TimeUnit timeUnit);

    void hookLogStreams() throws Exception;

    void unhookLogStreams() throws Exception;

}
