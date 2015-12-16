/*

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * A polynomial composed of terms
 * @author Robin Kirkman
 *
 */
public class TermPolynomial {
	/**
	 * Constant zero-valued polynomial
	 */
	public static final TermPolynomial ZERO = new TermPolynomial(Term.ZERO);
	/**
	 * Constant one-valued polynomial
	 */
	public static final TermPolynomial ONE = new TermPolynomial(Term.ONE);
	
	/**
	 * Compute a Lagrange polynomial from an array of points, with an (optional) specified modulus
	 * @param pts
	 * @param modulus
	 * @return
	 */
	public static TermPolynomial lagrangePolynomial(BigPoint[] pts, BigInteger modulus) {
		BigInteger[] px = new BigInteger[pts.length];
		BigInteger[] py = new BigInteger[pts.length];
		for(int i = 0; i < pts.length; i++) {
			px[i] = pts[i].getX();
			py[i] = pts[i].getY();
		}
		TermPolynomial poly = TermPolynomial.ZERO;
		for(int j = 0; j < px.length; j++)
			poly = poly.add(lagrangeSubPolynomial(px, py, j));
		Term[] terms = poly.getTerms();
		for(int i = 0; i < terms.length; i++)
			terms[i] = lagrangeTermModulo(terms[i],modulus);
		return new TermPolynomial(terms, modulus);
	}
	
	/**
	 * Compute a sub-polynomial as part of a Lagrange polynomial
	 * @param px
	 * @param py
	 * @param j
	 * @return
	 */
	private static TermPolynomial lagrangeSubPolynomial(BigInteger[] px, BigInteger[] py, int j) {
		TermPolynomial result = TermPolynomial.ONE;
		for(int i = 0; i < px.length; i++) {
			if(i == j)
				continue;
			Term t1 = new Term(BigInteger.ONE, px[j].subtract(px[i]));
			Term t0 = new Term(px[i].negate(), px[j].subtract(px[i]));
			TermPolynomial t = new TermPolynomial(new Term[] {t0, t1});
			result = result.multiply(t);
		}
		return result.multiply(py[j]);
	}
	
	/**
	 * Return the term as a coefficient of some power of X, adjusted to be
	 * an integer for some modulus
	 * @param term
	 * @param mod
	 * @return
	 */
	private static Term lagrangeTermModulo(Term term, BigInteger mod) {
		if(mod == null)
			return term;
		BigInteger n = term.getNumerator();
		BigInteger d = term.getDenominator();
		d = d.modInverse(mod);
		return new Term(n.multiply(d).mod(mod));
	}
	
	/**
	 * Create a random secret-generating polynomial for the argument secret
	 * @param secret
	 * @param secretBits
	 * @param powx
	 * @param rnd
	 * @return
	 */
	public static TermPolynomial secretPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		BigInteger prime = BigInteger.probablePrime(secretBits+1, rnd);
		while(prime.compareTo(secret) < 0)
			prime = BigInteger.probablePrime(secretBits+1, rnd);
		TermPolynomial poly = TermPolynomial.ONE.multiply(secret);
		for(int i = 1; i <= powx; i++) {
			BigInteger a = new BigInteger(secretBits, rnd);
			while(a.compareTo(prime) > 0)
				a = new BigInteger(secretBits, rnd);
			poly = poly.add(TermPolynomial.ONE.multiply(a).powX(i));
		}
		return new TermPolynomial(poly.getTerms(), prime);
	}
	
	/**
	 * The terms in this polynomial.  The ith element in the array
	 * is multiplied by x^i in the polynomial
	 */
	private Term[] terms;
	
	/**
	 * The modulus for this polynomial.  May be null.
	 */
	private BigInteger modulus;
	
	/**
	 * Create a {@link TermPolynomial} from an array of {@link Term}s
	 * @param terms
	 */
	public TermPolynomial(Term... terms) {
		this(terms, null);
	}
	
	/**
	 * Copy a {@link TermPolynomial}
	 * @param other
	 */
	public TermPolynomial(TermPolynomial other) {
		this(other.getTerms(), other.getModulus());
	}
	
	/**
	 * Create a new {@link TermPolynomial} from an array of terms.
	 * @param terms
	 */
	public TermPolynomial(Term[] terms, BigInteger modulus) {
		this.terms = Arrays.copyOf(terms, terms.length);
		this.modulus = modulus;
	}
	
	/**
	 * Create a Lagrange interpolating polynomial for the argument points
	 * and optional modulus
	 * @param pts
	 * @param modulus
	 */
	public TermPolynomial(BigPoint[] pts, BigInteger modulus) {
		this(lagrangePolynomial(pts, modulus));
	}
	
	/**
	 * Create a random secret-generating polynomial for the argument secret
	 * @param secret
	 * @param secretBits
	 * @param powx
	 * @param rnd
	 */
	public TermPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		this(secretPolynomial(secret, secretBits, powx, rnd));
	}
	
	@Override
	public String toString() {
		Term[] terms = getTerms();
		if(terms.length == 0)
			return "0";
		StringBuilder sb = new StringBuilder(terms[0].toString());
		for(int i = 1; i < terms.length; i++)
			sb.append(" + " + terms[i] + "x^" + i);
		if(getModulus() != null)
			sb.append(" (mod " + getModulus() + ")");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof TermPolynomial) {
			TermPolynomial p = (TermPolynomial) obj;
			return 
					Arrays.equals(getTerms(), p.getTerms()) 
					&& (getModulus() == null ? p.getModulus() == null : getModulus().equals(p.getModulus()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(getTerms()) + (getModulus() == null ? 0 : getModulus().hashCode());
	}
	
	/**
	 * Return the terms in this polynomial
	 * @return
	 */
	public Term[] getTerms() {
		return Arrays.copyOf(terms, terms.length);
	}
	
	/**
	 * Return the modulus.  May be null.
	 * @return
	 */
	public BigInteger getModulus() {
		return modulus;
	}
	
	/**
	 * Compute the y-value for a given x-value
	 * @param x
	 * @return
	 */
	public Term y(BigInteger x) {
		Term result = Term.ZERO;
		Term[] terms = getTerms();
		BigInteger xp = BigInteger.ONE;
		for(int i = 0; i < terms.length; i++) {
			result = result.add(terms[i].multiply(xp));
			xp = xp.multiply(x);
		}
		if(modulus != null)
			result = result.mod(modulus);
		return result;
	}
	
	/**
	 * Add this polynomial to another polynomial and return a new polynomial
	 * @param other
	 * @return
	 */
	public TermPolynomial add(TermPolynomial other) {
		Term[] terms = getTerms();
		Term[] otherTerms = other.getTerms();
		Term[] t = new Term[Math.max(terms.length, otherTerms.length)];
		for(int i = 0; i < t.length; i++) {
			Term lhs = (i < terms.length) ? terms[i] : Term.ZERO;
			Term rhs = (i < otherTerms.length) ? otherTerms[i] : Term.ZERO;
			t[i] = lhs.add(rhs);
		}
		return new TermPolynomial(t);
	}
	
	/**
	 * Multiply this polynomial by a power of X and return
	 * a new polynomial
	 * @param powx The power of X to multiply by
	 * @return A new {@link TermPolynomial}
	 */
	public TermPolynomial powX(int powx) {
		Term[] terms = getTerms();
		Term[] t = new Term[terms.length + powx];
		Arrays.fill(t, Term.ZERO);
		System.arraycopy(terms, 0, t, powx, terms.length);
		return new TermPolynomial(t);
	}
	
	/**
	 * Multiply this polynomial by a term, and a power of X,
	 * and return a new polynomial
	 * @param term
	 * @param powx
	 * @return
	 */
	public TermPolynomial multiply(Term term, int powx) {
		Term[] t = getTerms();
		for(int i = 0; i < t.length; i++)
			t[i] = t[i].multiply(term);
		return new TermPolynomial(t).powX(powx);
	}
	
	/**
	 * Multiply this polynomial by another polynomial
	 * and return a new polynomial
	 * @param other
	 * @return
	 */
	public TermPolynomial multiply(TermPolynomial other) {
		TermPolynomial result = TermPolynomial.ZERO;
		Term[] terms = getTerms();
		
		for(int i = 0; i < terms.length; i++) {
			result = result.add(other.multiply(terms[i], i));
		}
		
		return result;
	}
	
	/**
	 * Multiply this polynomial by a constant and return
	 * a new polynomial
	 * @param val
	 * @return
	 */
	public TermPolynomial multiply(BigInteger val) {
		Term[] terms = getTerms();
		for(int i = 0; i < terms.length; i++)
			terms[i] = terms[i].multiply(val);
		return new TermPolynomial(terms);
	}

	/**
	 * Return a single point on this polynomial
	 * @param x
	 * @return
	 */
	public BigPoint p(BigInteger x) {
		return new BigPoint(x, y(x).whole());
	}
	
	/**
	 * Return an array of points on this polynomial for
	 * the argument array of X values
	 * @param x
	 * @return
	 */
	public BigPoint[] p(BigInteger[] x) {
		BigPoint[] pts = new BigPoint[x.length];
		for(int i = 0; i < x.length; i++)
			pts[i] = p(x[i]);
		return pts;
	}
}
