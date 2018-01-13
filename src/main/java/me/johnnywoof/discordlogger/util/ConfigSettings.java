package me.johnnywoof.discordlogger.util;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ConfigSettings {

    public final List<Level> levels;
    public final List<String> keywords;
    public final List<String> ignoredPrefixes;
    public final List<String> ignoredContent;
    public final URL discordWebhookURL;
    public final String userAgent;
    public final String messagePrefix;
    public final boolean prefixLogLevels;
    public final boolean removeURLs, removeIPAddresses;

    public ConfigSettings(List<Level> levels, List<String> keywords, List<String> ignoredPrefixes, List<String> ignoredContent, URL discordWebhookURL,
                          String userAgent, String messagePrefix, boolean prefixLogLevels, boolean removeURLs, boolean removeIPAddresses) {
        this.levels = Collections.unmodifiableList(levels);
        this.keywords = Collections.unmodifiableList(keywords.stream().map(String::trim).collect(Collectors.toList()));
        this.ignoredPrefixes = Collections.unmodifiableList(ignoredPrefixes.stream().map(String::trim).collect(Collectors.toList()));
        this.ignoredContent = Collections.unmodifiableList(ignoredContent.stream().map(String::trim).collect(Collectors.toList()));
        this.discordWebhookURL = discordWebhookURL;
        this.userAgent = userAgent;
        this.messagePrefix = messagePrefix;
        this.prefixLogLevels = prefixLogLevels;
        this.removeURLs = removeURLs;
        this.removeIPAddresses = removeIPAddresses;
    }

}
