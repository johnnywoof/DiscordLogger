package me.johnnywoof.discordlogger;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    private final DiscordLogger discordLogger;

    public LogHandler(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
    }

    @Override
    public void publish(LogRecord record) {
        if (this.discordLogger.getLoggedLevels().contains(record.getLevel())) {

            String logMessage = this.getFormatter().formatMessage(record);

            if (this.discordLogger.getMessagePrefix() != null) {
                this.discordLogger.postMessage(this.discordLogger.getMessagePrefix() + logMessage);
            } else {
                this.discordLogger.postMessage(logMessage);
            }
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}
