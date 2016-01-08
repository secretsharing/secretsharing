package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NonNegativeLongData extends LongData implements PreparedData {

	public NonNegativeLongData() {
	}

	public NonNegativeLongData(long value) {
		super(value);
	}
	
	@Override
	public DataOutput prepareData(DataOutput output) throws StorageException, IOException {
		if(value < 0)
			throw new StorageFormatException();
		return super.prepareData(output);
	}

	@Override
	public void finishData(DataInput input) throws StorageException, IOException {
		super.finishData(input);
		if(value < 0)
			throw new StorageFormatException();
	}

}
