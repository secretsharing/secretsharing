package org.secretsharing.codec;

import java.nio.charset.Charset;

import org.junit.Test;

public class Base32Test {
	private static final Charset ASCII = Charset.forName("ASCII");
	
	@Test
	public void testEncode() throws Exception {
		byte[] enc = Base32.encode(null, "Hello World".getBytes(ASCII));
		System.out.println(new String(enc, ASCII));
		byte[] dec = Base32.decode(null, enc);
		System.out.println(new String(dec, ASCII));
	}
}
