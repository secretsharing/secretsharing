package org.secretsharing;

import java.math.BigInteger;

public class LagrangePolynomial implements Polynomial {
	private TermPolynomial poly;
	
	public LagrangePolynomial(BigPoint[] pts) {
		BigInteger[] px = new BigInteger[pts.length];
		BigInteger[] py = new BigInteger[pts.length];
		for(int i = 0; i < pts.length; i++) {
			px[i] = pts[i].getX();
			py[i] = pts[i].getY();
		}
		poly = TermPolynomial.ZERO;
		for(int j = 0; j < px.length; j++)
			poly = poly.add(l(px, py, j));
	}
	
	public LagrangePolynomial(BigInteger[] px, BigInteger[] py) {
		if(px.length != py.length)
			throw new IllegalArgumentException();
		poly = TermPolynomial.ZERO;
		for(int j = 0; j < px.length; j++)
			poly = poly.add(l(px, py, j));
	}
	
	@Override
	public String toString() {
		return poly.toString();
	}
	
	private TermPolynomial l(BigInteger[] px, BigInteger[] py, int j) {
		TermPolynomial result = TermPolynomial.ONE;
		for(int i = 0; i < px.length; i++) {
			if(i == j)
				continue;
			Term t1 = new Term(BigInteger.ONE, px[j].subtract(px[i]));
			Term t0 = new Term(px[i].negate(), px[j].subtract(px[i]));
			TermPolynomial t = new TermPolynomial(new Term[] {t0, t1});
			result = result.multiply(t);
		}
		return result.multiply(py[j]);
	}

	@Override
	public Term y(BigInteger x) {
		return poly.y(x);
	}
}
