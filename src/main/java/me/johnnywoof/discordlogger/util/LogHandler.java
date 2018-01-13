package me.johnnywoof.discordlogger.util;

import me.johnnywoof.discordlogger.DiscordLogger;
import me.johnnywoof.discordlogger.formatting.EmbedBuilder;
import me.johnnywoof.discordlogger.formatting.WebhookBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
        this.builder = new WebhookBuilder();
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
        boolean isIgnoredPrefix = this.discordLogger.getSettings().ignoredPrefixes.stream().anyMatch(logMessage::startsWith);

        //System.out.println("Is Word Ignored: " + isIgnoredWord);
        //System.out.println("Is Level: " + isLevel);
        //System.out.println("Is Watched: " + isWatched);
        //System.out.println("Is Prefix Ignored: " + isIgnoredPrefix);
        //System.out.println("Value: " + ((isLevel || isWatched) && !isIgnoredPrefix && !isIgnoredWord));

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
