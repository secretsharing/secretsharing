package org.mitre.secretsharing.codec;

import java.util.NoSuchElementException;

public class ArrayByteIterator implements ByteIterator {

	protected byte[] data;
	protected int pos;
	protected int stop;
	
	public ArrayByteIterator(byte[] data, int start, int stop) {
		this.data = data;
		this.pos = start;
		this.stop = stop;
	}

	@Override
	public boolean hasNext() {
		return pos < stop;
	}

	@Override
	public byte next() {
		if(!hasNext())
			throw new NoSuchElementException();
		return data[pos++];
	}

}
