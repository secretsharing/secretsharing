package org.secretsharing;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Random;

public class BytesSecrets {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Random rnd = new SecureRandom();
	
	public static byte[][] split(byte[] secret, int totalParts, int requiredParts) {
		int secretBits = secret.length * 8;
		SecretPolynomial poly = new SecretPolynomial(new BigInteger(secret), secretBits, requiredParts-1);
		BigPoint[] pts = poly.p(totalParts, secretBits);
		byte[][] s = new byte[totalParts][];
		for(int i = 0; i < totalParts; i++) {
			s[i] = (secret.length + "," + poly.getPrime() + "," + pts[i]).getBytes(UTF8);
		}
		return s;
	}
	
	public static byte[] join(byte[][] parts) {
		BigPoint[] pts = new BigPoint[parts.length];
		Integer secretLength = null;
		BigInteger prime = null;
		for(int i = 0; i < pts.length; i++) {
			String[] s = new String(parts[i], UTF8).split(",");
			if(secretLength == null)
				secretLength = Integer.parseInt(s[0]);
			else if(!secretLength.equals(Integer.parseInt(s[0])))
				throw new IllegalArgumentException();
			if(prime == null)
				prime = new BigInteger(s[1]);
			else if(!prime.equals(new BigInteger(s[1])))
				throw new IllegalArgumentException();
			pts[i] = new BigPoint(new BigInteger(s[2]), new BigInteger(s[3]));
		}
		LagrangePolynomial poly = new LagrangePolynomial(pts);
		byte[] secret = poly.y(BigInteger.ZERO).getNumerator().mod(prime).add(prime).mod(prime).toByteArray();
		byte[] ret = new byte[secretLength];
		System.arraycopy(secret, 0, ret, ret.length - secret.length, secret.length);
		return ret;
	}
	
	private BytesSecrets() {}
}
