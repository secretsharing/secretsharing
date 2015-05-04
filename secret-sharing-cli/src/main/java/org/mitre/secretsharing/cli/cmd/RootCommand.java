package org.mitre.secretsharing.cli.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class RootCommand extends AbstractCommand {

	protected RootCommand() {
		super("[command]", "");
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();
		opt.addOption("h", "help", false, "show this help");
		return opt;
	}

	@Override
	public void perform(CommandLine cmd, BufferedReader in, PrintWriter out) throws Exception {
		String[] args = cmd.getArgs();
		String subcmd;
		if(cmd.hasOption("help") || args.length == 0)
			subcmd = "help";
		else {
			subcmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		
		Command c = Commands.forName(subcmd);
		if(c == null) {
			c = new HelpCommand();
			args = new String[0];
		}
		
		c.perform(c.parse(args), in, out);
	}
	
	@Override
	protected String getHelpFooter() {
		return "For help with a command: help <command>";
	}
	
	protected String getActualHelpFooter() {
		return "";
	}
	
	@Override
	public void showHelp(PrintWriter out) {
		super.showHelp(out);
	
		out.println();
		
		Options cmds = new Options();
		for(Command c : Commands.subCommands()) {
			cmds.addOption(null, c.getName(), false, c.getDescription());
		}
		
		HelpFormatter h = new HelpFormatter();
		h.setSyntaxPrefix("");
		h.setLongOptPrefix("");
		h.setOptionComparator(new Comparator<Option>() {
			@Override
			public int compare(Option o1, Option o2) {
				Integer p1 = Commands.names().indexOf(o1.getLongOpt());
				Integer p2 = Commands.names().indexOf(o2.getLongOpt());
				return p1.compareTo(p2);
			}
		});
		h.printHelp(out, 
				80, 
				"Valid Commands:", 
				"", 
				cmds, 
				12, 
				12, 
				getActualHelpFooter());
		out.flush();
	}
}
