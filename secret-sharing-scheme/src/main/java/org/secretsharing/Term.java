package org.secretsharing;

import java.math.BigInteger;

/**
 * A fractional term used in {@link TermPolynomial}
 * @author robin
 *
 */
public final class Term implements Comparable<Term> {
	/**
	 * Constant term valued zero
	 */
	public static final Term ZERO = new Term(BigInteger.ZERO);
	/**
	 * Constant term valued one
	 */
	public static final Term ONE = new Term(BigInteger.ONE);
	
	/**
	 * The numerator of the fraction
	 */
	private BigInteger numerator;
	/**
	 * The denominator of the fraction
	 */
	private BigInteger denominator;
	
	public Term(BigInteger val) {
		this(val, BigInteger.ONE);
	}
	
	/**
	 * Create a new term with the argument numerator and denominator
	 * @param numerator
	 * @param denominator
	 */
	public Term(BigInteger numerator, BigInteger denominator) {
		if(denominator.equals(BigInteger.ZERO))
			throw new ArithmeticException("Divide by zero in term");
		BigInteger n = numerator;
		BigInteger d = denominator;
		if(n.equals(BigInteger.ZERO)) {
			d = BigInteger.ONE;
		} else {
			if(d.compareTo(BigInteger.ZERO) < 0) {
				n = n.negate();
				d = d.negate();
			}
			BigInteger gcd = n.gcd(d);
			n = n.divide(gcd);
			d = d.divide(gcd);
		}
		this.numerator = n;
		this.denominator = d;
	}
	
	@Override
	public int hashCode() {
		return numerator.hashCode() * denominator.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof Term) {
			Term t = (Term) obj;
			return numerator.equals(t.numerator) && denominator.equals(t.denominator);
		}
		return false;
	}
	
	@Override
	public int compareTo(Term o) {
		return subtract(o).numerator.compareTo(BigInteger.ZERO);
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
		return new Term(n, d);
	}
	
	public Term subtract(Term other) {
		return add(new Term(other.numerator.negate(), other.denominator));
	}
	
	/**
	 * Multiply this term by another term and return a new term
	 * @param other
	 * @return
	 */
	public Term multiply(Term other) {
		BigInteger n = numerator.multiply(other.numerator);
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d);
	}
	
	/**
	 * Multiply this term by a {@link BigInteger} and return a new term
	 * @param val
	 * @return
	 */
	public Term multiply(BigInteger val) {
		BigInteger n = numerator.multiply(val);
		BigInteger d = denominator;
		return new Term(n, d);
	}
	
	public boolean isWhole() {
		return denominator.equals(BigInteger.ONE);
	}
	
	public BigInteger whole() {
		if(!denominator.equals(BigInteger.ONE))
			throw new ArithmeticException("Cannot get whole value of fraction");
		return numerator;
	}
}
