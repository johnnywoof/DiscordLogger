package me.johnnywoof.discordlogger.spigot;

import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.generic.NativeEnvironment;
import me.johnnywoof.discordlogger.util.ConfigSettings;
import me.johnnywoof.discordlogger.util.LogHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SpigotLoader extends JavaPlugin implements NativeEnvironment {

    private final DiscordLogger discordLogger = new DiscordLogger(this);
    private LogHandler logHandler = null;

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
    public void runAsyncTimer(Runnable runnable, long delay, TimeUnit timeUnit) {
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, 0, timeUnit.toSeconds(delay) * 20);//1 second = 20 ticks
    }

    @Override
    public void hookLogStreams() throws Exception {
        if (this.logHandler != null)
            throw new IllegalStateException("Already hooked");

        this.logHandler = new LogHandler(this.discordLogger);
        Bukkit.getLogger().addHandler(this.logHandler);
    }

    @Override
    public void unhookLogStreams() throws Exception {

        if (this.logHandler != null) {

            Bukkit.getLogger().removeHandler(this.logHandler);
            this.logHandler.close();
            this.logHandler = null;

        }

    }

    @Override
    public void flushLogHook() {
        if (this.logHandler != null)
            this.logHandler.flush();
    }

    @Override
    public ConfigSettings getDiscordLoggerConfig() {

        Configuration config = this.getConfig();

        try {
            return new ConfigSettings(
                    config.getStringList("log-levels").stream().map(Level::parse).collect(Collectors.toList()),
                    config.getStringList("log-keywords"),
                    config.getStringList("ignored-log-prefixes"),
                    config.getStringList("ignored-log-words"),
                    new URL(config.getString("discord-webhook-url")),
                    config.getString("http-user-agent"),
                    config.getString("message-prefix"),
                    config.getBoolean("prefix-log-level", true),
                    config.getBoolean("remove-urls", true),
                    config.getBoolean("remove-ips", true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
