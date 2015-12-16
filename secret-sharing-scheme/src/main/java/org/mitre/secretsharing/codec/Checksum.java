/*

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mitre.secretsharing.BigPoint;
import org.mitre.secretsharing.util.BytesReadable;
import org.mitre.secretsharing.util.BytesWritable;

/**
 * Simple checksum.  Makes absolutely no attempt at error correction.<p>
 * 
 * Deprecated in version 1.1.0
 * @author Robin Kirkman
 *
 */
@Deprecated
final class Checksum {

	/**
	 * Return a {@link Checksum} represented by the two-byte array argument.
	 * @param cxb The two bytes to process as a {@link Checksum}
	 * @return A new {@link Checksum}
	 */
	static Checksum fromBytes(byte[] cxb) {
		return new Checksum((0xff & cxb[0]) | ((0xff & cxb[1]) << 8));
	}
	
	/**
	 * Compute a 2-byte checksum by XORing the bytes of the SHA-1
	 * of the argument
	 * @param buf
	 * @return
	 */
	static int checksum(byte[] buf) {
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
	Checksum(byte[] buf) {
		this.cx = checksum(buf);
		cxb = new byte[] {(byte) cx, (byte)(cx >>> 8)};
	}
	
	/**
	 * Parse a checksum from a string
	 * @param s
	 */
	Checksum(String s) {
		cxb = Base32.decode(s);
		cx = (0xff & cxb[0]) | ((0xff & cxb[1]) << 8);
	}
	
	/**
	 * Create a new {@link Checksum} from a literal checksum int
	 * @param cx
	 */
	Checksum(int cx) {
		this.cx = cx;
		cxb = new byte[] {(byte) cx, (byte)(cx >>> 8)};
	}
	
	/**
	 * Read a {@link Checksum} from bytes
	 * @param r
	 */
	Checksum(BytesReadable r) {
		cxb = r.readBytes(2);
		cx = (0xff & cxb[0]) | ((0xff & cxb[1]) << 8);
	}
	
	Checksum(BigPoint point) {
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
	int getChecksum() {
		return cx;
	}
	
	/**
	 * Return the 16-bit checksum as a byte array
	 * @return
	 */
	byte[] getChecksumBytes() {
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
