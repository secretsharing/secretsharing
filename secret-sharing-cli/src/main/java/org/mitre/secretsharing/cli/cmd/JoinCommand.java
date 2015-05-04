package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class JoinCommand extends AbstractCommand {

	public JoinCommand() {
		super("join", "join secret parts to reconstruct a secret");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(CommandLine cmd, InputStream in, PrintStream out) throws Exception {
		// TODO Auto-generated method stub

	}

}
