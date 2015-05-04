package org.mitre.secretsharing.cli.cmd;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public abstract class AbstractCommand implements Command {
	protected String name;
	
	protected abstract String getHelpHeader();
	protected abstract String getHelpFooter();
	
	public AbstractCommand(String name) {
		this.name = name;
	}
	
	@Override
	public CommandLine parse(String[] args) throws ParseException {
		return new PosixParser().parse(getOptions(), args, true);
	}
	
	@Override
	public void showHelp(PrintWriter out) {
		HelpFormatter h = new HelpFormatter();
		h.printHelp(out, 
				80, 
				"java -jar sss.jar " + getName(), 
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
}
