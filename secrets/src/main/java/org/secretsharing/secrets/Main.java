package org.secretsharing.secrets;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.secretsharing.BytesSecrets;
import org.secretsharing.codec.Base32;

public class Main {
	private static final Options OPT = new Options();
	static {
		OPT.addOption("s", false, "split a secret");
		OPT.addOption("j", false, "join parts of a secret");
		OPT.addOption("t", true, "total parts to generate (when splitting)");
		OPT.addOption("r", true, "required parts to reconstruct (when splitting)");
	}
	
	public static void main(String[] args) throws Exception {
		CommandLine cli = new PosixParser().parse(OPT, args);
		if(!cli.hasOption('s') && !cli.hasOption('j') || cli.hasOption('s') && cli.hasOption('j'))
			throw new RuntimeException("Must specify either -s or -j");
		if(cli.hasOption('s')) {
			if(!cli.hasOption('t') || !cli.hasOption('r'))
				throw new RuntimeException("Must specify -t and -j when splitting");
			int totalParts = Integer.parseInt(cli.getOptionValue('t'));
			int requiredParts = Integer.parseInt(cli.getOptionValue('r'));
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			for(int r = System.in.read(b); r > 0; r = System.in.read(b))
				buf.write(b, 0, r);
			byte[][] parts = BytesSecrets.split(buf.toByteArray(), totalParts, requiredParts);
			for(byte[] part : parts) {
				System.out.write(part);
				System.out.println();
			}
			return;
		}
		if(cli.hasOption('j')) {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			List<byte[]> parts = new ArrayList<byte[]>();
			for(int c = System.in.read(); c != -1; c = System.in.read()) {
				if(c == '\n') {
					parts.add(buf.toByteArray());
					buf.reset();
				} else
					buf.write(c);
			}
			byte[] secret = BytesSecrets.join(parts.toArray(new byte[0][]));
			System.out.write(secret);
			return;
		}
	}
}
