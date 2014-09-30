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
	private static final int CURRENT_VERSION = 1;
	
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
		 * The number of parts required to reconstruct the secret
		 */
		private int requiredParts = -1;
		/**
		 * The prime modulo for the secret parts
		 */
		private BigInteger modulus;
		
		/**
		 * Create a new {@link PublicSecretPart}
		 * @param length
		 * @param modulus
		 */
		private PublicSecretPart(int length, int requiredParts, BigInteger modulus) {
			if(modulus == null)
				throw new IllegalArgumentException();
			this.length = length;
			this.requiredParts = requiredParts;
			this.modulus = modulus;
		}
		
		/**
		 * The length (in bytes) of the secret
		 * @return
		 */
		public int getLength() {
			return length;
		}

		/**
		 * The number of parts required to reconstruct the secret
		 * @return
		 */
		public int getRequiredParts() {
			return requiredParts;
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
			return dash(w.writeInt(length).writeInt(requiredParts).writeBigInteger(modulus).reset());
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
		public static Checksum pcx(BigPoint point) {
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
	public SecretPart(int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(CURRENT_VERSION, new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}
	
	private SecretPart(SecretPart other) {
		this(other.getVersion(), other.getPublicPart(), other.getPrivatePart());
	}
	
	private static SecretPart parse(String s) {
		if(s == null)
			throw new IllegalArgumentException();
		String[] f = s.split(":");
		int version = new BytesReadable(f[0]).readInt();
		return SecretPartParser.values()[version].parse(f[1]);
	}
	
	/**
	 * Parse a string representation of a {@link SecretPart}
	 * @param s
	 */
	public SecretPart(String s) {
		this(parse(s));
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
	
	public int getRequiredParts() {
		return getPublicPart().getRequiredParts();
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
