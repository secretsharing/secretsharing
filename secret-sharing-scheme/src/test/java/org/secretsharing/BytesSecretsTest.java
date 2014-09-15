package org.secretsharing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BytesSecretsTest {
	
	@Parameters(name = "{0} {1} {2}")
	public static Iterable<Object[]> params() {
		Random random = new Random(0L);
		
		List<Object[]> params = new ArrayList<Object[]>();
		for(int i = 0; i < 1000; i++) {
			byte[] secret = new byte[random.nextInt(128)];
			random.nextBytes(secret);
			int requiredParts = 2 + random.nextInt(10);
			int totalParts = requiredParts + random.nextInt(10);
			params.add(new Object[] {secret, requiredParts, totalParts});
		}
		
		return params;
	}
	
	private byte[] secret;
	private int requiredParts;
	private int totalParts;
	
	public BytesSecretsTest(byte[] secret, int requiredParts, int totalParts) {
		this.secret = secret;
		this.requiredParts = requiredParts;
		this.totalParts = totalParts;
	}
	
	@Test
	public void testSecrets() {
		byte[][] parts = BytesSecrets.split(secret, totalParts, requiredParts);
		parts = Arrays.copyOf(parts, requiredParts);
		byte[] reconstructed = BytesSecrets.join(parts);
		Assert.assertArrayEquals(secret, reconstructed);
	}

}
