package org.secretsharing.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.secretsharing.codec.Base32;

public class BytesWritable {
	private ByteArrayOutputStream buf = new ByteArrayOutputStream();
	private DataOutput data = new DataOutputStream(buf);
	
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
	
	public BytesWritable writeBytes(byte[] b) {
		try {
			data.write(b);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public byte[] toByteArray() {
		return buf.toByteArray();
	}
	
	public String reset() {
		String s = toString();
		buf.reset();
		return s;
	}
	
	public int size() {
		return buf.size();
	}
	
	@Override
	public String toString() {
		return Base32.encode(toByteArray());
	}
}
