package me.johnnywoof.discordlogger.util;

import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.formatting.EmbedBuilder;
import me.johnnywoof.discordlogger.formatting.WebhookBuilder;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    private static final DateFormat FOOTER_FORMAT = new SimpleDateFormat("E, MMMM d, y | k:m:s a");

    private final DiscordLogger discordLogger;
    private WebhookBuilder builder = new WebhookBuilder();

    public LogHandler(DiscordLogger discordLogger) {
        this.discordLogger = discordLogger;
        this.init();
    }

    private void init() {
        this.builder = new WebhookBuilder(discordLogger.getSettings().removeURLs, discordLogger.getSettings().removeIPAddresses);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publish(LogRecord record) {

        String logMessage = this.getFormatter() != null ? this.getFormatter().formatMessage(record) : record.getMessage();
        logMessage = logMessage.trim();
        String removedCharStr = logMessage.replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\\[", "").replaceAll("\\]", "");

        boolean isIgnoredContent = this.discordLogger.getSettings().ignoredContent.stream().anyMatch(logMessage::contains) ||
                this.discordLogger.getSettings().ignoredContent.stream().anyMatch(removedCharStr::contains);

        boolean isLevel = this.discordLogger.getSettings().levels.contains(record.getLevel());
        boolean isWatched = Arrays.stream(logMessage.split(" ")).anyMatch(s -> this.discordLogger.getSettings().keywords.contains(s));
        boolean isIgnoredPrefix = this.discordLogger.getSettings().ignoredPrefixes.stream().anyMatch(logMessage::startsWith);

        if ((isLevel || isWatched) && !isIgnoredPrefix && !isIgnoredContent) {

            for (Object[] objects : discordLogger.getEnvironment().logToEmbedList(this.builder.redact(logMessage))) {

                EmbedBuilder eBuilder = new EmbedBuilder();
                Color color = LogColor.getFrom(record.getLevel()).getLoggingColor();

                if (objects.length < 2)
                    continue;
                Map<String, String> entryMap = new HashMap<>();
                if (objects[0] instanceof Map)
                    ((Map) objects[0]).forEach((key, value) -> entryMap.put(key.toString(), value.toString()));
                boolean inline = Boolean.parseBoolean(objects[1].toString());
                if (objects.length > 3) {
                    if (objects[2] instanceof Color)
                        color = (Color) objects[2];
                }

                eBuilder.setColor(color);
                entryMap.forEach((key, val) -> eBuilder.addField(key, val, inline));
                eBuilder.setFooter(FOOTER_FORMAT.format(new Date()));

                this.builder.addEmbed(eBuilder);
            }
        }
    }

    @Override
    public void flush() {

        if (hasContentPending()) {
            this.discordLogger.postMessage(this.builder);
            this.init();
        }
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
