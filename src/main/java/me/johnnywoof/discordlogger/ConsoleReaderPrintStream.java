package me.johnnywoof.discordlogger;

import java.io.PrintStream;

public class ConsoleReaderPrintStream extends PrintStream {

	private final PrintStream stream;
	private StringBuilder currentLine = new StringBuilder();

	public ConsoleReaderPrintStream(PrintStream out) {
		super(out, true);
		this.stream = out;
	}

	@Override
	public void write(int b) {
		super.write(b);
		this.currentLine.append((char) b);
	}

	@Override
	public void flush() {
		super.flush();

		//TODO Parse text in currentLine

		this.currentLine = new StringBuilder();
	}

	public PrintStream getRawStream() {
		return this.stream;
	}

}
