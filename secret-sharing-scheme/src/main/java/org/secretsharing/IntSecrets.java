package org.secretsharing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;

public class IntSecrets {
	private static SecureRandom random = new SecureRandom();
	
	private static BigDecimal toY(int y) {
		return new BigDecimal(y);
	}
	
	private static int fromY(BigDecimal y) {
		return y.round(MathContext.DECIMAL128).intValue();
	}
	
	public static BigDecimal[][] split(int secret, int totalParts, int requiredParts) {
		LagrangePolynomial poly = new LagrangePolynomial();
		
		poly.add(BigDecimal.ZERO, toY(secret));
		
		for(int i = 1; i < requiredParts; i++)
			poly.add(new BigDecimal(random.nextInt()), toY(random.nextInt()));
		
		BigDecimal[][] ret = new BigDecimal[totalParts][];
		for(int i = 0; i < totalParts; i++) {
			BigDecimal x = new BigDecimal(random.nextInt());
			BigDecimal y = poly.y(x);
			ret[i] = new BigDecimal[] {x, y};
		}
			
		return ret;
	}
	
	public static int join(BigDecimal[][] parts) {
		LagrangePolynomial poly = new LagrangePolynomial();
		
		for(BigDecimal[] part : parts)
			poly.add(part[0], part[1]);
		
		return fromY(poly.y(BigDecimal.ZERO));
	}
	
	private IntSecrets() {}
}
