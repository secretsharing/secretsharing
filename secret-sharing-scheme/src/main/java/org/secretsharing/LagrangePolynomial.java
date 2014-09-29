package org.secretsharing;

import java.math.BigInteger;

public class LagrangePolynomial implements Polynomial {
	private TermPolynomial poly;
	
	public LagrangePolynomial(BigInteger[] px, BigInteger[] py) {
		if(px.length != py.length)
			throw new IllegalArgumentException();
		poly = TermPolynomial.ZERO;
		for(int j = 0; j < px.length; j++)
			poly = poly.add(l(px, py, j));
	}
	
	private TermPolynomial l(BigInteger[] px, BigInteger[] py, int j) {
		TermPolynomial result = TermPolynomial.ONE;
		for(int i = 0; i < px.length; i++) {
			if(i == j)
				continue;
			Term t1 = new Term(BigInteger.ONE, px[i].subtract(px[j]));
			Term t0 = new Term(px[i].negate(), px[i].subtract(px[j]));
			TermPolynomial t = new TermPolynomial(new Term[] {t1, t0});
			result = result.multiply(t);
		}
		return result.multiply(py[j]);
	}

	@Override
	public Term y(BigInteger x) {
		return poly.y(x);
	}
}
