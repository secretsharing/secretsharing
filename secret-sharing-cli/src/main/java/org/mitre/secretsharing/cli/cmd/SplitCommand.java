/*

Copyright 2015 The MITRE Corporation

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
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;
import org.mitre.secretsharing.cli.util.IOUtils;
import org.mitre.secretsharing.codec.PartFormats;

public class SplitCommand extends AbstractCommand {
	
	private static final Option TOTAL = new Option("t", "total", true, "total parts to create");
	private static final Option REQUIRED = new Option("r", "required", true, "required parts");
	private static final Option BASE64 = new Option(null, "base-64", false, "secret already Base64 encoded");
	static {
		REQUIRED.setArgName("parts");
		TOTAL.setArgName("parts");
	}

	public SplitCommand() {
		super("split", "splits a secret into secret parts");
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();
		
		opt.addOption(TOTAL);
		opt.addOption(REQUIRED);
		opt.addOption(BASE64);
		
		return opt;
	}
	
	@Override
	protected String checkArgument(CommandLine cmd, Option o) {
		String invalid = super.checkArgument(cmd, o);
		if(!invalid.isEmpty())
			return invalid;
		if(TOTAL == o) {
			try {
				int i =Integer.parseInt(cmd.getOptionValue(o.getLongOpt()));
				if(i <= 0)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + TOTAL.getLongOpt() +" must be provided a positive integer";
			}
		}
		if(REQUIRED == o) {
			try {
				int i =Integer.parseInt(cmd.getOptionValue(o.getLongOpt()));
				if(i <= 0)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + REQUIRED.getLongOpt() +" must be provided a positive integer";
			}
		}
		return invalid;
	}

	@Override
	public void perform(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
		if(!checkArguments(cmd, in, out, err))
			return;
		byte[] secret = IOUtils.toByteArray(in);
		if(cmd.hasOption(BASE64.getLongOpt())) {
			try {
				secret = Base64.getDecoder().decode(secret);
			} catch(RuntimeException e) {
				err.println("Not a Base64-encoded secret");
				System.exit(1);
			}
		}
		int totalParts = Integer.parseInt(cmd.getOptionValue(TOTAL.getLongOpt()));
		int requiredParts = Integer.parseInt(cmd.getOptionValue(REQUIRED.getLongOpt()));
		Random rnd = new SecureRandom();
		Part[] parts = Secrets.split(secret, totalParts, requiredParts, rnd);
		for(Part p : parts) {
			String s = PartFormats.currentStringFormat().format(p);
			out.println(s);
		}
	}

	@Override
	protected List<Option> requiredArguments() {
		return Arrays.asList(TOTAL, REQUIRED);
	}

}
