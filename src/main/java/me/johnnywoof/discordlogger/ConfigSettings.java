package me.johnnywoof.discordlogger;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ConfigSettings {

    public final List<Level> levels;
    public final List<String> keywords;
    public final List<String> ignoredPrefixes;
    public final URL discordWebhookURL;
    public final String userAgent;
    public final String messagePrefix;
    public final boolean prefixLogLevels;

    public ConfigSettings(List<Level> levels, List<String> keywords, List<String> ignoredPrefixes, URL discordWebhookURL, String userAgent, String messagePrefix, boolean prefixLogLevels) {
        this.levels = Collections.unmodifiableList(levels);
        this.keywords = Collections.unmodifiableList(keywords);
        this.ignoredPrefixes = Collections.unmodifiableList(ignoredPrefixes);
        this.discordWebhookURL = discordWebhookURL;
        this.userAgent = userAgent;
        this.messagePrefix = messagePrefix;
        this.prefixLogLevels = prefixLogLevels;
    }
}
