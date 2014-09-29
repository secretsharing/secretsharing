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
		SecretPolynomial sp = new SecretPolynomial(secret, secret.bitLength(), powx, rnd);
		BigPoint[] pts = sp.p(powx+1);
		LagrangePolynomial lp = new LagrangePolynomial(pts, sp.getPrime());
		// sanity check
		for(BigPoint p : pts)
			Assert.assertEquals(p.getY(), lp.y(p.getX()).getNumerator().mod(sp.getPrime()));
		Term t = lp.y(BigInteger.ZERO);
		Assert.assertEquals(BigInteger.ONE, t.getDenominator());
		BigInteger s = t.getNumerator().mod(sp.getPrime()).add(sp.getPrime()).mod(sp.getPrime());
		Assert.assertEquals(secret, s);
	}
}
