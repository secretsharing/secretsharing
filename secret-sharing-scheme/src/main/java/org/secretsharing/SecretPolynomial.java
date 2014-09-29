package org.secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SecretPolynomial extends TermPolynomial {
	private static final Random rnd = new SecureRandom();
	
	private BigInteger prime;
	
	public SecretPolynomial(BigInteger secret, int secretBits, int powx) {
		prime = BigInteger.probablePrime(secretBits + 8, rnd);
		TermPolynomial poly = TermPolynomial.ONE.multiply(secret);
		for(int i = 1; i <= powx; i++) {
			poly = poly.add(TermPolynomial.ONE.multiply(new BigInteger(secretBits, rnd).add(BigInteger.ONE)).powX(i));
		}
		setTerms(poly.getTerms());
	}
	
	public BigInteger getPrime() {
		return prime;
	}
	
	public BigPoint[] p(int count, int secretBits) {
		Set<BigInteger> xs = new HashSet<BigInteger>();
		xs.add(BigInteger.ZERO);
		BigPoint[] p = new BigPoint[count];
		for(int i = 0; i < count; i++) {
			BigInteger x = BigInteger.ZERO;
			while(xs.contains(x))
				x = new BigInteger(secretBits, rnd).add(BigInteger.ONE);
			p[i] = p(x);
		}
		return p;
	}
	
	public BigPoint p(BigInteger x) {
		Term t = y(x);
		if(!t.getDenominator().equals(BigInteger.ONE))
			throw new IllegalStateException();
		return new BigPoint(x, t.getNumerator());
	}
}
