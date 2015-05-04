package org.mitre.secretsharing.cli.cmd;

public abstract class Commands {
	
	public static Command getRoot() {
		return new RootCommand();
	}

	public static Command[] getCommands() {
		return new Command[] {
				new HelpCommand(),
				new SplitCommand(),
				new JoinCommand(),
				
		};
	}
	
	private Commands() {}
}
