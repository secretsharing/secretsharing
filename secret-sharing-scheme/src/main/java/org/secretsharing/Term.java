/*

Copyright (c) 2014, The MITRE Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
	in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived 
	from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package org.secretsharing;

import java.math.BigInteger;

/**
 * A fractional term used in {@link TermPolynomial}
 * @author robin
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
	 * @param val
	 */
	public Term(BigInteger val) {
		this(val, BigInteger.ONE);
	}
	
	/**
	 * Create a new term with the argument numerator and denominator
	 * @param numerator
	 * @param denominator
	 */
	public Term(BigInteger numerator, BigInteger denominator) {
		if(denominator.equals(BigInteger.ZERO))
			throw new ArithmeticException("Divide by zero in term");
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
		return subtract(o).numerator.compareTo(BigInteger.ZERO);
	}
	
	/**
	 * Return the numerator
	 * @return
	 */
	public BigInteger getNumerator() {
		return numerator;
	}

	/**
	 * Return the denominator
	 * @return
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
	 * @param other
	 * @return
	 */
	public Term add(Term other) {
		BigInteger n = numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d);
	}
	
	/**
	 * Subtract another term from this term and return a new term
	 * @param other
	 * @return
	 */
	public Term subtract(Term other) {
		return add(new Term(other.numerator.negate(), other.denominator));
	}
	
	/**
	 * Multiply this term by another term and return a new term
	 * @param other
	 * @return
	 */
	public Term multiply(Term other) {
		BigInteger n = numerator.multiply(other.numerator);
		BigInteger d = denominator.multiply(other.denominator);
		return new Term(n, d);
	}
	
	/**
	 * Multiply this term by a {@link BigInteger} and return a new term
	 * @param val
	 * @return
	 */
	public Term multiply(BigInteger val) {
		BigInteger n = numerator.multiply(val);
		BigInteger d = denominator;
		return new Term(n, d);
	}
	
	/**
	 * Returns whether this term is a whole number (denominator == 1)
	 * @return
	 */
	public boolean isWhole() {
		return denominator.equals(BigInteger.ONE);
	}
	
	/**
	 * Returns the whole-number value of this term.  Throws an exception
	 * if this term is not a whole number.
	 * @return
	 * @throws ArithmeticException If this term is not a whole number.
	 */
	public BigInteger whole() throws ArithmeticException {
		if(!denominator.equals(BigInteger.ONE))
			throw new ArithmeticException("Cannot get whole value of fraction");
		return numerator;
	}
}
