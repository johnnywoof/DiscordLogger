package me.johnnywoof.discordlogger;

import java.nio.file.Path;
import java.util.logging.Level;

public interface NativeEnvironment {

	void log(Level level, String message);

	Path dataFolder();

	/* Schedulers */
	void runAsync(Runnable runnable);

}
