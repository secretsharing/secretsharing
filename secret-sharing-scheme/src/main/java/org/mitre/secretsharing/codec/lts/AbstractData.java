package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractData implements Data {
	
	protected abstract void writeData(DataOutput output) throws StorageException, IOException;
	protected abstract void readData(DataInput input) throws StorageException, IOException;
	
	protected boolean sealed;
	
	@Override
	public boolean isSealed() {
		return sealed;
	}
	
	@Override
	public Data seal() {
		sealed = true;
		return this;
	}
	
	protected void assertSealed() throws StorageException {
		if(!sealed)
			throw new StorageException();
	}
	
	protected void assertUnsealed() throws StorageException {
		if(sealed)
			throw new StorageException();
	}
	
	@Override
	public void write(DataOutput output) throws StorageException, IOException {
		if(this instanceof PreparedData)
			output = prepareData(output);
		try {
			assertSealed();
			writeData(output);
		} finally {
			if(this instanceof PreparedData)
				finishData(output);
		}
	}
	
	@Override
	public void read(DataInput input) throws StorageException, IOException {
		if(this instanceof PreparedData)
			input = prepareData(input);
		try {
			assertUnsealed();
			readData(input);
		} finally {
			if(this instanceof PreparedData)
				finishData(input);
		}
	}
	
	public DataOutput prepareData(DataOutput output) throws StorageException, IOException {
		return output;
	}

	public DataInput prepareData(DataInput input) throws StorageException, IOException {
		return input;
	}

	public void finishData(DataOutput output) throws StorageException, IOException {
	}

	public void finishData(DataInput input) throws StorageException, IOException {
	}

}
