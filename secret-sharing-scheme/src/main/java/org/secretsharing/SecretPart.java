package org.secretsharing;

import java.math.BigInteger;

import org.secretsharing.util.BytesReadable;
import org.secretsharing.util.BytesWritable;
import org.secretsharing.util.Checksum;

/**
 * Utility class for holding split parts of a secret
 * @author robin
 *
 */
public class SecretPart {
	/**
	 * The current version of {@link SecretPart} written and stringified
	 */
	private static final int CURRENT_VERSION = 0;
	
	/**
	 * Insert dashes in the argument string
	 * @param s
	 * @return
	 */
	private static String dash(String s) {
		s = s.replaceAll("(......)", "$1-");
		if(s.endsWith("-"))
			s = s.substring(0, s.length()-1);
		return s;
	}
	
	/**
	 * The public components of a secret part.  These are the same
	 * for all parts of a secret.
	 * @author robin
	 *
	 */
	public static class PublicSecretPart {
		/**
		 * The number of bytes in the secret
		 */
		private int length;
		/**
		 * The prime modulo for the secret parts
		 */
		private BigInteger modulus;
		
		/**
		 * Create a new {@link PublicSecretPart}
		 * @param length
		 * @param modulus
		 */
		private PublicSecretPart(int length, BigInteger modulus) {
			if(modulus == null)
				throw new IllegalArgumentException();
			this.length = length;
			this.modulus = modulus;
		}
		
		/**
		 * Parse a string representation of a {@link PublicSecretPart}
		 * @param version
		 * @param s
		 */
		private PublicSecretPart(int version, String s) {
			if(s == null)
				throw new IllegalArgumentException();
			if(version == 0) {
				BytesReadable r = new BytesReadable(s.replace("-", ""));
				length = r.readInt();
				modulus = r.readBigInteger();
			} else
				throw new IllegalArgumentException("Invalid version:" + version);
		}
		
		/**
		 * Parse a byte representation of a {@link PublicSecretPart}
		 * @param version
		 * @param r
		 */
		private PublicSecretPart(int version, BytesReadable r) {
			if(r == null)
				throw new IllegalArgumentException();
			if(version == 0) {
				length = r.readInt();
				modulus = r.readBigInteger();
			} else
				throw new IllegalArgumentException("Invalid version:" + version);
		}

		/**
		 * The length (in bytes) of the secret
		 * @return
		 */
		public int getLength() {
			return length;
		}

		/**
		 * The prime modulus for the key parts
		 * @return
		 */
		public BigInteger getModulus() {
			return modulus;
		}
		
		@Override
		public String toString() {
			BytesWritable w = new BytesWritable();
			return dash(w.writeInt(length).writeBigInteger(modulus).reset());
		}
		
		/**
		 * Write this {@link PublicSecretPart} as bytes
		 * @param w
		 */
		private void toBytes(BytesWritable w) {
			w.writeInt(length);
			w.writeBigInteger(modulus);
		}
	}
	
	/**
	 * The private component of a secret part.  This is different for every secret part.
	 * @author robin
	 *
	 */
	public static class PrivateSecretPart {
		/**
		 * Compute the {@link Checksum} for a {@link BigPoint}
		 * @param point
		 * @return
		 */
		private static Checksum pcx(BigPoint point) {
			return new Checksum(new BytesWritable().writeBigInteger(point.getX()).writeBigInteger(point.getY()).toByteArray());
		}
		
		/**
		 * The point on the polynomial
		 */
		private BigPoint point;
		/**
		 * The checksum of that point
		 */
		private Checksum cx;
		
		/**
		 * Create a new {@link PrivateSecretPart}
		 * @param point
		 */
		private PrivateSecretPart(BigPoint point) {
			if(point == null)
				throw new IllegalArgumentException();
			this.point = point;
			cx = pcx(point);
		}
		
		/**
		 * Parse a string representation of a {@link PrivateSecretPart}
		 * @param version
		 * @param s
		 */
		private PrivateSecretPart(int version, String s) {
			if(version == 0) {
				BytesReadable r = new BytesReadable(s.replace("-", ""));
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				point = new BigPoint(x, y);
				cx = new Checksum(r);
				if(!cx.equals(pcx(point)))
					throw new IllegalArgumentException("Checksum mismatch");
			} else
				throw new IllegalArgumentException("Invalid version:" + version);
		}
		
		/**
		 * Parse a bytes representation of a {@link PrivateSecretPart}
		 * @param version
		 * @param r
		 */
		private PrivateSecretPart(int version, BytesReadable r) {
			if(version == 0) {
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				point = new BigPoint(x, y);
				cx = new Checksum(r);
				if(!cx.equals(pcx(point)))
					throw new IllegalArgumentException("Checksum mismatch");
			} else
				throw new IllegalArgumentException("Invalid version:" + version);
		}

		/**
		 * Return the point on the polynomial
		 * @return
		 */
		public BigPoint getPoint() {
			return point;
		}
		
		public Checksum getChecksum() {
			return cx;
		}
		
		@Override
		public String toString() {
			BytesWritable w = new BytesWritable()
				.writeBigInteger(point.getX())
				.writeBigInteger(point.getY());
			cx.write(w);
			return dash(w.reset());
		}
		
		/**
		 * Write the {@link PrivateSecretPart} to bytes
		 * @param w
		 * @return
		 */
		private BytesWritable toBytes(BytesWritable w) {
			w.writeBigInteger(point.getX()).writeBigInteger(point.getY());
			cx.write(w);
			return w;
		}
	}
	
	/**
	 * The format version of this {@link SecretPart}
	 */
	private int version;
	/**
	 * The public component of the part
	 */
	private PublicSecretPart publicPart;
	/**
	 * The private component of the part
	 */
	private PrivateSecretPart privatePart;
	
	/**
	 * Create a new {@link SecretPart}
	 * @param version
	 * @param publicPart
	 * @param privatePart
	 */
	public SecretPart(int version, PublicSecretPart publicPart, PrivateSecretPart privatePart) {
		this.version = version;
		this.publicPart = publicPart;
		this.privatePart = privatePart;
	}
	
	/**
	 * Create a new {@link SecretPart}
	 * @param length
	 * @param modulus
	 * @param point
	 */
	public SecretPart(int length, BigInteger modulus, BigPoint point) {
		this(CURRENT_VERSION, new PublicSecretPart(length, modulus), new PrivateSecretPart(point));
	}
	
	/**
	 * Parse a string representation of a {@link SecretPart}
	 * @param s
	 */
	public SecretPart(String s) {
		if(s == null)
			throw new IllegalArgumentException();
		String[] f = s.split("(:|//)");
		int version = new BytesReadable(f[0]).readInt();
		if(version > CURRENT_VERSION || version < 0)
			throw new IllegalArgumentException("Unknown secret part version:" + version);
		this.version = version;
		if(version == 0) {
			publicPart = new PublicSecretPart(version, f[1]);
			privatePart = new PrivateSecretPart(version, f[2]);
		} else
			throw new IllegalArgumentException("Invalid version:" + version);
		
	}
	
	/**
	 * Parse a bytes representation of a {@link SecretPart}
	 * @param b
	 */
	public SecretPart(byte[] b) {
		BytesReadable r = new BytesReadable(b);
		int version = r.readInt();
		if(version > CURRENT_VERSION || version < 0)
			throw new IllegalArgumentException("Unknown secret part version:" + version);
		this.version = version;
		if(version == 0) {
			publicPart = new PublicSecretPart(version, r);
			privatePart = new PrivateSecretPart(version, r);
		} else
			throw new IllegalArgumentException("Invalid version:" + version);
	}

	@Override
	public String toString() {
		BytesWritable w = new BytesWritable().writeInt(version);
		StringBuilder sb = new StringBuilder(w.reset());
		sb.append(":");
		sb.append(getPublicPart());
		sb.append("//");
		sb.append(getPrivatePart());
		return sb.toString();
	}
	
	/**
	 * Convert this {@link SecretPart} to bytes
	 * @return
	 */
	public byte[] toBytes() {
		BytesWritable w = new BytesWritable().writeInt(version);
		getPublicPart().toBytes(w);
		getPrivatePart().toBytes(w);
		return w.toByteArray();
	}

	/**
	 * Return the public part of this secret part
	 * @return
	 */
	public PublicSecretPart getPublicPart() {
		return publicPart;
	}

	/**
	 * Return the private part of this secret part
	 * @return
	 */
	public PrivateSecretPart getPrivatePart() {
		return privatePart;
	}
	
	/**
	 * Return the format version of this secret part
	 * @return
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Return the length in bytes of the secret
	 * @return
	 */
	public int getLength() {
		return getPublicPart().getLength();
	}
	
	/**
	 * Return the prime modulus for the secret parts
	 * @return
	 */
	public BigInteger getModulus() {
		return getPublicPart().getModulus();
	}
	
	/**
	 * Return the point on the polynomial
	 * @return
	 */
	public BigPoint getPoint() {
		return getPrivatePart().getPoint();
	}
	
	public Checksum getChecksum() {
		return getPrivatePart().getChecksum();
	}
}
