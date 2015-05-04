package org.mitre.secretsharing.cli.cmd;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public interface Command {

	public Options getOptions();
	
	public void perform(CommandLine cmd) throws Exception;

	public CommandLine parse(String[] args) throws ParseException;

	public String getName();
	
	public void showHelp(PrintWriter out);

}