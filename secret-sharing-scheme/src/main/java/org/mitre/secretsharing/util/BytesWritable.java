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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.mitre.secretsharing.codec.Base32;

/**
 * Utility class for writing values to byte arrays
 * @author Robin Kirkman
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
