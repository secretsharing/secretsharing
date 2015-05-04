package org.mitre.secretsharing.cli.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class SplitCommand extends AbstractCommand {

	public SplitCommand() {
		super("split", "split a secret into secret parts");
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();
		
		opt.addOption("t", "total", true, "total number of parts to create");
		opt.addOption("r", "required", true, "number of required parts");
		opt.addOption("b", "base-64", false, "the secret is Base64 encoded");
		
		return opt;
	}

	@Override
	public void perform(CommandLine cmd, BufferedReader in, PrintWriter out) throws Exception {
		// TODO Auto-generated method stub

	}

}
