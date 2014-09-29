package org.secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Randomly generated polynomial for a given Y-intercept, modulo a prime
 * @author robin
 *
 */
public class SecretPolynomial extends TermPolynomial {
	/**
	 * The prime that this polynomial is modulo
	 */
	private BigInteger prime;
	
	/**
	 * Create a new {@link SecretPolynomial} with a given Y-intercept and degree
	 * @param secret The Y-intercept
	 * @param secretBits The expected number of bits in the Y-intercept
	 * @param powx The degree of this polynomial
	 * @param rnd A source of random
	 */
	public SecretPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		prime = BigInteger.probablePrime(secretBits+1, rnd);
		while(prime.compareTo(secret) < 0)
			prime = BigInteger.probablePrime(secretBits+1, rnd);
		TermPolynomial poly = TermPolynomial.ONE.multiply(secret);
		for(int i = 1; i <= powx; i++) {
			BigInteger a = new BigInteger(secretBits, rnd).add(BigInteger.ONE);
			while(a.compareTo(prime) > 0)
				a = new BigInteger(secretBits, rnd).add(BigInteger.ONE);
			poly = poly.add(TermPolynomial.ONE.multiply(a).powX(i));
		}
		setTerms(poly.getTerms());
	}
	
	/**
	 * Return the prime that this polynomial is modulo
	 * @return
	 */
	public BigInteger getPrime() {
		return prime;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ( mod " + prime + ")";
	}
	
	/**
	 * Return an array of points on this polynomial
	 * @param count The number of points
	 * @param secretBits
	 * @return
	 */
	public BigPoint[] p(int count) {
		BigPoint[] p = new BigPoint[count];
		for(int i = 0; i < count; i++) {
			BigInteger x = BigInteger.valueOf(i+1);
			p[i] = p(x);
		}
		return p;
	}
	
	/**
	 * Return a single point on this polynomial
	 * @param x
	 * @return
	 */
	public BigPoint p(BigInteger x) {
		Term t = y(x);
		if(!t.getDenominator().equals(BigInteger.ONE))
			throw new IllegalStateException();
		return new BigPoint(x, t.getNumerator().mod(prime));
	}
}
