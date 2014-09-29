package org.secretsharing;

import java.math.BigInteger;

/**
 * A polynomial created using Lagrange interpolation, and then simplified modulo
 * some number
 * @author robin
 *
 */
public class LagrangePolynomial extends TermPolynomial {
	/**
	 * Create a {@link LagrangePolynomial} from the given points
	 * with the given modulus
	 * @param pts
	 * @param mod
	 */
	public LagrangePolynomial(BigPoint[] pts, BigInteger mod) {
		BigInteger[] px = new BigInteger[pts.length];
		BigInteger[] py = new BigInteger[pts.length];
		for(int i = 0; i < pts.length; i++) {
			px[i] = pts[i].getX();
			py[i] = pts[i].getY();
		}
		TermPolynomial poly = TermPolynomial.ZERO;
		for(int j = 0; j < px.length; j++)
			poly = poly.add(l(px, py, j));
		Term[] terms = poly.getTerms();
		for(int i = 0; i < terms.length; i++)
			terms[i] = terms[i].modulo(mod);
		setTerms(terms);
	}
	
	/**
	 * A single term-expression in the lagrange interpolation
	 * @param px point X values
	 * @param py point Y values
	 * @param j The jth expression
	 * @return
	 */
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
}
