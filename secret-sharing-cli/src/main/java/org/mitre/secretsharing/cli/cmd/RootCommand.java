/*

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	public void perform(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
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
		
		c.perform(c.parse(args), in, out, err);
	}
	
	@Override
	protected String getHelpFooter() {
		return "For help with a command: help <command>";
	}
	
	protected String getActualHelpFooter() {
		return "";
	}
	
	@Override
	public void showHelp(PrintStream out) {
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
		h.printHelp(
				new PrintWriter(out, true), 
				80, 
				"Valid Commands:", 
				"", 
				cmds, 
				12, 
				12, 
				getActualHelpFooter());
		out.flush();
	}

	@Override
	protected List<Option> requiredArguments() {
		return Collections.emptyList();
	}
}
