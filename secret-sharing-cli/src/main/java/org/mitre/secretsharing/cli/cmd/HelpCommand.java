package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class HelpCommand extends AbstractCommand {

	public HelpCommand() {
		super("help", "shows help for a command");
	}

	@Override
	public Options getOptions() {
		return new Options();
	}

	@Override
	public void perform(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
		String[] args = cmd.getArgs();
		if(args.length > 1)
			args = new String[] { "help" };
		Command c = args.length == 0 ? Commands.rootCommand() : Commands.forName(args[0]);
		if(c == null)
			c = Commands.rootCommand();
		c.showHelp(out);
	}

	@Override
	protected List<Option> requiredArguments() {
		return Collections.emptyList();
	}

}
