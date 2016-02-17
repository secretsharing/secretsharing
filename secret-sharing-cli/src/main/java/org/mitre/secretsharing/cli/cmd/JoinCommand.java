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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.cli.util.IOUtils;
import org.mitre.secretsharing.codec.PartFormats;

public class JoinCommand extends AbstractCommand {
	private static final Option BASE64 = new Option(null, "base-64", false, "outupt secret Base64 encoded");
	private static final Option FILE_PREFIX = new Option("p", "output-prefix", true, "prefix for storing secret parts as files");
	private static final Option FILE_SUFFIX = new Option("s", "output-suffix", true, "suffix for storing secret parts as files (requires prefix)");

	static {
		FILE_PREFIX.setArgName("prefix");
		FILE_SUFFIX.setArgName("suffix");

		FILE_PREFIX.setArgs(1);
		FILE_SUFFIX.setArgs(1);;
	}

	public JoinCommand() {
		super("join", "joins secret parts to reconstruct a secret");
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();

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
		if(FILE_PREFIX == o)  {
			try {
				if(cmd.getOptionValue(FILE_PREFIX.getLongOpt()) == null)
					return invalid;
				if(cmd.getOptionValues(o.getLongOpt()).length > 1)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + FILE_PREFIX.getLongOpt() +" must be provided a single path prefix";
			}
		}
		if(FILE_SUFFIX == o)  {
			try {
				if(cmd.getOptionValue(FILE_SUFFIX.getLongOpt()) == null)
					return invalid;
				if(cmd.getOptionValues(o.getLongOpt()).length > 1)
					throw new RuntimeException();
			} catch(RuntimeException e) {
				invalid += "--" + FILE_SUFFIX.getLongOpt() + " must be provided a single path suffix";
			}
		}
		return invalid;
	}

	@Override
	public void perform(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
		List<Part> parts = new ArrayList<Part>();
		boolean failure = false;
		if(cmd.getOptionValue(FILE_PREFIX.getLongOpt()) == null && cmd.getOptionValue(FILE_SUFFIX.getLongOpt()) == null) {
			List<String> lines = IOUtils.readLines(in);
			for(String line : lines) {
				if(line.isEmpty())
					continue;
				try {
					parts.add(PartFormats.parse(line));
				} catch(RuntimeException e) {
					err.println("Not a secret part: " + line);
					failure = true;
				}
			}
		} else {
			String prefix = cmd.getOptionValue(FILE_PREFIX.getLongOpt());
			String suffix = cmd.getOptionValue(FILE_SUFFIX.getLongOpt());
			if(prefix == null)
				prefix = "./";
			if(suffix == null)
				suffix = "";
			File pfile = new File(prefix);
			if(pfile.isDirectory()) {
				Pattern p = Pattern.compile("\\d+" + Pattern.quote(suffix));
				for(File f : pfile.listFiles()) {
					if(p.matcher(f.getName()).matches()) {
						InputStream fin = new FileInputStream(f);
						try {
							try {
								for(String line : IOUtils.readLines(fin)) {
									if(line.isEmpty())
										continue;
									parts.add(PartFormats.parse(line));
								}
							} catch(RuntimeException e) {
								err.println("Not a secret part: " + f);
								failure = true;
							}
						} finally {
							fin.close();
						}
					}
				}
			} else {
				Pattern p = Pattern.compile(Pattern.quote(prefix) + "\\d+" + Pattern.quote(suffix));
				File parent = pfile.getParentFile();
				if(parent == null)
					parent = new File(".");
				for(File f : parent.listFiles()) {
					if(p.matcher(f.getName()).matches()) {
						InputStream fin = new FileInputStream(f);
						try {
							try {
								for(String line : IOUtils.readLines(fin)) {
									if(line.isEmpty())
										continue;
									parts.add(PartFormats.parse(line));
								}
							} catch(RuntimeException e) {
								err.println("Not a secret part: " + f);
								failure = true;
							}
						} finally {
							fin.close();
						}
					}
				}
			}
		}
		if(failure)
			System.exit(1);
		byte[] secret;
		try {
			Part[] p = parts.toArray(new Part[0]);
			secret = p[0].join(Arrays.copyOfRange(p, 1, p.length));
		} catch(RuntimeException e) {
			err.println("Invalid secret part combination: " + e.getMessage());
			System.exit(1);
			return;
		}
		if(cmd.hasOption(BASE64.getLongOpt()))
			secret = Base64.encodeBase64(secret);
		out.write(secret);
	}

	@Override
	protected List<Option> requiredArguments() {
		return Collections.emptyList();
	}

}
