package me.johnnywoof.discordlogger.bungee;

import com.google.common.io.ByteStreams;
import me.johnnywoof.discordlogger.ConfigSettings;
import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.LogHandler;
import me.johnnywoof.discordlogger.NativeEnvironment;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BungeeLoader extends Plugin implements NativeEnvironment {

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
    public void saveDefaultConfig() {

        Path dataFolder = this.getDataFolder().toPath();

        try {

            if (Files.notExists(dataFolder))
                Files.createDirectory(dataFolder);

            Path configFile = dataFolder.resolve("config.yml");

            if (Files.notExists(configFile)) {

                InputStream in = this.getClass().getResourceAsStream("/config.yml");

                Files.write(configFile, ByteStreams.toByteArray(in));

                in.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ConfigSettings getDiscordLoggerConfig() {

        try {

            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.getDataFolder().toPath().resolve("config.yml").toFile());

            return new ConfigSettings(
                    config.getStringList("log-levels").stream().map(Level::parse).collect(Collectors.toList()),
                    config.getString("discord-webhook-url"),
                    config.getString("http-user-agent"),
                    config.getString("message-prefix")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.getProxy().getScheduler().runAsync(this, runnable);
    }

    @Override
    public void hookLogStreams() throws Exception {

        BungeeCord.getInstance().getLogger().addHandler(new LogHandler(discordLogger));

    }

    @Override
    public void unhookLogStreams() {

        Logger logger = BungeeCord.getInstance().getLogger();

        Arrays.stream(logger.getHandlers())
                .filter(handler -> handler instanceof LogHandler)
                .forEach(logger::removeHandler);

    }
}
