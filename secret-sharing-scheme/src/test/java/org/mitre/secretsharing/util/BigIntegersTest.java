package org.mitre.secretsharing.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class BigIntegersTest {
	@Test
	public void testRandom() {
		BigInteger[] bigs = BigIntegers.random(
				BigInteger.ZERO,
				BigInteger.TEN,
				new Random(),
				1000,
				4);
		Set<BigInteger> numerals = new HashSet<>(Arrays.asList(bigs));
		Set<BigInteger> expected = new HashSet<>(Arrays.asList(
				BigInteger.valueOf(0),
				BigInteger.valueOf(1),
				BigInteger.valueOf(2),
				BigInteger.valueOf(3),
				BigInteger.valueOf(4),
				BigInteger.valueOf(5),
				BigInteger.valueOf(6),
				BigInteger.valueOf(7),
				BigInteger.valueOf(8),
				BigInteger.valueOf(9)
				));
		Assert.assertEquals(expected, numerals);
	}
}
