package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MagicLongData extends LongData implements PreparedData {

	protected long magic;

	public MagicLongData(long magic) {
		super(magic);
		this.magic = magic;
	}
	
	public long getMagic() {
		return magic;
	}
	
	@Override
	public DataOutput prepareData(DataOutput output) throws StorageException, IOException {
		if(value != magic)
			throw new StorageFormatException();
		return super.prepareData(output);
	}

	@Override
	public void finishData(DataInput input) throws StorageException, IOException {
		super.finishData(input);
		if(value != magic)
			throw new StorageFormatException();
	}
	
}
