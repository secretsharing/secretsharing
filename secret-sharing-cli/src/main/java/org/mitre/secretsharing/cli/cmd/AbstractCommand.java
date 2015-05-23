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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.mitre.secretsharing.cli.SecretsCLI;

public abstract class AbstractCommand implements Command {
	protected String name;
	protected String description;

	protected abstract List<Option> requiredArguments();

	public AbstractCommand(String name, String description) {
		this.name = name;
		this.description = description;
	}

	protected String getHelpHeader() {
		return getDescription();
	}
	protected String getHelpFooter() {
		return "\nversion " + SecretsCLI.version();
	}
	
	protected String checkArgument(CommandLine cmd, Option o) {
		String invalid = "";
		if(!requiredArguments().contains(o))
			return invalid;
		if((o.getOpt() == null || !cmd.hasOption(o.getOpt().charAt(0))) && !cmd.hasOption(o.getLongOpt())) {
			if(o.getOpt() != null)
				invalid += "-" + o.getOpt() + ",";
			invalid += "--" + o.getLongOpt();
			if(o.hasArg())
				invalid += " <" + o.getArgName() + ">"; 
			invalid += " is missing.\n";
		}
		return invalid;
	}

	protected boolean checkArguments(CommandLine cmd, InputStream in, PrintStream out, PrintStream err) throws Exception {
		String invalid = "";
		List<Option> req = new ArrayList<>(requiredArguments());
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Option> all = new ArrayList(getOptions().getOptions());
		all.removeAll(req);
		req.addAll(all);
		
		for(Option o : req) {
			invalid += checkArgument(cmd, o);
		}
		
		if(invalid.isEmpty())
			return true;

		err.println("Invalid arguments:");
		err.println(invalid);
		Command h = new HelpCommand();
		h.perform(h.parse(getName()), in, err, err);
		return false;
	}

	@Override
	public CommandLine parse(String... args) throws ParseException {
		return new DefaultParser().parse(getOptions(), args, true);
	}

	@Override
	public void showHelp(PrintStream out) {
		HelpFormatter h = new HelpFormatter();
		h.printHelp(
				new PrintWriter(out, true), 
				80, 
				"java -jar sss.jar " + getName() + " <arguments>", 
				getHelpHeader(), 
				getOptions(), 
				12, 
				12, 
				getHelpFooter());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
