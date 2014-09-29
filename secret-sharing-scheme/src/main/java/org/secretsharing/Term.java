package org.secretsharing;

import java.math.BigInteger;

public class Term {
	public static final Term ZERO = new Term(BigInteger.ZERO, BigInteger.ONE);
	public static final Term ONE = new Term(BigInteger.ONE, BigInteger.ONE);
	
	private BigInteger numerator;
	private BigInteger denominator;
	
	public Term(BigInteger numerator, BigInteger denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public BigInteger getNumerator() {
		return numerator;
	}

	public BigInteger getDenominator() {
		return denominator;
	}
	
	public Term add(Term other) {
		BigInteger n = numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
		BigInteger d = denominator.multiply(other.denominator);
		BigInteger gcd = n.gcd(d);
		return new Term(n.divide(gcd), d.divide(gcd));
	}
	
	public Term multiply(Term other) {
		BigInteger n = numerator.multiply(other.numerator);
		BigInteger d = denominator.multiply(other.denominator);
		BigInteger gcd = n.gcd(d);
		return new Term(n.divide(gcd), d.divide(gcd));
	}
	
	public Term multiply(BigInteger val) {
		BigInteger n = numerator.multiply(val);
		BigInteger d = denominator;
		BigInteger gcd = n.gcd(d);
		return new Term(n.divide(gcd), d.divide(gcd));
	}
}
