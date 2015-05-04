package org.mitre.secretsharing.cli.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class RootCommand extends AbstractCommand {

	protected RootCommand() {
		super(null);
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();
		opt.addOption("h", "help", false, "show help");
		return opt;
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
