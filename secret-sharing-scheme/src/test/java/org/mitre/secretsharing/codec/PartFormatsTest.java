package org.mitre.secretsharing.codec;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;

public class PartFormatsTest {
	@Test
	public void testLongString() {
		byte[] secret = new byte[1024*1024];
		Arrays.fill(secret, (byte) 'A');
		System.out.println("splitting");
		Part part = Secrets.splitPerByte(secret, 1, 1, new Random())[0];
		System.out.println("formatting");
		String formatted = PartFormats.currentStringFormat().format(part);
		System.out.println("- length=" + formatted.length());
		System.out.println("parsing");
		PartFormats.parse(formatted);
	}
}
