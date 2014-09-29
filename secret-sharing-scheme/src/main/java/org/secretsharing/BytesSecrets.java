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

public class BytesSecrets {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	private static void write(BigInteger i, DataOutput data) throws IOException {
		byte[] b = i.toByteArray();
		data.writeInt(b.length);
		data.write(b);
	}
	
	private static BigInteger read(DataInput data) throws IOException {
		int len = data.readInt();
		byte[] b = new byte[len];
		data.readFully(b);
		return new BigInteger(b);
	}
	
	public static byte[][] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		BigInteger secretBits = BigInteger.valueOf(secret.length * 8);
		SecretPolynomial poly = new SecretPolynomial(new BigInteger(secret), secretBits.intValue(), requiredParts-1, rnd);
		BigPoint[] pts = poly.p(totalParts, secretBits.intValue());
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
				secretLength = sl.intValue();
			else if(!secretLength.equals(sl.intValue()))
				throw new IllegalArgumentException();
			if(prime == null)
				prime = p;
			else if(!prime.equals(p))
				throw new IllegalArgumentException();
			pts[i] = new BigPoint(x, y);
		}
		LagrangePolynomial poly = new LagrangePolynomial(pts);
		byte[] secret = poly.y(BigInteger.ZERO).getNumerator().mod(prime).add(prime).mod(prime).toByteArray();
		byte[] ret = new byte[secretLength];
		System.arraycopy(secret, 0, ret, ret.length - secret.length, secret.length);
		return ret;
	}
	
	private BytesSecrets() {}
}
