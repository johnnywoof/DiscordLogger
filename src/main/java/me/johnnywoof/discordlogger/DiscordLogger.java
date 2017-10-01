package me.johnnywoof.discordlogger;

import java.util.logging.Level;

public class DiscordLogger {

	private final NativeEnvironment nativeEnvironment;

	public DiscordLogger(NativeEnvironment nativeEnvironment) {
		this.nativeEnvironment = nativeEnvironment;
	}

	public void onEnable() {

		System.setOut(new ConsoleReaderPrintStream(System.out));

	}

	public void onDisable() {

		this.nativeEnvironment.log(Level.INFO, "Disabling...");

		if (System.out instanceof ConsoleReaderPrintStream)
			System.setOut(((ConsoleReaderPrintStream) System.out).getRawStream());

	}

}
