package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class SplitCommand extends AbstractCommand {

	public SplitCommand() {
		super("split", "split a secret into secret parts");
	}

	@Override
	public Options getOptions() {
		Options opt = new Options();
		
		opt.addOption("t", "total", true, "total number of parts to create (required)");
		opt.addOption("r", "required", true, "number of required parts (required)");
		opt.addOption("b", "base-64", false, "the secret is Base64 encoded");
		
		return opt;
	}

	@Override
	public void perform(CommandLine cmd, InputStream in, PrintStream out) throws Exception {
		String invalid = "";
		if(!cmd.hasOption("total"))
			invalid += "--total <parts> is required\n";
		if(!cmd.hasOption("required"))	
			invalid += "--required <parts> is required\n";
		if(!invalid.isEmpty()) {
			out.println("Missing required arguments:");
			out.println(invalid);
			Command h = new HelpCommand();
			h.perform(h.parse(getName()), in, out);
			return;
		}
		
		// TODO Auto-generated method stub

		
		
	}

}
