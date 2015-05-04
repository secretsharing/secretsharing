package org.mitre.secretsharing.cli.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public interface Command {

	public String getName();
	
	public String getDescription();
	
	public Options getOptions();
	
	public CommandLine parse(String[] args) throws ParseException;
	
	public void perform(CommandLine cmd, BufferedReader in, PrintWriter out) throws Exception;
	
	public void showHelp(PrintWriter out);

}