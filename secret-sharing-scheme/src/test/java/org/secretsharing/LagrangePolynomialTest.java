package org.secretsharing;

import java.math.BigInteger;

import org.junit.Test;

public class LagrangePolynomialTest {
	@Test
	public void test() {
		BigPoint p1 = new BigPoint(BigInteger.ZERO, BigInteger.TEN);
		BigPoint p2 = new BigPoint(BigInteger.ONE, BigInteger.ONE);
		BigPoint p3 = new BigPoint(BigInteger.valueOf(2), BigInteger.ZERO);
		BigPoint p4 = new BigPoint(BigInteger.valueOf(3), BigInteger.ONE);
		LagrangePolynomial poly = new LagrangePolynomial(new BigPoint[] {p1,p2,p3,p4});
		System.out.println(poly);
	}
}
