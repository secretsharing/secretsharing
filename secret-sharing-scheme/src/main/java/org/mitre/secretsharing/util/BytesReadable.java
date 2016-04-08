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

package org.mitre.secretsharing.util;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.math.BigInteger;

import org.mitre.secretsharing.codec.Base32;

/**
 * Utility class for reading byte arrays
 * @author Robin Kirkman
 *
 */
public class BytesReadable {
	/**
	 * The stream buffer
	 */
	private ByteArrayInputStream buf;

	/**
	 * The string value
	 */
	private String string;
	
	/**
	 * Base 32 decode the argument then create a {@link BytesReadable}
	 * @param s Base 32 encoded data to read
	 */
	public BytesReadable(String s) {
		InputValidation.begin().when(s == null, "string is null").validate();
		buf = new ByteArrayInputStream(Base32.decode(s));
		string = s;
	}
	
	/**
	 * Create a {@link BytesReadable} that reads the argument
	 * @param b Data to read
	 */
	public BytesReadable(byte[] b) {
		InputValidation.begin().when(b == null, "array is null").validate();
		buf = new ByteArrayInputStream(b);
		string = Base32.encode(b);
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	/**
	 * Read a {@link BigInteger}
	 * @return The next {@link BigInteger}
	 */
	public BigInteger readBigInteger() {
		int len = readInt();
		byte[] b = new byte[len];
		if(buf.read(b, 0, b.length) < b.length)
			throw new RuntimeException(new EOFException());
		return new BigInteger(b);
	}
	
	/**
	 * Read an {@code int}, optimized for space for non-negative values
	 * @return The next {@code int}
	 */
	public int readInt() {
		int i = 0;
		int off = 0;
		boolean term;
		do {
			int l = buf.read();
			if(l < 0)
				throw new RuntimeException(new EOFException());
			term = (l & 0x80) != 0;
			i |= (l & 0x7f) << off;
			off += 7;
		} while(!term);
		return i;
	}
	
	/**
	 * Read some raw bytes
	 * @param len The number of bytes to read
	 * @return A new {@code byte[]}
	 */
	public byte[] readBytes(int len) {
		byte[] b = new byte[len];
		if(buf.read(b, 0, b.length) < b.length)
			throw new RuntimeException(new EOFException());
		return b;
	}
}
