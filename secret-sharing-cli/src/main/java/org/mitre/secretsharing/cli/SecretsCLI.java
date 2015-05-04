package org.mitre.secretsharing.cli;

import org.mitre.secretsharing.cli.cmd.Command;
import org.mitre.secretsharing.cli.cmd.Commands;

public class SecretsCLI {
	public static void main(String[] args) throws Exception {
		Command root = Commands.rootCommand();
		root.perform(root.parse(args), System.in, System.out, System.err);
	}
}
