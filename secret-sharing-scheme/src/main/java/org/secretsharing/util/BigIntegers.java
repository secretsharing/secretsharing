package org.secretsharing.util;

import java.math.BigInteger;

/**
 * Utility class for dealing with {@link BigInteger}s
 * @author robin
 *
 */
public class BigIntegers {
	/**
	 * Create an array of {@link BigInteger} ranging in value
	 * from {@code from} (inclusive) to {@code to} (exclusive)
	 * @param from
	 * @param to
	 * @return
	 */
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
