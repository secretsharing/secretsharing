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
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.mitre.secretsharing.codec.Base32;

/**
 * Utility class for reading byte arrays
 * @author Robin Kirkman
 *
 */
public class BytesReadable {
	/**
	 * The raw backing array
	 */
	private byte[] b;
	/**
	 * The stream buffer
	 */
	private ByteArrayInputStream buf;
	/**
	 * Handles data input
	 */
	private DataInput data;
	
	/**
	 * Base 32 decode the argument then create a {@link BytesReadable}
	 * @param b
	 */
	public BytesReadable(String b) {
		this(Base32.decode(b));
	}
	
	/**
	 * Create a {@link BytesReadable} that reads the argument
	 * @param b
	 */
	public BytesReadable(byte[] b) {
		this.b = b;
		buf = new ByteArrayInputStream(b);
		data = new DataInputStream(buf);
	}
	
	@Override
	public String toString() {
		return Base32.encode(b);
	}
	
	/**
	 * Read a {@link BigInteger}
	 * @return
	 */
	public BigInteger readBigInteger() {
		try {
			int len = readInt();
			byte[] b = new byte[len];
			data.readFully(b);
			return new BigInteger(b);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Read an int, optimized for space for non-negative values
	 * @return
	 */
	public int readInt() {
		try {
			int i = 0;
			int off = 0;
			boolean term;
			do {
				int l = data.readUnsignedByte();
				term = (l & 0x80) != 0;
				i |= (l & 0x7f) << off;
				off += 7;
			} while(!term);
			return i;
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Read some raw bytes
	 * @param len
	 * @return
	 */
	public byte[] readBytes(int len) {
		try {
			byte[] b = new byte[len];
			data.readFully(b);
			return b;
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
