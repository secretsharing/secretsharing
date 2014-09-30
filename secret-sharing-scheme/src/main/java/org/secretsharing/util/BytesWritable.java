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
