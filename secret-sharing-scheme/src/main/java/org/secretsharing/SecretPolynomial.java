package org.secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class SecretPolynomial extends TermPolynomial {
	private static final Random rnd = new SecureRandom();
	
	private BigInteger prime;
	
	public SecretPolynomial(BigInteger secret, int secretBits, int powx) {
		this(secret, secretBits, powx, rnd);
	}
	
	public SecretPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		prime = BigInteger.probablePrime(secretBits + 1, rnd);
		TermPolynomial poly = TermPolynomial.ONE.multiply(secret);
		for(int i = 1; i < powx; i++) {
			poly = poly.add(TermPolynomial.ONE.multiply(new BigInteger(secretBits, rnd)).powX(i));
		}
		setTerms(poly.getTerms());
	}
	
	public BigInteger getPrime() {
		return prime;
	}
	
	public BigPoint p(BigInteger x) {
		Term t = y(x);
		if(!t.getDenominator().equals(BigInteger.ONE))
			throw new IllegalStateException();
		return new BigPoint(x, t.getNumerator().mod(prime));
	}
}
