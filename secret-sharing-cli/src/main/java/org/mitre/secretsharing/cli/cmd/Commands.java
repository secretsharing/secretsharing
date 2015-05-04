package org.mitre.secretsharing.cli.cmd;

import java.util.ArrayList;
import java.util.List;

public abstract class Commands {

	private static final Command ROOT = new RootCommand();
	private static final Command[] SUB = new Command[] {
		new HelpCommand(),
		new SplitCommand(),
		new JoinCommand(),
		new ExtendCommand(),
	};

	public static Command rootCommand() {
		return ROOT;
	}

	public static Command[] subCommands() {
		return SUB;
	}

	public static List<String> names() {
		List<String> n = new ArrayList<>();
		for(Command c : subCommands())
			n.add(c.getName());
		return n;
	}

	public static Command forName(String name) {
		for(Command c : subCommands())
			if(name.equals(c.getName()))
				return c;
		return null;
	}

	private Commands() {}
}
