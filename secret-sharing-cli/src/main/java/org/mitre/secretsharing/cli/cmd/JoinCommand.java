package org.mitre.secretsharing.cli.cmd;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;
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
			secret = Base64.encodeBase64(secret);
		out.write(secret);
	}

	@Override
	protected List<Option> requiredArguments() {
		return Collections.emptyList();
	}

}
