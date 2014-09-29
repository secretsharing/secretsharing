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

/**
 * Utility class for splitting and joining secret and secret parts
 * @author robin
 *
 */
public class BytesSecrets {
	/**
	 * Write a {@link BigInteger} with a minimal size
	 * @param i
	 * @param data
	 * @throws IOException
	 */
	private static void write(BigInteger i, DataOutput data) throws IOException {
		byte[] b = i.toByteArray();
		int len = b.length;
		do {
			boolean term = (len & ~0x7f) == 0;
			data.write((len & 0x7f) | (term ? 0x80 : 0));
			len = len >>> 7;
		} while(len != 0);
		data.write(b);
	}
	
	/**
	 * Read a {@link BigInteger} written by {@link #write(BigInteger, DataOutput)}
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private static BigInteger read(DataInput data) throws IOException {
		int len = 0;
		int off = 0;
		boolean term;
		do {
			int l = data.readUnsignedByte();
			term = (l & 0x80) != 0;
			len |= (l & 0x7f) << off;
			off += 7;
		} while(!term);
		byte[] b = new byte[len];
		data.readFully(b);
		return new BigInteger(b);
	}
	
	/**
	 * Split a secret into a number of parts
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return
	 */
	public static byte[][] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		BigInteger secretBits = BigInteger.valueOf(secret.length * 8);
		SecretPolynomial poly = new SecretPolynomial(new BigInteger(secret), secretBits.intValue(), requiredParts-1, rnd);
		BigPoint[] pts = poly.p(totalParts);
		byte[][] s = new byte[totalParts][];
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(buf);
		for(int i = 0; i < totalParts; i++) {
			try {
				write(secretBits, data);
				write(poly.getPrime(), data);
				write(pts[i].getX(), data);
				write(pts[i].getY(), data);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			s[i] = buf.toByteArray();
			buf.reset();
		}
		return s;
	}
	
	/**
	 * Recover a secret from its parts
	 * @param parts
	 * @return
	 */
	public static byte[] join(byte[][] parts) {
		BigPoint[] pts = new BigPoint[parts.length];
		Integer secretLength = null;
		BigInteger prime = null;
		for(int i = 0; i < pts.length; i++) {
			ByteArrayInputStream buf = new ByteArrayInputStream(parts[i]);
			DataInputStream data = new DataInputStream(buf);
			
			BigInteger sl;
			BigInteger p;
			BigInteger x;
			BigInteger y;
			try {
				sl = read(data);
				p = read(data);
				x = read(data);
				y = read(data);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			
			if(secretLength == null)
				secretLength = sl.intValue() / 8;
			else if(!secretLength.equals(sl.intValue() / 8))
				throw new IllegalArgumentException();
			if(prime == null)
				prime = p;
			else if(!prime.equals(p))
				throw new IllegalArgumentException();
			pts[i] = new BigPoint(x, y);
		}
		LagrangePolynomial poly = new LagrangePolynomial(pts, prime);
		byte[] secret = poly.y(BigInteger.ZERO).getNumerator().mod(prime).add(prime).mod(prime).toByteArray();
		byte[] ret = new byte[secretLength];
		System.arraycopy(secret, 0, ret, ret.length - secret.length, secret.length);
		return ret;
	}
	
	private BytesSecrets() {}
}
