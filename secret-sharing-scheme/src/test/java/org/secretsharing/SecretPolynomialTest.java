package org.secretsharing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.secretsharing.util.BigIntegers;

@RunWith(Parameterized.class)
public class SecretPolynomialTest {
	private static final Random rnd = new Random(0L);
	
	@Parameters
	public static Iterable<Object[]> params() {
		List<Object[]> p = new ArrayList<Object[]>();
		for(int i = 0; i < 100; i++) {
			BigInteger secret = new BigInteger(8, rnd);
			int powx = 2 + (i % 7);
			p.add(new Object[] {secret, powx});
		}
		return p;
	}
	
	private BigInteger secret;
	private int powx;
	
	public SecretPolynomialTest(BigInteger secret, int powx) {
		this.secret = secret;
		this.powx = powx;
	}
	
	@Test
	public void testReconstructSecret() {
		TermPolynomial sp = new TermPolynomial(secret, secret.bitLength(), powx, rnd);
		BigPoint[] pts = sp.p(BigIntegers.range(1, powx+2));
		TermPolynomial lp = new TermPolynomial(pts, sp.getModulus());
		BigInteger s = lp.wholeY(BigInteger.ZERO);
		Assert.assertEquals(secret, s);
	}
}
