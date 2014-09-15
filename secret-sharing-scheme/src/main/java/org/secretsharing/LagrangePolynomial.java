package org.secretsharing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

public class LagrangePolynomial {
	private static final MathContext CTX = new MathContext(128, RoundingMode.HALF_EVEN);
	
	private BigDecimal[] x = new BigDecimal[0];
	private BigDecimal[] y = new BigDecimal[0];
	
	public void add(BigDecimal x, BigDecimal y) {
		this.x = Arrays.copyOf(this.x, this.x.length + 1);
		this.x[this.x.length-1] = x;
		
		this.y = Arrays.copyOf(this.y, this.y.length + 1);
		this.y[this.y.length-1] = y;
	}
	
	public BigDecimal y(BigDecimal x) {
		BigDecimal y = BigDecimal.ZERO;
		for(int j = 0; j < this.x.length; j++)
			y = y.add(term(j, x), CTX);
		return y;
	}
	
	private BigDecimal term(int j, BigDecimal x) {
		BigDecimal t = y[j];
		for(int i = 0; i < this.x.length; i++) {
			if(i == j)
				continue;
			t = t.multiply(x.subtract(this.x[i], CTX), CTX);
		}
		BigDecimal d = BigDecimal.ONE;
		for(int i = 0; i < this.x.length; i++) {
			if(i == j)
				continue;
			d = d.multiply(this.x[j].subtract(this.x[i], CTX), CTX);
		}
		return t.divide(d, CTX);
	}
	
}
