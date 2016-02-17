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

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;
import org.mitre.secretsharing.cli.util.IOUtils;
import org.mitre.secretsharing.codec.PartFormats;

public class SplitCommand extends AbstractCommand {
	
	private static final Option TOTAL = new Option("t", "total", true, "total parts to create");
	private static final Option REQUIRED = new Option("r", "required", true, "required parts");
	private static final Option BASE64 = new Option(null, "base-64", false, "secret already Base64 encoded");
	private static final Option FILE_PREFIX = new Option("p", "output-prefix", true, "prefix for storing secret parts as files");
	private static final Option FILE_SUFFIX = new Option("s", "output-suffix", true, "suffix for storing secret parts as files (requires prefix)");
	static {
		TOTAL.setArgName("parts");
		REQUIRED.setArgName("parts");
		FILE_PREFIX.setArgName("prefix");
		FILE_SUFFIX.setArgName("suffix");

		TOTAL.setArgs(1);
		REQUIRED.setArgs(1);
		FILE_PREFIX.setArgs(1);
		FILE_SUFFIX.setArgs(1);;
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
		opt.addOption(FILE_PREFIX);
		opt.addOption(FILE_SUFFIX);
		
		return opt;
	}
	
	@Override
	protected String checkArgument(CommandLine cmd, Option o) {
		String invalid = super.checkArgument(cmd, o);
		if(!invalid.isEmpty())
			return invalid;
		if(TOTAL == o) {
			try {
				if(cmd.getOptionValues(o.getLongOpt()).length > 1)
					throw new RuntimeException();
				int i =Integer.parseInt(cmd.getOptionValue(o.getLongOpt()));
				if(i <= 0)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + TOTAL.getLongOpt() +" must be provided a single positive integer";
			}
		}
		if(REQUIRED == o) {
			try {
				if(cmd.getOptionValues(o.getLongOpt()).length > 1)
					throw new RuntimeException();
				int i =Integer.parseInt(cmd.getOptionValue(o.getLongOpt()));
				if(i <= 0)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + REQUIRED.getLongOpt() +" must be provided a single positive integer";
			}
		}
		if(FILE_PREFIX == o)  {
			try {
				if(cmd.getOptionValues(o.getLongOpt()).length > 1)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + FILE_PREFIX.getLongOpt() +" must be provided a single path prefix";
			}
		}
		if(FILE_SUFFIX == o)  {
			try {
				if(cmd.getOptionValues(o.getLongOpt()).length > 1)
					throw new RuntimeException();
				if(cmd.getOptionValue(FILE_PREFIX.getLongOpt()) == null)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + TOTAL.getLongOpt() +" must be used with --" + FILE_PREFIX + " and must be provided a single path prefix";
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
				secret = Base64.decodeBase64(secret);
			} catch(RuntimeException e) {
				err.println("Not a Base64-encoded secret");
				System.exit(1);
			}
		}
		int totalParts = Integer.parseInt(cmd.getOptionValue(TOTAL.getLongOpt()));
		int requiredParts = Integer.parseInt(cmd.getOptionValue(REQUIRED.getLongOpt()));
		Random rnd = new SecureRandom();
		Part[] parts = Secrets.splitPerByte(secret, totalParts, requiredParts, rnd);
		String prefix = cmd.getOptionValue(FILE_PREFIX.getLongOpt());
		if(prefix == null) {
			for(Part p : parts) {
				String s = PartFormats.currentStringFormat().format(p);
				out.println(s);
			}
		} else {
			String suffix = cmd.getOptionValue(FILE_SUFFIX.getLongOpt());
			if(suffix == null)
				suffix = "";
			for(int i = 0; i < parts.length; i++) {
				File file = new File(prefix + i + suffix);
				PrintStream fout = new PrintStream(file, "UTF-8");
				try {
					fout.println(PartFormats.currentStringFormat().format(parts[i]));
				} finally {
					fout.close();
				}
			}
		}
	}

	@Override
	protected List<Option> requiredArguments() {
		return Arrays.asList(TOTAL, REQUIRED);
	}

}
