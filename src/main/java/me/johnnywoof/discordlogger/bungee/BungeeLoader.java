package me.johnnywoof.discordlogger.bungee;

import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.NativeEnvironment;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.logging.Level;

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
	public Path dataFolder() {
		return this.getDataFolder().toPath();
	}

	@Override
	public void runAsync(Runnable runnable) {
		this.getProxy().getScheduler().runAsync(this, runnable);
	}
}
