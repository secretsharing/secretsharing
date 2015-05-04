package org.mitre.secretsharing.cli.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class HelpCommand extends AbstractCommand {

	public HelpCommand() {
		super("help", "show help for a command");
	}

	@Override
	public Options getOptions() {
		return new Options();
	}

	@Override
	public void perform(CommandLine cmd, BufferedReader in, PrintWriter out) throws Exception {
		String[] args = cmd.getArgs();
		if(args.length > 1)
			args = new String[] { "help" };
		Command c = args.length == 0 ? Commands.rootCommand() : Commands.forName(args[0]);
		if(c == null)
			c = Commands.rootCommand();
		c.showHelp(out);
	}

}
