package org.mitre.secretsharing.cli.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		for(int r = in.read(buf); r >= 0; r = in.read(buf))
			bytes.write(buf, 0, r);
		return bytes.toByteArray();
	}
	
	public static List<String> readLines(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "ASCII"));
		List<String> lines = new ArrayList<>();
		for(String line = br.readLine(); line != null; line = br.readLine())
			lines.add(line);
		return lines;
	}
}
