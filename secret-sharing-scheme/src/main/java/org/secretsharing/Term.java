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
	
	@Override
	public String toString() {
		if(denominator.equals(BigInteger.ONE))
			return numerator.toString();
		return "(" + numerator + "/" + denominator + ")";
	}
	
	public Term add(Term other) {
		BigInteger n = numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d).simplify();
	}
	
	public Term multiply(Term other) {
		BigInteger n = numerator.multiply(other.numerator);
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d).simplify();
	}
	
	public Term multiply(BigInteger val) {
		BigInteger n = numerator.multiply(val);
		BigInteger d = denominator;
		return new Term(n, d).simplify();
	}
	
	public Term modulo(BigInteger mod) {
		BigInteger n = numerator;
		BigInteger d = denominator;
		d = d.modInverse(mod);
		return new Term(n.multiply(d).mod(mod), BigInteger.ONE);
	}
	
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
