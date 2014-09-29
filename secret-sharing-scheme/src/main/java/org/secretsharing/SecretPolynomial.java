package org.secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class SecretPolynomial implements Polynomial {
	private static final Random rnd = new SecureRandom();
	
	private TermPolynomial poly;
	private BigInteger prime;
	
	public SecretPolynomial(BigInteger secret, int secretBits, int powx) {
		this(secret, secretBits, powx, rnd);
	}
	
	public SecretPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		prime = BigInteger.probablePrime(secretBits + 1, rnd);
		poly = TermPolynomial.ONE.multiply(secret);
		for(int i = 1; i < powx; i++) {
			poly = poly.add(TermPolynomial.ONE.multiply(new BigInteger(secretBits, rnd)).powX(i));
		}
	}
	
	public BigInteger getPrime() {
		return prime;
	}
	
	@Override
	public String toString() {
		return poly.toString();
	}
	
	public BigPoint p(BigInteger x) {
		Term t = y(x);
		if(!t.getDenominator().equals(BigInteger.ONE))
			throw new IllegalStateException();
		return new BigPoint(x, t.getNumerator().mod(prime));
	}

	@Override
	public Term y(BigInteger x) {
		return poly.y(x);
	}
}
