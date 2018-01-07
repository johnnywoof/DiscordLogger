package me.johnnywoof.discordlogger.spigot;

import me.johnnywoof.discordlogger.ConfigSettings;
import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.LogHandler;
import me.johnnywoof.discordlogger.NativeEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SpigotLoader extends JavaPlugin implements NativeEnvironment {

    private final DiscordLogger discordLogger = new DiscordLogger(this);

    @Override
    public void onEnable() {
        this.discordLogger.onEnable();
    }

    @Override
    public void onDisable() {
        this.discordLogger.onDisable();
    }

    @Override
    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void hookLogStreams() throws Exception {
        Bukkit.getLogger().addHandler(new LogHandler(this.discordLogger));
    }

    @Override
    public void unhookLogStreams() throws Exception {

        Logger logger = Bukkit.getLogger();

        Arrays.stream(logger.getHandlers())
                .filter(handler -> handler instanceof LogHandler)
                .forEach(logger::removeHandler);

    }

    @Override
    public ConfigSettings getDiscordLoggerConfig() {

        Configuration config = this.getConfig();

        return new ConfigSettings(
                config.getStringList("log-levels").stream().map(Level::parse).collect(Collectors.toList()),
                config.getString("discord-webhook-url"),
                config.getString("http-user-agent"),
                config.getString("message-prefix")
        );

    }

}
