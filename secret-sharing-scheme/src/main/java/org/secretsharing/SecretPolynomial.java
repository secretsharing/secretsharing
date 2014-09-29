package org.secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class SecretPolynomial implements Polynomial {
	private static final Random rnd = new SecureRandom();
	
	private TermPolynomial poly;
	private BigInteger prime;
	
	public SecretPolynomial(BigInteger secret, int powx) {
		this(secret, powx, rnd);
	}
	
	public SecretPolynomial(BigInteger secret, int powx, Random rnd) {
		prime = BigInteger.probablePrime(secret.bitLength() + 1, rnd);
		poly = TermPolynomial.ZERO;
		for(int i = 0; i < powx; i++) {
			poly = poly.add(TermPolynomial.ONE.multiply(new BigInteger(secret.bitLength(), rnd)).powX(i));
		}
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
