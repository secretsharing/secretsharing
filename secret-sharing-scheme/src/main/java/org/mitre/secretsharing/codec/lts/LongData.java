package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongData extends AbstractData {
	
	protected long value;

	public LongData() {
	}
	
	public LongData(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	public void setValue(long value) {
		this.value = value;
	}

	@Override
	protected void writeData(DataOutput output) throws StorageException, IOException {
		output.writeLong(getValue());
	}

	@Override
	protected void readData(DataInput input) throws StorageException, IOException {
		setValue(input.readLong());
	}

}
