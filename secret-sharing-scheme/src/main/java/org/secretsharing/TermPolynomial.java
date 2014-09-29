package org.secretsharing;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * A polynomial composed of terms
 * @author robin
 *
 */
public class TermPolynomial {
	/**
	 * Constant zero-valued polynomial
	 */
	public static final TermPolynomial ZERO = new TermPolynomial(new Term[] {Term.ZERO});
	/**
	 * Constant one-valued polynomial
	 */
	public static final TermPolynomial ONE = new TermPolynomial(new Term[] {Term.ONE});
	
	/**
	 * The terms in this polynomial.  The ith element in the array
	 * is multiplied by x^i in the polynomial
	 */
	private Term[] terms;
	
	protected TermPolynomial() {}
	
	/**
	 * Create a new {@link TermPolynomial} from an array of terms.
	 * @param terms
	 */
	public TermPolynomial(Term[] terms) {
		this.terms = Arrays.copyOf(terms, terms.length);
	}
	
	@Override
	public String toString() {
		if(terms.length == 0)
			return "0";
		StringBuilder sb = new StringBuilder(terms[0].toString());
		for(int i = 1; i < terms.length; i++)
			sb.append(" + " + terms[i] + "x^" + i);
		return sb.toString();
	}
	
	/**
	 * Return the terms in this polynomial
	 * @return
	 */
	public Term[] getTerms() {
		return Arrays.copyOf(terms, terms.length);
	}
	
	/**
	 * Set the terms in this polynomial
	 * @param terms
	 */
	protected void setTerms(Term[] terms) {
		this.terms = Arrays.copyOf(terms, terms.length);
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
		return result.simplify();
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
	 * @param powx
	 * @return
	 */
	public TermPolynomial powX(int powx) {
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
}
