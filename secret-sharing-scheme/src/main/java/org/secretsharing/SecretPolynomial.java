package org.secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SecretPolynomial extends TermPolynomial {
	private BigInteger prime;
	
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
	
	public BigInteger getPrime() {
		return prime;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ( mod " + prime + ")";
	}
	
	public BigPoint[] p(int count, int secretBits) {
		BigPoint[] p = new BigPoint[count];
		int off = 2;
		for(int i = 0; i < count; i++) {
			BigInteger x = BigInteger.valueOf(i+off);
			while(!x.isProbablePrime(200))
				x = BigInteger.valueOf(i+(++off));
			p[i] = p(x);
		}
		return p;
	}
	
	public BigPoint p(BigInteger x) {
		Term t = y(x);
		if(!t.getDenominator().equals(BigInteger.ONE))
			throw new IllegalStateException();
		return new BigPoint(x, t.getNumerator().mod(prime));
	}
}
