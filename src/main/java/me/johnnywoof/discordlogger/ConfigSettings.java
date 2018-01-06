package me.johnnywoof.discordlogger;

import java.util.List;

public class ConfigSettings {

    public final List<String> keywords;
    public final String discordWebhookURL;
    public final String userAgent;
    public final String messagePrefix;

    public ConfigSettings(List<String> keywords, String discordWebhookURL, String userAgent, String messagePrefix) {
        this.keywords = keywords;
        this.discordWebhookURL = discordWebhookURL;
        this.userAgent = userAgent;
        this.messagePrefix = messagePrefix;
    }
}
