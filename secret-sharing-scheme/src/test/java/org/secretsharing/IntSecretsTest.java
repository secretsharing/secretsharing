package org.secretsharing;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
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
public class IntSecretsTest {
	
	@Parameters(name = "{0} {1} {2}")
	public static Iterable<Object[]> params() {
		Random random = new Random(0L);
		
		List<Object[]> params = new ArrayList<Object[]>();
		for(int i = 0; i < 1000; i++) {
			int requiredParts = 2 + random.nextInt(10);
			int totalParts = requiredParts + random.nextInt(10);
			params.add(new Object[] {random.nextInt(), requiredParts, totalParts});
		}
		
		return params;
	}
	
	private int secret;
	private int requiredParts;
	private int totalParts;
	
	public IntSecretsTest(int secret, int requiredParts, int totalParts) {
		this.secret = secret;
		this.requiredParts = requiredParts;
		this.totalParts = totalParts;
	}
	
	@Test
	public void testSecrets() {
		BigDecimal[][] parts = IntSecrets.split(secret, totalParts, requiredParts);
		parts = Arrays.copyOf(parts, requiredParts);
		int reconstructed = IntSecrets.join(parts);
		Assert.assertEquals(secret, reconstructed);
	}
}
