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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.mitre.secretsharing.util.BigIntegers;
import org.mitre.secretsharing.util.InputValidation;

/**
 * Utility class for splitting and joining secret and secret parts
 * @author Robin Kirkman
 *
 */
public abstract class Secrets {
	/**
	 * Split a secret into a number of parts, using {@link #splitMultibyte(byte[], int, int, Random)}.
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return An array of secret {@link Part}s
	 * @see #splitMultibyte(byte[], int, int, Random)
	 */
	public static Part[] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		return splitMultibyte(secret, totalParts, requiredParts, rnd);
	}
	
	/**
	 * Split a secret into a number of parts by treating the secret byte array as a single Y coordinate
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return An array of secret {@link Part}s
	 */
	public static Part[] splitMultibyte(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		InputValidation.begin()
			.when(secret == null, "secret is null")
			.when(totalParts < 1, "totalParts is less than 1")
			.when(requiredParts > totalParts, "requiredParts is greater than totalParts")
			.when(rnd == null, "rnd is null")
			.validate();
		int secretBytes = secret.length;
		int secretBits = secretBytes * 8;
		TermPolynomial poly = new TermPolynomial(new BigInteger(secret), secretBits, requiredParts-1, rnd);
		BigPoint[] pts = poly.p(BigIntegers.range(1, totalParts + 1));
		Part[] s = new Part[totalParts];
		for(int i = 0; i < totalParts; i++)
			s[i] = new Part(secretBytes, requiredParts, poly.getModulus(), pts[i]);
		return s;
	}
	
	/**
	 * Split a secret into a number of parts by treating the secret byte array as individual secrets of 1 byte each
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return An array of secret {@link Part}s
	 */
	public static PerBytePart[] splitPerByte(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		InputValidation.begin()
			.when(secret == null, "secret is null")
			.when(totalParts < 1, "totalParts is less than 1")
			.when(totalParts > PerBytePart.MAX_PARTS, "totalParts is greater than " + PerBytePart.MAX_PARTS)
			.when(requiredParts > totalParts, "requiredParts is greater than totalParts")
			.when(rnd == null, "rnd is null")
			.validate();
		List<Integer> xs = new ArrayList<Integer>();
		for(int i = 1; i < PerBytePart.MODULUS.intValue(); i++)
			xs.add(i);
		BigPoint[][] pts = new BigPoint[totalParts][secret.length];
		BigInteger[] x = new BigInteger[totalParts];
		for(int j = 0; j < x.length; j++)
			x[j] = BigInteger.valueOf(xs.remove((int)(rnd.nextDouble() * xs.size())));
		for(int i = 0; i < secret.length; i++) {
			TermPolynomial poly = TermPolynomial.ONE.multiply(BigInteger.valueOf(0xFF & secret[i]));
			for(int j = 0; j < requiredParts-1; j++)
				poly = poly.add(TermPolynomial.ONE.multiply(BigInteger.valueOf((long)(PerBytePart.MODULUS.longValue() * rnd.nextDouble()))).powX(j+1));
			poly = new TermPolynomial(poly.getTerms(), PerBytePart.MODULUS);
			for(int j = 0; j < totalParts; j++) {
				pts[j][i] = poly.p(x[j]);
			}
		}
		PerBytePart[] parts = new PerBytePart[totalParts];
		for(int j = 0; j < totalParts; j++) {
			byte[] b = new byte[1 + secret.length * 2];
			for(int i = 0; i < secret.length; i++) {
				short v = pts[j][i].getY().shortValue();
				b[2*i+1] = (byte)(v >>> 8);
				b[2*i+2] = (byte) v;
			}
			parts[j] = new PerBytePart(2, secret.length, requiredParts, new BigPoint(x[j], new BigInteger(b)));
		}
		return parts;
	}
	
	/**
	 * Join {@link Part}s of a secret back into a byte array.
	 * Calls {@link Part#join(Part...)} on the first element in
	 * the array, which is overridden by {@link PerBytePart#join(Part...)}
	 * @param parts The array of parts to join, of length at least one
	 * @return The reconstructed secret byte array
	 */
	public static byte[] join(Part[] parts) {
		InputValidation.begin()
			.when(parts == null, "parts array is null")
			.when(parts != null && parts.length == 0, "parts array is empty")
			.validate();
		return parts[0].join(Arrays.copyOfRange(parts, 1, parts.length));
	}

	/**
	 * Recover a secret from an array of {@link Part}s
	 * @param parts The array of secret parts
	 * @return The recovered secret
	 */
	public static byte[] joinMultibyte(Part[] parts) {
		InputValidation iv = InputValidation.begin()
			.when(parts == null, "parts array is null")
			.when(parts != null && parts.length == 0, "parts array is empty")
			.validate();
		
		Integer secretLength = null;
		Integer requiredParts = null;
		BigInteger prime = null;
		for(Part part : parts) {
			secretLength = (secretLength == null ? part.getLength() : secretLength);
			requiredParts = (requiredParts == null ? part.getRequiredParts() : requiredParts);
			prime = (prime == null ? part.getModulus() : prime);

			iv.when(part instanceof PerBytePart, "perbyte parts cannot be used for multibyte join");
			iv.when(part.getLength() != secretLength, "inconsistent secret lengths");
			iv.when(part.getRequiredParts() != requiredParts, "inconsistent number of required parts");
			iv.when(!part.getModulus().equals(prime), "inconsistent moduli");
		}
		iv.validate()
			.when(parts.length < requiredParts, requiredParts + " parts required but " + parts.length + " parts provided")
			.validate();
		
		BigPoint[] pts = new BigPoint[parts.length];
		for(int i = 0; i < pts.length; i++) {
			pts[i] = parts[i].getPoint();
		}
		
		TermPolynomial poly = new TermPolynomial(pts, prime);
		byte[] secret = poly.y(BigInteger.ZERO).getNumerator().mod(prime).add(prime).mod(prime).toByteArray();
		byte[] ret = new byte[secretLength];
		System.arraycopy(secret, 0, ret, ret.length - secret.length, secret.length);
		return ret;
	}

	/**
	 * Recover a per-byte secret from an array of {@link PerBytePart}s
	 * @param parts The secret parts
	 * @return The recovered secret
	 */
	public static byte[] joinPerByte(PerBytePart[] parts) {
		InputValidation iv = InputValidation.begin()
				.when(parts == null, "parts array is null")
				.when(parts != null && parts.length == 0, "parts array is empty")
				.validate();
			
		Part.PublicSecretPart pub = parts[0].getPublicPart();
		byte[] secret = new byte[pub.getLength()];
		BigPoint[][] pts = new BigPoint[secret.length][parts.length];
		Integer secretLength = null;
		Integer requiredParts = null;
		for(Part part : parts) {
			secretLength = (secretLength == null ? part.getLength() : secretLength);
			requiredParts = (requiredParts == null ? part.getRequiredParts() : requiredParts);

			iv.when(!(part instanceof PerBytePart), "multibyte parts cannot be used for perbyte join");
			iv.when(part.getLength() != secretLength, "inconsistent secret lengths");
			iv.when(part.getRequiredParts() != requiredParts, "inconsistent number of required parts");
			iv.when(!part.getModulus().equals(PerBytePart.MODULUS), "inconsistent moduli");
		}
		iv.validate()
			.when(parts.length < requiredParts, requiredParts + " parts required but " + parts.length + " parts provided")
			.validate();
		
		for(int i = 0; i < parts.length; i++) {
			byte[] b = parts[i].getPrivatePart().getPoint().getY().toByteArray();
			byte[] pb = new byte[secret.length * 2];
			if(b.length > pb.length)
				pb = Arrays.copyOfRange(b, b.length - pb.length, b.length);
			else
				System.arraycopy(b, 0, pb, pb.length - b.length, b.length);
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
