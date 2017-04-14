package org.mitre.secretsharing;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class MSBOneTest {

	@Test
	public void testMSBOneMultibyte() {
		byte[] secret = new byte[] {(byte) 0x80};
		Part[] parts = Secrets.splitMultibyte(secret, 2, 2, new Random());
		try {
			byte[] reconstructed = Secrets.joinMultibyte(parts);
			Assert.assertArrayEquals(secret, reconstructed);
		} catch(ArrayIndexOutOfBoundsException e) {
			Assert.fail("github issue #4, msb one of multibyte secret");
		}
	}

	@Test
	public void testMSBOnePerByte() {
		byte[] secret = new byte[] {(byte) 0x80};
		PerBytePart[] parts = Secrets.splitPerByte(secret, 2, 2, new Random());
		try {
			byte[] reconstructed = Secrets.joinPerByte(parts);
			Assert.assertArrayEquals(secret, reconstructed);
		} catch(ArrayIndexOutOfBoundsException e) {
			Assert.fail("github issue #4, msb one of multibyte secret");
		}
	}

}
