package me.johnnywoof.discordlogger;

import java.util.List;
import java.util.logging.Level;

public class ConfigSettings {

    public final List<Level> levels;
    public final String discordWebhookURL;
    public final String userAgent;
    public final String messagePrefix;

    public ConfigSettings(List<Level> levels, String discordWebhookURL, String userAgent, String messagePrefix) {
        this.levels = levels;
        this.discordWebhookURL = discordWebhookURL;
        this.userAgent = userAgent;
        this.messagePrefix = messagePrefix;
    }
}
