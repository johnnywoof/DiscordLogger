package me.johnnywoof.discordlogger.util;

import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.formatting.EmbedBuilder;
import me.johnnywoof.discordlogger.formatting.WebhookBuilder;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    private final DiscordLogger discordLogger;
    private WebhookBuilder builder = new WebhookBuilder();

    public LogHandler(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
        this.init();
    }

    private void init() {
        builder.setRedactIP(discordLogger.getSettings().removeIPAddresses);
        builder.setRedactURL(discordLogger.getSettings().removeURLs);
    }

    @Override
    public void publish(LogRecord record) {

        String logMessage = this.getFormatter() != null ? this.getFormatter().formatMessage(record) : record.getMessage();
        logMessage = logMessage.trim();

        boolean isIgnoredWord = Arrays.stream(logMessage.split(" ")).anyMatch(s -> this.discordLogger.getSettings().ignoredContent.contains(s));
        boolean isLevel = this.discordLogger.getSettings().levels.contains(record.getLevel());
        boolean isWatched = Arrays.stream(logMessage.split(" ")).anyMatch(s -> this.discordLogger.getSettings().keywords.contains(s));
        boolean isIgnoredPrefix = this.discordLogger.getSettings().ignoredPrefixes.stream().noneMatch(logMessage::startsWith);

        if ((isLevel || isWatched) && !isIgnoredPrefix && !isIgnoredWord) {

            for (Object[] objects : discordLogger.getEnvironment().logToEmbedList(logMessage)) {

                EmbedBuilder eBuilder = new EmbedBuilder();
                eBuilder.setColor(LogColor.getFrom(record.getLevel()).getLoggingColor());

                if (objects.length != 3)
                    continue;
                String header = objects[0].toString();
                String content = objects[1].toString();
                boolean inline = Boolean.parseBoolean(objects[2].toString());

                eBuilder.addField(header, content, inline);
                this.builder.addEmbed(eBuilder);
            }
        }
    }

    @Override
    public void flush() {

        this.discordLogger.postMessage(this.builder);
        this.builder.reset();
    }

    private boolean hasContentPending() {
        return this.builder != null && !this.builder.isEmpty();
    }

    @Override
    public void close() throws SecurityException {
        for (int i = 0; this.hasContentPending() && i < 3; i++)// Burst limit. Just ignore everything else I guess
            this.flush(); //Flush contents

    }

}
