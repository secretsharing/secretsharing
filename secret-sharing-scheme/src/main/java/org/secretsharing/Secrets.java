package org.secretsharing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Random;

import org.secretsharing.util.BigIntegers;

/**
 * Utility class for splitting and joining secret and secret parts
 * @author robin
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
	public static SecretPart[] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		int secretBytes = secret.length;
		TermPolynomial poly = new TermPolynomial(new BigInteger(secret), secretBytes * 8, requiredParts-1, rnd);
		BigPoint[] pts = poly.p(BigIntegers.range(1, totalParts + 1));
		SecretPart[] s = new SecretPart[totalParts];
		for(int i = 0; i < totalParts; i++)
			s[i] = new SecretPart(secretBytes, requiredParts, poly.getModulus(), pts[i]);
		return s;
	}
	
	/**
	 * Recover a secret from its parts
	 * @param parts
	 * @return
	 */
	public static byte[] join(SecretPart[] parts) {
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
