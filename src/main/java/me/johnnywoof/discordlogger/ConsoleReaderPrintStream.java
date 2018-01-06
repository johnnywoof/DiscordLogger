package me.johnnywoof.discordlogger;

import java.io.PrintStream;

public class ConsoleReaderPrintStream extends PrintStream {

    private final DiscordLogger discordLogger;
    private final PrintStream stream;
    private StringBuilder currentLine = new StringBuilder();

    public ConsoleReaderPrintStream(DiscordLogger discordLogger, PrintStream out) {
        super(out, true);
        this.stream = out;
        this.discordLogger = discordLogger;
    }

    @Override
    public void write(int b) {
        super.write(b);
        this.currentLine.append((char) b);
    }

    @Override
    public void flush() {
        super.flush();

        String line = this.currentLine.toString();
        String[] words = line.split(" ");

        for (String word : words) {

            if (this.discordLogger.getKeywords().contains(word)) {

                this.discordLogger.postMessage(
                        this.discordLogger.getMessagePrefix() != null ? this.discordLogger.getMessagePrefix() + line : line
                );

                break;
            }

        }

        this.currentLine = new StringBuilder();
    }

    public PrintStream getRawStream() {
        return this.stream;
    }

}
