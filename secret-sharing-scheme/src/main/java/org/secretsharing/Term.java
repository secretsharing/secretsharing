package org.secretsharing;

import java.math.BigInteger;

/**
 * A fractional term used in {@link TermPolynomial}
 * @author robin
 *
 */
public class Term {
	/**
	 * Constant term valued zero
	 */
	public static final Term ZERO = new Term(BigInteger.ZERO, BigInteger.ONE);
	/**
	 * Constant term valued one
	 */
	public static final Term ONE = new Term(BigInteger.ONE, BigInteger.ONE);
	
	/**
	 * The numerator of the fraction
	 */
	private BigInteger numerator;
	/**
	 * The denominator of the fraction
	 */
	private BigInteger denominator;
	
	/**
	 * Create a new term with the argument numerator and denominator
	 * @param numerator
	 * @param denominator
	 */
	public Term(BigInteger numerator, BigInteger denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	/**
	 * Return the numerator
	 * @return
	 */
	public BigInteger getNumerator() {
		return numerator;
	}

	/**
	 * Return the denominator
	 * @return
	 */
	public BigInteger getDenominator() {
		return denominator;
	}
	
	@Override
	public String toString() {
		if(denominator.equals(BigInteger.ONE))
			return numerator.toString();
		return "(" + numerator + "/" + denominator + ")";
	}
	
	/**
	 * Add another term to this term and return a new term
	 * @param other
	 * @return
	 */
	public Term add(Term other) {
		BigInteger n = numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d).simplify();
	}
	
	/**
	 * Multiply this term by another term and return a new term
	 * @param other
	 * @return
	 */
	public Term multiply(Term other) {
		BigInteger n = numerator.multiply(other.numerator);
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d).simplify();
	}
	
	/**
	 * Multiply this term by a {@link BigInteger} and return a new term
	 * @param val
	 * @return
	 */
	public Term multiply(BigInteger val) {
		BigInteger n = numerator.multiply(val);
		BigInteger d = denominator;
		return new Term(n, d).simplify();
	}
	
	/**
	 * Compute the modulo term of this term for a given modulus,
	 * and return it as a new term 
	 * @param mod
	 * @return
	 */
	public Term modulo(BigInteger mod) {
		BigInteger n = numerator;
		BigInteger d = denominator;
		d = d.modInverse(mod);
		return new Term(n.multiply(d).mod(mod), BigInteger.ONE);
	}
	
	/**
	 * Simplify the fraction in this term
	 * @return
	 */
	public Term simplify() {
		BigInteger n = numerator;
		BigInteger d = denominator;
		if(d.compareTo(BigInteger.ZERO) < 0) {
			n = n.negate();
			d = d.negate();
		}
		BigInteger gcd = n.gcd(d);
		if(gcd.equals(BigInteger.ZERO))
			return this;
		return new Term(n.divide(gcd), d.divide(gcd));
	}
}
