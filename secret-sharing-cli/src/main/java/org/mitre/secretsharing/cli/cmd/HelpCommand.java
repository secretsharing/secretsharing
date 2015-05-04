package org.mitre.secretsharing.cli.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class HelpCommand extends AbstractCommand {

	public HelpCommand() {
		super("help");
	}

	@Override
	public Options getOptions() {
		return new Options();
	}

	@Override
	public void perform(CommandLine cmd) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getHelpHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getHelpFooter() {
		// TODO Auto-generated method stub
		return null;
	}

}
