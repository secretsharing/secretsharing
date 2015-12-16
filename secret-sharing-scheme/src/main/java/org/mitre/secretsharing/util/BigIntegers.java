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

package org.mitre.secretsharing.util;

import java.math.BigInteger;
import java.util.Random;

/**
 * Utility class for dealing with {@link BigInteger}s
 * @author Robin Kirkman
 *
 */
public abstract class BigIntegers {
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
	
	/**
	 * Create an array of {@link BigInteger} with size {@code size}
	 * composed of random numbers ranging from {@code from} (inclusive)
	 * to {@code to} (exclusive), with {@code bits} bits of randomness.
	 * @param from The lower bound of the random numbers, inclusive
	 * @param to The upper bound of the random numbers, exclusive
	 * @param rnd The source of randomness
	 * @param size The size of the returned array
	 * @param bits The number of bits of randomness
	 * @return
	 */
	public static BigInteger[] random(BigInteger from, BigInteger to, Random rnd, int size, int bits) {
		BigInteger range = to.subtract(from);
		BigInteger bitmax = BigInteger.ONE.shiftLeft(bits);
		BigInteger[] bigs = new BigInteger[size];
		for(int i = 0; i < bigs.length; i++)
			bigs[i] = new BigInteger(bits, rnd).multiply(range).divide(bitmax).add(from);
		return bigs;
	}
	
	private BigIntegers() {}
}
