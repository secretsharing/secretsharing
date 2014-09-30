package org.secretsharing.util;

import java.math.BigInteger;

public class BigIntegers {
	
	public static BigInteger[] range(int from, int to) {
		if(from > to)
			throw new IllegalArgumentException();
		BigInteger[] bigs = new BigInteger[to - from];
		for(int i = 0; i < bigs.length; i++)
			bigs[i] = BigInteger.valueOf(from + i);
		return bigs;
	}
	
	private BigIntegers() {}
}
