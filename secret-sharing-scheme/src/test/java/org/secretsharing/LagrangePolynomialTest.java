package org.secretsharing;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class LagrangePolynomialTest {
	@Test
	public void testLinear() {
		LagrangePolynomial poly = new LagrangePolynomial();
		
		poly.add(BigDecimal.ZERO, BigDecimal.ZERO);
		poly.add(BigDecimal.ONE, BigDecimal.ONE);
		
		
		Assert.assertEquals(BigDecimal.ZERO, poly.y(BigDecimal.ZERO));
		Assert.assertEquals(BigDecimal.ONE, poly.y(BigDecimal.ONE));
		Assert.assertEquals(BigDecimal.TEN, poly.y(BigDecimal.TEN));
	}
}
