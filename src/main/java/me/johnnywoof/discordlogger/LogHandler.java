package me.johnnywoof.discordlogger;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    private final DiscordLogger discordLogger;
    private StringBuilder discordContent;

    public LogHandler(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
        this.init();
    }

    private void init() {
        this.discordContent = new StringBuilder();
        this.discordContent.append("```");
    }

    @Override
    public void publish(LogRecord record) {

        String logMessage = this.getFormatter() != null ? this.getFormatter().formatMessage(record) : record.getMessage();

        if ((this.discordLogger.getSettings().levels.contains(record.getLevel())
                || Arrays.stream(logMessage.split(" ")).anyMatch(s -> this.discordLogger.getSettings().keywords.contains(s)))
                && this.discordLogger.getSettings().ignoredPrefixes.stream().noneMatch(logMessage::startsWith)) {

            if (this.discordLogger.getSettings().prefixLogLevels)
                this.discordContent.append("[").append(record.getLevel()).append("] ");

            if (this.discordLogger.getSettings().messagePrefix != null)
                this.discordContent.append(this.discordLogger.getSettings().messagePrefix);

            this.discordContent.append(logMessage).append("\n");

        }
    }

    @Override
    public void flush() {

        if (this.discordContent.length() > 3) {

            StringBuilder message;

            if (this.discordContent.length() >= (DiscordLogger.MAX_DISCORD_CHARACTERS - 3)) {

                message = new StringBuilder();
                String[] lines = this.discordContent.toString().split("\n");
                int index = 0;

                while (message.length() < (DiscordLogger.MAX_DISCORD_CHARACTERS - 3)) {

                    int size = lines[index].length();

                    if ((message.length() + size) >= (DiscordLogger.MAX_DISCORD_CHARACTERS - 3))
                        break;

                    message.append(lines[index++]);

                }

                this.discordContent = this.discordContent.replace(0, message.length(), "");//Clear it out

            } else {
                message = this.discordContent;
                this.discordContent = new StringBuilder();
            }

            message.append("```");

            this.discordLogger.postMessage(message.toString());
            this.init();

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
