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

package org.mitre.secretsharing;

import java.math.BigInteger;
import java.util.Random;

import org.mitre.secretsharing.util.BigIntegers;

/**
 * Utility class for splitting and joining secret and secret parts
 * @author Robin Kirkman
 *
 */
public class Secrets {
	/**
	 * Split a secret into a number of parts
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return
	 */
	public static Part[] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		int secretBytes = secret.length;
		TermPolynomial poly = new TermPolynomial(new BigInteger(secret), secretBytes * 8, requiredParts-1, rnd);
		BigPoint[] pts = poly.p(BigIntegers.range(1, totalParts + 1));
		Part[] s = new Part[totalParts];
		for(int i = 0; i < totalParts; i++)
			s[i] = new Part(secretBytes, requiredParts, poly.getModulus(), pts[i]);
		return s;
	}

	/**
	 * Reconstruct the secret polynomial from a number of parts
	 * @param parts The parts with which to reconstruct the polynomial
	 * @return The reconstructed secret polynomial
	 */
	public static TermPolynomial polynomialOf(Part[] parts) {
		BigPoint[] pts = new BigPoint[parts.length];
		Integer secretLength = null;
		Integer requiredParts = null;
		BigInteger prime = null;
		for(int i = 0; i < pts.length; i++) {
			int sl = parts[i].getLength();
			BigInteger p = parts[i].getModulus();

			if(secretLength == null)
				secretLength = sl;
			else if(!secretLength.equals(sl))
				throw new IllegalArgumentException();
			if(requiredParts == null)
				requiredParts = parts[i].getRequiredParts();
			else if(!requiredParts.equals(parts[i].getRequiredParts()))
				throw new IllegalArgumentException();
			if(prime == null)
				prime = p;
			else if(!prime.equals(p))
				throw new IllegalArgumentException();
			pts[i] = parts[i].getPoint();
		}
		if(requiredParts > 0 && parts.length < requiredParts)
			throw new IllegalArgumentException(requiredParts + " parts are required but only " + parts.length + " supplied");
		return new TermPolynomial(pts, prime);
	}

	/**
	 * Recover a secret from its parts
	 * @param parts
	 * @return
	 */
	public static byte[] join(Part[] parts) {
		BigPoint[] pts = new BigPoint[parts.length];
		Integer secretLength = null;
		Integer requiredParts = null;
		BigInteger prime = null;
		for(int i = 0; i < pts.length; i++) {
			int sl = parts[i].getLength();
			BigInteger p = parts[i].getModulus();

			if(secretLength == null)
				secretLength = sl;
			else if(!secretLength.equals(sl))
				throw new IllegalArgumentException();
			if(requiredParts == null)
				requiredParts = parts[i].getRequiredParts();
			else if(!requiredParts.equals(parts[i].getRequiredParts()))
				throw new IllegalArgumentException();
			if(prime == null)
				prime = p;
			else if(!prime.equals(p))
				throw new IllegalArgumentException();
			pts[i] = parts[i].getPoint();
		}
		if(requiredParts > 0 && parts.length < requiredParts)
			throw new IllegalArgumentException(requiredParts + " parts are required but only " + parts.length + " supplied");
		TermPolynomial poly = new TermPolynomial(pts, prime);
		byte[] secret = poly.y(BigInteger.ZERO).getNumerator().mod(prime).add(prime).mod(prime).toByteArray();
		byte[] ret = new byte[secretLength];
		System.arraycopy(secret, 0, ret, ret.length - secret.length, secret.length);
		return ret;
	}

	private Secrets() {}
}
