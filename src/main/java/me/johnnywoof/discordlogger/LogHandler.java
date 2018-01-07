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

        if (this.discordContent.length() >= 2) {

            StringBuilder message;

            if (this.discordContent.length() >= 2000) {

                message = new StringBuilder();
                String[] lines = this.discordContent.toString().split("\n");
                int index = 0;

                while (message.length() < 2000) {

                    int size = lines[index].length();

                    if ((message.length() + size) >= 2000)
                        break;

                    message.append(lines[index++]);

                }

                this.discordContent = this.discordContent.replace(0, message.length(), "");//Clear it out

            } else {
                message = this.discordContent;
                this.discordContent = new StringBuilder();
            }

            this.discordLogger.postMessage(message.toString());
            this.discordContent = new StringBuilder();

        }

    }

    private boolean hasContentPending() {
        return this.discordContent != null && this.discordContent.length() > 0;
    }

    @Override
    public void close() throws SecurityException {
        for (int i = 0; this.hasContentPending() && i < 3; i++)// Burst limit. Just ignore everything else I guess
            this.flush(); //Flush contents

        this.discordContent = null; //Release memory
    }

}
