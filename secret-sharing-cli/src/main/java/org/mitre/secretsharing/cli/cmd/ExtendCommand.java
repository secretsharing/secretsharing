package org.mitre.secretsharing.cli.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class ExtendCommand extends AbstractCommand {

	public ExtendCommand() {
		super("extend", "generate additional secret parts");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(CommandLine cmd, BufferedReader in, PrintWriter out) throws Exception {
		// TODO Auto-generated method stub

	}

}
