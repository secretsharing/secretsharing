package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
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
	public void perform(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected List<Option> requiredArguments() {
		// TODO Auto-generated method stub
		return null;
	}

}
