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
import java.util.Arrays;
import java.util.Random;

import org.mitre.secretsharing.util.BigIntegers;

/**
 * Utility class for splitting and joining secret and secret parts
 * @author Robin Kirkman
 *
 */
public class Secrets {
	public static Part[] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		return splitMultibyte(secret, totalParts, requiredParts, rnd);
	}
	
	/**
	 * Split a secret into a number of parts
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return
	 */
	public static Part[] splitMultibyte(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		int secretBytes = secret.length;
		int secretBits = secretBytes * 8;
		TermPolynomial poly = new TermPolynomial(new BigInteger(secret), secretBits, requiredParts-1, rnd);
		BigPoint[] pts = poly.p(BigIntegers.range(1, totalParts + 1));
		Part[] s = new Part[totalParts];
		for(int i = 0; i < totalParts; i++)
			s[i] = new Part(secretBytes, requiredParts, poly.getModulus(), pts[i]);
		return s;
	}
	
	public static PerBytePart[] splitPerByte(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		BigPoint[][] pts = new BigPoint[totalParts][secret.length];
		for(int i = 0; i < secret.length; i++) {
			TermPolynomial poly = TermPolynomial.ONE.multiply(BigInteger.valueOf(0xFF & secret[i]));
			for(int j = 0; j < requiredParts-1; j++)
				poly = poly.add(TermPolynomial.ONE.multiply(new BigInteger(16, rnd)).powX(j));
			poly = new TermPolynomial(poly.getTerms(), PerBytePart.MODULUS);
			for(int j = 0; j < totalParts; j++)
				pts[j][i] = poly.p(BigInteger.valueOf(j+1));
		}
		PerBytePart[] parts = new PerBytePart[totalParts];
		for(int j = 0; j < totalParts; j++) {
			byte[] b = new byte[1 + secret.length * 2];
			for(int i = 0; i < secret.length; i++) {
				short v = pts[j][i].getY().shortValue();
				b[2*i+1] = (byte)(v >>> 8);
				b[2*i+2] = (byte) v;
			}
			parts[j] = new PerBytePart(2, secret.length, requiredParts, new BigPoint(BigInteger.valueOf(j+1), new BigInteger(b)));
		}
		return parts;
	}
	
	public static byte[] join(Part[] parts) {
		return parts[0].join(Arrays.copyOfRange(parts, 1, parts.length));
	}

	/**
	 * Recover a secret from its parts
	 * @param parts
	 * @return
	 */
	public static byte[] joinMultibyte(Part[] parts) {
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
				throw new IllegalArgumentException("Inconsistent secret length among parts");
			if(requiredParts == null)
				requiredParts = parts[i].getRequiredParts();
			else if(!requiredParts.equals(parts[i].getRequiredParts()))
				throw new IllegalArgumentException("Inconsistent required parts number among parts");
			if(prime == null)
				prime = p;
			else if(!prime.equals(p))
				throw new IllegalArgumentException("Inconsistent prime modulus among parts");
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

	public static byte[] joinPerByte(PerBytePart[] parts) {
		Part.PublicSecretPart pub = parts[0].getPublicPart();
		byte[] secret = new byte[pub.getLength()];
		BigPoint[][] pts = new BigPoint[secret.length][parts.length];
		for(int i = 0; i < parts.length; i++) {
			byte[] b = parts[i].getPrivatePart().getPoint().getY().toByteArray();
			byte[] pb = new byte[secret.length * 2];
			for(int j = 0; j < b.length; j++)
				pb[j + (pb.length - b.length)] = b[j];
			for(int j = 0; j < secret.length; j++)
				pts[j][i] = new BigPoint(parts[i].getPrivatePart().getPoint().getX(), BigInteger.valueOf(((0xFF & pb[2*j]) << 8) | (0xFF & pb[2*j+1])));
		}
		
		for(int i = 0; i < secret.length; i++) {
			TermPolynomial poly = new TermPolynomial(pts[i], pub.getModulus());
			secret[i] = poly.y(BigInteger.ZERO).getNumerator().mod(pub.getModulus()).add(pub.getModulus()).mod(pub.getModulus()).byteValue();
		}
		
		return secret;
	}
	
	private Secrets() {}
}
