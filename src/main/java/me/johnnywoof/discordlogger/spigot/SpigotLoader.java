package me.johnnywoof.discordlogger.spigot;

import me.johnnywoof.discordlogger.ConfigSettings;
import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.NativeEnvironment;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

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
    public ConfigSettings getDiscordLoggerConfig() {

        Configuration config = this.getConfig();

        return new ConfigSettings(
                config.getStringList("log-keywords"),
                config.getString("discord-webhook-url"),
                config.getString("http-user-agent"),
                config.getString("message-prefix")
        );

    }

}
