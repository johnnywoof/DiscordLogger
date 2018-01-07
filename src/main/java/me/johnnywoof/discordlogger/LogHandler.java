package me.johnnywoof.discordlogger;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    private final DiscordLogger discordLogger;
    private StringBuilder discordContent = new StringBuilder();

    public LogHandler(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
    }

    @Override
    public void publish(LogRecord record) {
        if (this.discordLogger.getLoggedLevels().contains(record.getLevel())) {

            String logMessage = this.getFormatter() != null ? this.getFormatter().formatMessage(record) : record.getMessage();

            if (this.discordLogger.getMessagePrefix() != null) {
                this.discordContent.append(this.discordLogger.getMessagePrefix()).append(logMessage).append("\n");
            } else {
                this.discordContent.append(logMessage).append("\n");
            }
        }
    }

    @Override
    public void flush() {
        String finalized = this.discordContent.length() >= 2
                ? this.discordContent.substring(0, this.discordContent.length() - 2)
                : this.discordContent.toString();

        this.discordLogger.postMessage(finalized);

        this.discordContent = new StringBuilder();
    }

    @Override
    public void close() throws SecurityException {

    }

}
