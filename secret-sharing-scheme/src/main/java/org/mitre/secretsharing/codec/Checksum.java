/*

Copyright (c) 2014, The MITRE Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
	in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived 
	from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package org.mitre.secretsharing.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mitre.secretsharing.BigPoint;
import org.mitre.secretsharing.util.BytesReadable;
import org.mitre.secretsharing.util.BytesWritable;

/**
 * Simple checksum.  Makes absolutely no attempt at error correction.
 * @author Robin Kirkman
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
