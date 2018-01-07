package me.johnnywoof.discordlogger.bungee;

import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BungeeLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return MessageFormat.format(record.getMessage(), record.getParameters());
    }

}
