package me.johnnywoof.discordlogger;

import java.util.logging.Level;

public interface NativeEnvironment {

    void log(Level level, String message);

    void saveDefaultConfig();

    ConfigSettings getDiscordLoggerConfig();

    /* Schedulers */
    void runAsync(Runnable runnable);

}
