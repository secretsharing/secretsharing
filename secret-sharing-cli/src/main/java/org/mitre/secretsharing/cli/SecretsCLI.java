package org.mitre.secretsharing.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.mitre.secretsharing.cli.cmd.Command;
import org.mitre.secretsharing.cli.cmd.Commands;

public class SecretsCLI {
	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
		Command root = Commands.rootCommand();
		root.perform(root.parse(args), in, out);
	}
}
