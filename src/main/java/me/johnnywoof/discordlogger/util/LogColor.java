package me.johnnywoof.discordlogger.util;

import java.awt.*;
import java.util.Arrays;
import java.util.logging.Level;

public enum LogColor {
    UNKNOWN(Level.ALL, Color.CYAN),
    FINEST(Level.FINEST, Color.WHITE),
    FINER(Level.FINER, Color.LIGHT_GRAY),
    FINE(Level.FINE, Color.DARK_GRAY),
    CONFIG(Level.CONFIG, Color.GREEN),
    INFO(Level.INFO, Color.YELLOW),
    WARNING(Level.WARNING, Color.ORANGE),
    SEVERE(Level.SEVERE, Color.RED);

    private Level loggingLevel;
    private Color loggingColor;

    LogColor(Level loggingLevel, Color loggingColor) {
        this.loggingLevel = loggingLevel;
        this.loggingColor = loggingColor;
    }

    public static LogColor getFrom(Level level) {
        return Arrays.stream(values()).filter(val -> val.getLoggingLevel().equals(level)).findFirst().orElse(UNKNOWN);
    }

    public Level getLoggingLevel() {
        return loggingLevel;
    }

    public Color getLoggingColor() {
        return loggingColor;
    }
}
