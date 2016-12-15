package org.mitre.secretsharing.codec;

import java.util.NoSuchElementException;

//TODO javadoc
public class StringByteIterator implements ByteIterator {
	protected String data;
	protected int pos;
	protected int stop;

	public StringByteIterator(String data, int start, int stop) {
		this.data = data;
		this.pos = start;
		this.stop = stop;
	}
	
	protected boolean skip(char c) {
		return false;
	}

	@Override
	public boolean hasNext() {
		while(pos < stop && skip(data.charAt(pos)))
			pos++;
		return pos < stop;
	}

	@Override
	public byte next() {
		if(!hasNext())
			throw new NoSuchElementException();
		return (byte) data.charAt(pos++);
	}

}
