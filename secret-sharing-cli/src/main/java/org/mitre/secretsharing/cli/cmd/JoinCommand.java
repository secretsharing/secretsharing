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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;
import org.mitre.secretsharing.cli.util.IOUtils;
import org.mitre.secretsharing.codec.PartFormats;

public class JoinCommand extends AbstractCommand {
	private static final Option BASE64 = new Option(null, "base-64", false, "outupt secret Base64 encoded");

	public JoinCommand() {
		super("join", "joins secret parts to reconstruct a secret");
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();
		
		opt.addOption(BASE64);
		
		return opt;
	}

	@Override
	public void perform(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
		List<String> lines = IOUtils.readLines(in);
		List<Part> parts = new ArrayList<>();
		boolean failure = false;
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
		if(failure)
			System.exit(1);
		byte[] secret;
		try {
			secret = Secrets.join(parts.toArray(new Part[0]));
		} catch(RuntimeException e) {
			err.println("Invalid secret part combination: " + e.getMessage());
			System.exit(1);
			return;
		}
		if(cmd.hasOption(BASE64.getLongOpt()))
			secret = Base64.getEncoder().encode(secret);
		out.write(secret);
	}

	@Override
	protected List<Option> requiredArguments() {
		return Collections.emptyList();
	}

}
