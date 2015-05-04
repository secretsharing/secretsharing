package org.mitre.secretsharing.cli.cmd;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public abstract class AbstractCommand implements Command {
	protected String name;
	protected String description;
	
	protected String getHelpHeader() {
		return "";
	}
	protected String getHelpFooter() {
		return "";
	}
	
	public AbstractCommand(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	@Override
	public CommandLine parse(String... args) throws ParseException {
		return new PosixParser().parse(getOptions(), args, true);
	}
	
	@Override
	public void showHelp(PrintStream out) {
		HelpFormatter h = new HelpFormatter();
		h.printHelp(
				new PrintWriter(out, true), 
				80, 
				"java -jar sss.jar " + getName() + " <arguments>", 
				getHelpHeader(), 
				getOptions(), 
				12, 
				12, 
				getHelpFooter());
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
}
