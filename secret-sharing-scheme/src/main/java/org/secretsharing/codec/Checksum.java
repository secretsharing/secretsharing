package org.secretsharing.codec;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.secretsharing.BigPoint;
import org.secretsharing.util.BytesReadable;
import org.secretsharing.util.BytesWritable;

/**
 * Simple checksum.  Makes absolutely no attempt at error correction.
 * @author robin
 *
 */
public final class Checksum {

	public static Checksum fromBytes(byte[] cxb) {
		return new Checksum((0xff & cxb[0]) | ((0xff & cxb[1]) << 8));
	}
	
	/**
	 * Compute a 2-byte checksum by XORing the bytes of the SHA-1
	 * of the argument
	 * @param buf
	 * @return
	 */
	public static int checksum(byte[] buf) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		int cx = 0;
		byte[] d = digest.digest(buf);
		for(int i = 0; i < d.length; i += 2) {
			cx ^= (0xff & d[i]);
			cx ^= (0xff & d[i+1]) << 8;
		}
		return cx;
	}
	
	/**
	 * The int value of the checksum
	 */
	private int cx;
	/**
	 * The bytes composing the checksum
	 */
	private byte[] cxb;
	
	/**
	 * Compute the checksum of a byte array
	 * @param buf
	 */
	public Checksum(byte[] buf) {
		this.cx = checksum(buf);
		cxb = new byte[] {(byte) cx, (byte)(cx >>> 8)};
	}
	
	/**
	 * Parse a checksum from a string
	 * @param s
	 */
	public Checksum(String s) {
		cxb = Base32.decode(s);
		cx = (0xff & cxb[0]) | ((0xff & cxb[1]) << 8);
	}
	
	/**
	 * Create a new {@link Checksum} from a literal checksum int
	 * @param cx
	 */
	public Checksum(int cx) {
		this.cx = cx;
		cxb = new byte[] {(byte) cx, (byte)(cx >>> 8)};
	}
	
	/**
	 * Read a {@link Checksum} from bytes
	 * @param r
	 */
	public Checksum(BytesReadable r) {
		cxb = r.readBytes(2);
		cx = (0xff & cxb[0]) | ((0xff & cxb[1]) << 8);
	}
	
	public Checksum(BigPoint point) {
		this(new BytesWritable().writeBigInteger(point.getX()).writeBigInteger(point.getY()).toByteArray());
	}
	
	@Override
	public String toString() {
		return Base32.encode(cxb);
	}
	
	/**
	 * Return the 16-bit checksum
	 * @return
	 */
	public int getChecksum() {
		return cx;
	}
	
	/**
	 * Return the 16-bit checksum as a byte array
	 * @return
	 */
	public byte[] getChecksumBytes() {
		return cxb;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof Checksum) {
			return getChecksum() == ((Checksum) obj).getChecksum();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return cx;
	}
}
