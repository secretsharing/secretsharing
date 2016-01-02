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

import org.mitre.secretsharing.util.InputValidation;

/**
 * A fractional term used in {@link TermPolynomial}
 * @author Robin Kirkman
 *
 */
public final class Term implements Comparable<Term> {
	/**
	 * Constant term valued zero
	 */
	public static final Term ZERO = new Term(BigInteger.ZERO);
	/**
	 * Constant term valued one
	 */
	public static final Term ONE = new Term(BigInteger.ONE);
	
	/**
	 * The numerator of the fraction
	 */
	private BigInteger numerator;
	/**
	 * The denominator of the fraction
	 */
	private BigInteger denominator;
	
	/**
	 * Create a whole-number term
	 * @param val The value of the term
	 */
	public Term(BigInteger val) {
		this(val, BigInteger.ONE);
	}
	
	/**
	 * Create a new fractional term with the argument numerator and denominator
	 * @param numerator The numerator
	 * @param denominator The denominator
	 */
	public Term(BigInteger numerator, BigInteger denominator) {
		InputValidation.begin()
			.when(numerator == null, "numerator is null")
			.when(denominator == null, "denominator is null")
			.when(BigInteger.ZERO.equals(denominator), "denominator is 0")
			.validate();
		BigInteger n = numerator;
		BigInteger d = denominator;
		if(n.equals(BigInteger.ZERO)) {
			d = BigInteger.ONE;
		} else {
			if(d.compareTo(BigInteger.ZERO) < 0) {
				n = n.negate();
				d = d.negate();
			}
			BigInteger gcd = n.gcd(d);
			n = n.divide(gcd);
			d = d.divide(gcd);
		}
		this.numerator = n;
		this.denominator = d;
	}
	
	@Override
	public int hashCode() {
		return numerator.hashCode() * denominator.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof Term) {
			Term t = (Term) obj;
			return numerator.equals(t.numerator) && denominator.equals(t.denominator);
		}
		return false;
	}
	
	@Override
	public int compareTo(Term o) {
		InputValidation.begin().when(o == null, "argument is null").validate();
		return subtract(o).numerator.compareTo(BigInteger.ZERO);
	}
	
	/**
	 * Return the numerator
	 * @return The numerator
	 */
	public BigInteger getNumerator() {
		return numerator;
	}

	/**
	 * Return the denominator
	 * @return The denominator
	 */
	public BigInteger getDenominator() {
		return denominator;
	}
	
	@Override
	public String toString() {
		if(denominator.equals(BigInteger.ONE))
			return numerator.toString();
		return "(" + numerator + "/" + denominator + ")";
	}
	
	/**
	 * Add another term to this term and return a new term
	 * @param other The term to add
	 * @return A new Term
	 */
	public Term add(Term other) {
		InputValidation.begin().when(other == null, "argument is null").validate();
		BigInteger n = numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d);
	}
	
	/**
	 * Subtract another term from this term and return a new term
	 * @param other The term to subtract
	 * @return A new Term
	 */
	public Term subtract(Term other) {
		InputValidation.begin().when(other == null, "argument is null").validate();
		return add(new Term(other.numerator.negate(), other.denominator));
	}
	
	/**
	 * Multiply this term by another term and return a new term
	 * @param other The term to multiply by
	 * @return A new Term
	 */
	public Term multiply(Term other) {
		InputValidation.begin().when(other == null, "argument is null").validate();
		BigInteger n = numerator.multiply(other.numerator);
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d);
	}
	
	/**
	 * Multiply this term by a {@link BigInteger} and return a new term
	 * @param val The number to multiply by
	 * @return A new Term
	 */
	public Term multiply(BigInteger val) {
		InputValidation.begin().when(val == null, "argument is null").validate();
		BigInteger n = numerator.multiply(val);
		BigInteger d = denominator;
		return new Term(n, d);
	}
	
	/**
	 * Return the modulus of this {@link Term} by a {@link BigInteger} as a new {@link Term}
	 * @param m The modulus
	 * @return A new {@link Term}
	 */
	public Term mod(BigInteger m) {
		InputValidation.begin().when(m == null, "argument is null").validate();
		m = m.multiply(denominator);
		return new Term(numerator.mod(m), denominator);
	}
	
	/**
	 * Returns whether this term is a whole number (denominator == 1)
	 * @return {@code true} for a whole number, {@code false} for a fraction
	 */
	public boolean isWhole() {
		return denominator.equals(BigInteger.ONE);
	}
	
	/**
	 * Returns the whole-number value of this term.  Throws an exception
	 * if this term is not a whole number.
	 * @return The value of this term, if a whole number
	 * @throws ArithmeticException If this term is not a whole number.
	 */
	public BigInteger whole() throws ArithmeticException {
		if(!denominator.equals(BigInteger.ONE))
			throw new ArithmeticException("Cannot get whole value of fraction");
		return numerator;
	}
}
