package org.secretsharing.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.secretsharing.codec.Base32;

public final class Checksum {

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
	
	private int cx;
	private byte[] cxb;
	
	public Checksum(byte[] buf) {
		this.cx = checksum(buf);
		cxb = new byte[] {(byte) cx, (byte)(cx >>> 8)};
	}
	
	public Checksum(String s) {
		cxb = Base32.decode(s);
		cx = (0xff & cxb[0]) | ((0xff & cxb[1]) << 8);
	}
	
	public Checksum(int cx) {
		this.cx = cx;
		cxb = new byte[] {(byte) cx, (byte)(cx >>> 8)};
	}
	
	public Checksum(BytesReadable r) {
		cxb = r.readBytes(2);
		cx = (0xff & cxb[0]) | ((0xff & cxb[1]) << 8);
	}
	
	@Override
	public String toString() {
		return Base32.encode(cxb);
	}
	
	public int getChecksum() {
		return cx;
	}
	
	public byte[] getChecksumBytes() {
		return cxb;
	}
	
	public void write(BytesWritable w) {
		w.writeBytes(cxb);
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
