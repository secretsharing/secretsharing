package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public abstract class AbstractCommand implements Command {
	protected String name;
	protected String description;

	protected abstract List<Option> requiredArguments();

	public AbstractCommand(String name, String description) {
		this.name = name;
		this.description = description;
	}

	protected String getHelpHeader() {
		return "";
	}
	protected String getHelpFooter() {
		return "";
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
		return new PosixParser().parse(getOptions(), args, true);
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
