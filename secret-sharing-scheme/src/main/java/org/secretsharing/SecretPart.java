package org.secretsharing;

import java.math.BigInteger;

import org.secretsharing.util.BytesReadable;
import org.secretsharing.util.BytesWritable;
import org.secretsharing.util.Checksum;

public class SecretPart {
	private static final int CURRENT_VERSION = 0;
	
	private static String dash(String s) {
		s = s.replaceAll("(......)", "$1-");
		if(s.endsWith("-"))
			s = s.substring(0, s.length()-1);
		return s;
	}
	
	public static class PublicSecretPart {
		private int length;
		private BigInteger modulus;
		
		private PublicSecretPart(int length, BigInteger modulus) {
			if(modulus == null)
				throw new IllegalArgumentException();
			this.length = length;
			this.modulus = modulus;
		}
		
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
		
		private PublicSecretPart(int version, BytesReadable r) {
			if(r == null)
				throw new IllegalArgumentException();
			if(version == 0) {
				length = r.readInt();
				modulus = r.readBigInteger();
			} else
				throw new IllegalArgumentException("Invalid version:" + version);
		}

		public int getLength() {
			return length;
		}

		public BigInteger getModulus() {
			return modulus;
		}
		
		@Override
		public String toString() {
			BytesWritable w = new BytesWritable();
			return dash(w.writeInt(length).writeBigInteger(modulus).reset());
		}
		
		private void toBytes(BytesWritable w) {
			w.writeInt(length);
			w.writeBigInteger(modulus);
		}
	}
	
	public static class PrivateSecretPart {
		private static Checksum pcx(BigPoint point) {
			return new Checksum(new BytesWritable().writeBigInteger(point.getX()).writeBigInteger(point.getY()).toByteArray());
		}
		
		private BigPoint point;
		private Checksum cx;
		
		private PrivateSecretPart(BigPoint point) {
			if(point == null)
				throw new IllegalArgumentException();
			this.point = point;
			cx = pcx(point);
		}
		
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

		public BigPoint getPoint() {
			return point;
		}
		
		@Override
		public String toString() {
			BytesWritable w = new BytesWritable()
				.writeBigInteger(point.getX())
				.writeBigInteger(point.getY());
			cx.write(w);
			return dash(w.reset());
		}
		
		private BytesWritable toBytes(BytesWritable w) {
			w.writeBigInteger(point.getX()).writeBigInteger(point.getY());
			cx.write(w);
			return w;
		}
	}
	
	private int version;
	private PublicSecretPart publicPart;
	private PrivateSecretPart privatePart;
	
	public SecretPart(int version, PublicSecretPart publicPart, PrivateSecretPart privatePart) {
		this.version = version;
		this.publicPart = publicPart;
		this.privatePart = privatePart;
	}
	
	public SecretPart(int length, BigInteger modulus, BigPoint point) {
		this(CURRENT_VERSION, new PublicSecretPart(length, modulus), new PrivateSecretPart(point));
	}
	
	public SecretPart(String s) {
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
	
	public byte[] toBytes() {
		BytesWritable w = new BytesWritable().writeInt(version);
		getPublicPart().toBytes(w);
		getPrivatePart().toBytes(w);
		return w.toByteArray();
	}

	public PublicSecretPart getPublicPart() {
		return publicPart;
	}

	public PrivateSecretPart getPrivatePart() {
		return privatePart;
	}
	
	public int getVersion() {
		return version;
	}
	
	public int getLength() {
		return getPublicPart().getLength();
	}
	
	public BigInteger getModulus() {
		return getPublicPart().getModulus();
	}
	
	public BigPoint getPoint() {
		return getPrivatePart().getPoint();
	}
}
