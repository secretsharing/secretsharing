package org.secretsharing;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * A polynomial composed of terms
 * @author robin
 *
 */
public class TermPolynomial {
	/**
	 * Constant zero-valued polynomial
	 */
	public static final TermPolynomial ZERO = new TermPolynomial(Term.ZERO);
	/**
	 * Constant one-valued polynomial
	 */
	public static final TermPolynomial ONE = new TermPolynomial(Term.ONE);
	
	private static TermPolynomial lagrangePolynomial(BigPoint[] pts, BigInteger modulus) {
		BigInteger[] px = new BigInteger[pts.length];
		BigInteger[] py = new BigInteger[pts.length];
		for(int i = 0; i < pts.length; i++) {
			px[i] = pts[i].getX();
			py[i] = pts[i].getY();
		}
		TermPolynomial poly = TermPolynomial.ZERO;
		for(int j = 0; j < px.length; j++)
			poly = poly.add(lagrangeSubPolynomial(px, py, j));
		Term[] terms = poly.getTerms();
		for(int i = 0; i < terms.length; i++)
			terms[i] = lagrangeTermModulo(terms[i],modulus);
		return new TermPolynomial(terms, modulus);
	}
	
	private static TermPolynomial lagrangeSubPolynomial(BigInteger[] px, BigInteger[] py, int j) {
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
	
	private static Term lagrangeTermModulo(Term term, BigInteger mod) {
		BigInteger n = term.getNumerator();
		BigInteger d = term.getDenominator();
		d = d.modInverse(mod);
		return new Term(n.multiply(d).mod(mod), BigInteger.ONE);
	}
	
	private static TermPolynomial secretPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		BigInteger prime = BigInteger.probablePrime(secretBits+1, rnd);
		while(prime.compareTo(secret) < 0)
			prime = BigInteger.probablePrime(secretBits+1, rnd);
		TermPolynomial poly = TermPolynomial.ONE.multiply(secret);
		for(int i = 1; i <= powx; i++) {
			BigInteger a = new BigInteger(secretBits, rnd).add(BigInteger.ONE);
			while(a.compareTo(prime) > 0)
				a = new BigInteger(secretBits, rnd).add(BigInteger.ONE);
			poly = poly.add(TermPolynomial.ONE.multiply(a).powX(i));
		}
		return new TermPolynomial(poly.getTerms(), prime);
	}
	
	/**
	 * The terms in this polynomial.  The ith element in the array
	 * is multiplied by x^i in the polynomial
	 */
	private Term[] terms;
	
	private BigInteger modulus;
	
	public TermPolynomial(Term... terms) {
		this(terms, null);
	}
	
	public TermPolynomial(TermPolynomial other) {
		this(other.getTerms(), other.getModulus());
	}
	
	/**
	 * Create a new {@link TermPolynomial} from an array of terms.
	 * @param terms
	 */
	public TermPolynomial(Term[] terms, BigInteger modulus) {
		this.terms = Arrays.copyOf(terms, terms.length);
		this.modulus = modulus;
	}
	
	public TermPolynomial(BigPoint[] pts, BigInteger modulus) {
		this(lagrangePolynomial(pts, modulus));
	}
	
	public TermPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		this(secretPolynomial(secret, secretBits, powx, rnd));
	}
	
	protected BigInteger mod(BigInteger val) {
		if(getModulus() == null)
			return val;
		return val.mod(getModulus()).add(getModulus()).mod(getModulus());
	}
	
	@Override
	public String toString() {
		Term[] terms = getTerms();
		if(terms.length == 0)
			return "0";
		StringBuilder sb = new StringBuilder(terms[0].toString());
		for(int i = 1; i < terms.length; i++)
			sb.append(" + " + terms[i] + "x^" + i);
		if(getModulus() != null)
			sb.append(" (mod " + getModulus() + ")");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof TermPolynomial) {
			TermPolynomial p = (TermPolynomial) obj;
			return Arrays.equals(getTerms(), p.getTerms()) && Objects.equals(getModulus(), p.getModulus());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(getTerms()) + Objects.hashCode(getModulus());
	}
	
	/**
	 * Return the terms in this polynomial
	 * @return
	 */
	public Term[] getTerms() {
		return Arrays.copyOf(terms, terms.length);
	}
	
	public BigInteger getModulus() {
		return modulus;
	}
	
	/**
	 * Compute the y-value for a given x-value
	 * @param x
	 * @return
	 */
	public Term y(BigInteger x) {
		Term result = Term.ZERO;
		Term[] terms = getTerms();
		BigInteger xp = BigInteger.ONE;
		for(int i = 0; i < terms.length; i++) {
			result = result.add(terms[i].multiply(xp));
			xp = xp.multiply(x);
		}
		return result;
	}
	
	/**
	 * Compute the y-value for a given x-value
	 * @param x
	 * @return
	 */
	public BigInteger wholeY(BigInteger x) {
		Term result = Term.ZERO;
		Term[] terms = getTerms();
		BigInteger xp = BigInteger.ONE;
		for(int i = 0; i < terms.length; i++) {
			result = result.add(terms[i].multiply(xp));
			xp = xp.multiply(x);
		}
		return mod(result.whole());
	}
	
	/**
	 * Add this polynomial to another polynomial and return a new polynomial
	 * @param other
	 * @return
	 */
	public TermPolynomial add(TermPolynomial other) {
		Term[] terms = getTerms();
		Term[] otherTerms = other.getTerms();
		Term[] t = new Term[Math.max(terms.length, otherTerms.length)];
		for(int i = 0; i < t.length; i++) {
			Term lhs = (i < terms.length) ? terms[i] : Term.ZERO;
			Term rhs = (i < otherTerms.length) ? otherTerms[i] : Term.ZERO;
			t[i] = lhs.add(rhs);
		}
		return new TermPolynomial(t);
	}
	
	/**
	 * Multiply this polynomial by a power of X and return
	 * a new polynomial
	 * @param powx
	 * @return
	 */
	public TermPolynomial powX(int powx) {
		Term[] terms = getTerms();
		Term[] t = new Term[terms.length + powx];
		Arrays.fill(t, Term.ZERO);
		System.arraycopy(terms, 0, t, powx, terms.length);
		return new TermPolynomial(t);
	}
	
	/**
	 * Multiply this polynomial by a term, and a power of X,
	 * and return a new polynomial
	 * @param term
	 * @param powx
	 * @return
	 */
	public TermPolynomial multiply(Term term, int powx) {
		Term[] t = getTerms();
		for(int i = 0; i < t.length; i++)
			t[i] = t[i].multiply(term);
		return new TermPolynomial(t).powX(powx);
	}
	
	/**
	 * Multiply this polynomial by another polynomial
	 * and return a new polynomial
	 * @param other
	 * @return
	 */
	public TermPolynomial multiply(TermPolynomial other) {
		TermPolynomial result = TermPolynomial.ZERO;
		Term[] terms = getTerms();
		
		for(int i = 0; i < terms.length; i++) {
			result = result.add(other.multiply(terms[i], i));
		}
		
		return result;
	}
	
	/**
	 * Multiply this polynomial by a constant and return
	 * a new polynomial
	 * @param val
	 * @return
	 */
	public TermPolynomial multiply(BigInteger val) {
		Term[] terms = getTerms();
		for(int i = 0; i < terms.length; i++)
			terms[i] = terms[i].multiply(val);
		return new TermPolynomial(terms);
	}

	/**
	 * Return a single point on this polynomial
	 * @param x
	 * @return
	 */
	public BigPoint p(BigInteger x) {
		return new BigPoint(x, mod(y(x).whole()));
	}
	
	public BigPoint[] p(BigInteger[] x) {
		BigPoint[] pts = new BigPoint[x.length];
		for(int i = 0; i < x.length; i++)
			pts[i] = p(x[i]);
		return pts;
	}
}
