package org.secretsharing;

import java.math.BigInteger;

import org.secretsharing.util.BytesReadable;
import org.secretsharing.util.Checksum;

public enum SecretPartParser {
	V0 {

		@Override
		public SecretPart parse(String s) {
			String[] f = s.split("//");
			BytesReadable r;
			
			r = new BytesReadable(f[0].replace("-", ""));
			int length = r.readInt();
			BigInteger modulus = r.readBigInteger();
		
			r = new BytesReadable(f[1].replace("-", ""));
			BigInteger x = r.readBigInteger();
			BigInteger y = r.readBigInteger();
			Checksum cx = new Checksum(r);
			
			BigPoint point = new BigPoint(x, y);
			if(!SecretPart.PrivateSecretPart.pcx(point).equals(cx))
				throw new IllegalArgumentException("Checksum mismatch");
			
			return new SecretPart(length, -1, modulus, point);
		}
		
	},
	
	V1 {

		@Override
		public SecretPart parse(String s) {
			String[] f = s.split("//");
			BytesReadable r;
			
			r = new BytesReadable(f[0].replace("-", ""));
			int length = r.readInt();
			int requiredParts = r.readInt();
			BigInteger modulus = r.readBigInteger();
		
			r = new BytesReadable(f[1].replace("-", ""));
			BigInteger x = r.readBigInteger();
			BigInteger y = r.readBigInteger();
			Checksum cx = new Checksum(r);
			
			BigPoint point = new BigPoint(x, y);
			if(!SecretPart.PrivateSecretPart.pcx(point).equals(cx))
				throw new IllegalArgumentException("Checksum mismatch");
			
			return new SecretPart(length, requiredParts, modulus, point);
		}
		
	}
	;
	
	public abstract SecretPart parse(String s);
}
