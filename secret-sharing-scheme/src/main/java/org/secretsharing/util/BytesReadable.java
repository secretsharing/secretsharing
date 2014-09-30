package org.secretsharing.util;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

import org.secretsharing.codec.Base32;

public class BytesReadable {
	private byte[] b;
	private ByteArrayInputStream buf;
	private DataInput data;
	
	public BytesReadable(String b) {
		this(Base32.decode(b));
	}
	
	public BytesReadable(byte[] b) {
		this.b = b;
		buf = new ByteArrayInputStream(b);
		data = new DataInputStream(buf);
	}
	
	@Override
	public String toString() {
		return Base32.encode(b);
	}
	
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
