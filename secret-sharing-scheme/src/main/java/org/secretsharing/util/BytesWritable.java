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

package org.secretsharing.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.secretsharing.codec.Base32;

/**
 * Utility class for writing values to byte arrays
 * @author robin
 *
 */
public class BytesWritable {
	/**
	 * Backing buffer
	 */
	private ByteArrayOutputStream buf = new ByteArrayOutputStream();
	/**
	 * Handles the writing of data
	 */
	private DataOutput data = new DataOutputStream(buf);
	
	/**
	 * Write a {@link BigInteger}
	 * @param val
	 * @return
	 */
	public BytesWritable writeBigInteger(BigInteger val) {
		try {
			byte[] b = val.toByteArray();
			writeInt(b.length);
			data.write(b);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/**
	 * Write an int, optimized for space for non-negative values
	 * @param val
	 * @return
	 */
	public BytesWritable writeInt(int val) {
		try {
			do {
				boolean term = (val & ~0x7f) == 0;
				data.write((val & 0x7f) | (term ? 0x80 : 0));
				val = val >>> 7;
			} while(val != 0);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/**
	 * Write a raw byte array
	 * @param b
	 * @return
	 */
	public BytesWritable writeBytes(byte[] b) {
		try {
			data.write(b);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/**
	 * Return the bytes written
	 * @return
	 */
	public byte[] toByteArray() {
		return buf.toByteArray();
	}
	
	/**
	 * Convert to a string, reset the buffer, then return the string
	 * @return
	 */
	public String reset() {
		String s = toString();
		buf.reset();
		return s;
	}
	
	/**
	 * Return the number of bytes written
	 * @return
	 */
	public int size() {
		return buf.size();
	}
	
	@Override
	public String toString() {
		return Base32.encode(toByteArray());
	}
}
