package org.secretsharing;

import java.math.BigInteger;
import java.util.Arrays;

public class TermPolynomial implements Polynomial {
	public static final TermPolynomial ZERO = new TermPolynomial(new Term[0]);
	public static final TermPolynomial ONE = new TermPolynomial(new Term[] {Term.ONE});
	
	private Term[] terms;
	
	public TermPolynomial(Term[] terms) {
		this.terms = Arrays.copyOf(terms, terms.length);
	}
	
	public Term[] getTerms() {
		return Arrays.copyOf(terms, terms.length);
	}
	
	public Term y(BigInteger x) {
		Term result = Term.ZERO;
		Term[] terms = getTerms();
		for(int i = 0; i < terms.length; i++)
			result = result.add(terms[i].multiply(x.pow(i)));
		return result;
	}
	
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
	
	public TermPolynomial powX(int powx) {
		Term[] t = new Term[terms.length + powx];
		Arrays.fill(t, Term.ZERO);
		System.arraycopy(terms, 0, t, powx, terms.length);
		return new TermPolynomial(t);
	}
	
	public TermPolynomial multiply(Term term, int powx) {
		Term[] t = getTerms();
		for(int i = 0; i < t.length; i++)
			t[i] = t[i].multiply(term);
		return new TermPolynomial(t).powX(powx);
	}
	
	public TermPolynomial multiply(TermPolynomial other) {
		TermPolynomial result = TermPolynomial.ZERO;
		Term[] terms = getTerms();
		
		for(int i = 0; i < terms.length; i++) {
			result = result.add(other.multiply(terms[i], i));
		}
		
		return result;
	}
	
	public TermPolynomial multiply(BigInteger val) {
		Term[] terms = getTerms();
		for(int i = 0; i < terms.length; i++)
			terms[i] = terms[i].multiply(val);
		return new TermPolynomial(terms);
	}
}
