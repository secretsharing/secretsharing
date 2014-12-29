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

/**
 * Utility class for dealing with {@link BigInteger}s
 * @author Robin Kirkman
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
