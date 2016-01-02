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

import org.mitre.secretsharing.util.InputValidation;

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
	 * @param pts Array of points found on the interpolating Lagrange polynomial
	 * @param modulus The modulus of the polynomial
	 * @return A new {@link TermPolynomial}
	 */
	public static TermPolynomial lagrangePolynomial(BigPoint[] pts, BigInteger modulus) {
		InputValidation.begin()
			.when(pts == null, "pts is null")
			.when(modulus != null && modulus.compareTo(BigInteger.ONE) <= 0, "modulus not greater than one")
			.validate();
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
	 * @param px The X values of the points
	 * @param py The Y values of the points
	 * @param j The index in the array of points
	 * @return A new {@link TermPolynomial} specific to this index
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
	 * an integer for some modulus, for use when computing a Lagrange polynomial.
	 * @param term The term
	 * @param mod The modulus
	 * @return A new {@link Term}
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
	 * @param secret The secret, as a Y coordinate on a polynomial
	 * @param secretBits Number of bits in the secret
	 * @param powx The power of the polynomial, one less than the number of required secret parts
	 * @param rnd A source of random
	 * @return A new {@link TermPolynomial}
	 */
	public static TermPolynomial secretPolynomial(BigInteger secret, int secretBits, int powx, Random rnd) {
		InputValidation.begin()
			.when(secret == null, "secret is null")
			.when(secretBits < 0, "secretBits is less than zero")
			.when(rnd == null, "rnd is null")
			.validate();
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
	 * Create a {@link TermPolynomial} from an array of {@link Term}s.
	 * The 0th element is the 0th power in the polynomial, 1th element
	 * is 1th power, etc
	 * @param terms The array of terms
	 */
	public TermPolynomial(Term... terms) {
		this(terms, null);
	}
	
	/**
	 * Copy a {@link TermPolynomial}
	 * @param other The {@link TermPolynomial} to copy
	 */
	public TermPolynomial(TermPolynomial other) {
		this(other.getTerms(), other.getModulus());
	}
	
	/**
	 * Create a new {@link TermPolynomial} from an array of terms and a modulus.
	 * The 0th element is the 0th power in the polynomial, 1th element
	 * is 1th power, etc
	 * @param terms The array of terms
	 * @param modulus The modulus, or {@code null} for no modulus
	 */
	public TermPolynomial(Term[] terms, BigInteger modulus) {
		InputValidation.begin()
			.when(terms == null, "terms is null")
			.when(modulus != null && modulus.compareTo(BigInteger.ONE) <= 0, "modulus not greater than one")
			.validate();
		this.terms = Arrays.copyOf(terms, terms.length);
		this.modulus = modulus;
		if(this.modulus != null) {
			for(int i = 0; i < this.terms.length; i++)
				this.terms[i] = this.terms[i].mod(this.modulus);
		}
	}
	
	/**
	 * Create a Lagrange interpolating polynomial for the argument points
	 * and optional modulus
	 * @param pts The points to interpolate
	 * @param modulus The polynomial modulus
	 */
	public TermPolynomial(BigPoint[] pts, BigInteger modulus) {
		this(lagrangePolynomial(pts, modulus));
	}
	
	/**
	 * Create a random secret-generating polynomial for the argument secret
	 * @param secret The secret, as a {@link BigInteger}
	 * @param secretBits The number of bits in the secret
	 * @param powx The power of the polynomial, one less than the number of required secret parts
	 * @param rnd A source of randomness
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
	 * Return the terms in this polynomial, ordered such that the nth element
	 * in the array is the coefficient to the nth power of X
	 * @return The terms
	 */
	public Term[] getTerms() {
		return Arrays.copyOf(terms, terms.length);
	}
	
	/**
	 * Return the modulus.  May be null.
	 * @return The modulus, or {@code null} for no modulus
	 */
	public BigInteger getModulus() {
		return modulus;
	}
	
	/**
	 * Compute the Y coordinate for a given X coordinate
	 * @param x The X coordinate
	 * @return The Y coordinate
	 */
	public Term y(BigInteger x) {
		InputValidation.begin().when(x == null, "argument is null").validate();
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
	 * @param other The {@link TermPolynomial} to add
	 * @return A new {@link TermPolynomial}
	 */
	public TermPolynomial add(TermPolynomial other) {
		InputValidation.begin().when(other == null, "argument is null").validate();
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
		InputValidation.begin().when(powx < 0, "powx is less than 0").validate();
		Term[] terms = getTerms();
		Term[] t = new Term[terms.length + powx];
		Arrays.fill(t, Term.ZERO);
		System.arraycopy(terms, 0, t, powx, terms.length);
		return new TermPolynomial(t);
	}
	
	/**
	 * Multiply this polynomial by a term and a given power of X,
	 * and return a new polynomial
	 * @param term The coeffient of the multiplicand
	 * @param powx The X power of the multiplicand
	 * @return A new {@link TermPolynomial}
	 */
	public TermPolynomial multiply(Term term, int powx) {
		InputValidation.begin()
			.when(term == null, "term is null")
			.when(powx < 0, "powx is less than 0")
			.validate();
		Term[] t = new Term[getTerms().length + powx];
		for(int i = 0; i < t.length; i++)
			t[i] = (i < powx ? Term.ZERO : getTerms()[i - powx].multiply(term));
		return new TermPolynomial(t, modulus);
	}
	
	/**
	 * Multiply this polynomial by another polynomial
	 * and return a new polynomial
	 * @param other The {@link TermPolynomial} to multiply by
	 * @return A new {@link TermPolynomial}
	 */
	public TermPolynomial multiply(TermPolynomial other) {
		InputValidation.begin().when(other == null, "argument is null").validate();
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
	 * @param val The number to multiply by
	 * @return A new {@link TermPolynomial}
	 */
	public TermPolynomial multiply(BigInteger val) {
		InputValidation.begin().when(val == null, "argument is null").validate();
		return multiply(new Term(val), 0);
	}

	/**
	 * Return a single point on this polynomial, as a {@link BigPoint}
	 * @param x The X coordinate of the point
	 * @return The point
	 */
	public BigPoint p(BigInteger x) {
		InputValidation.begin().when(x == null, "argument is null").validate();
		return new BigPoint(x, y(x).whole());
	}
	
	/**
	 * Return an array of points on this polynomial for
	 * the argument array of X values, as {@link BigPoint}s
	 * @param x The X coordinates of the points
	 * @return An array of points
	 */
	public BigPoint[] p(BigInteger[] x) {
		InputValidation.begin().when(x == null, "argument is null").validate();
		BigPoint[] pts = new BigPoint[x.length];
		for(int i = 0; i < x.length; i++)
			pts[i] = p(x[i]);
		return pts;
	}
}
