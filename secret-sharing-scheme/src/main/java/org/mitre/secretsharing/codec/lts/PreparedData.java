package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface PreparedData extends Data {
	public DataOutput prepareData(DataOutput output) throws StorageException, IOException;
	public DataInput prepareData(DataInput input) throws StorageException, IOException;
	public void finishData(DataOutput output) throws StorageException, IOException;
	public void finishData(DataInput input) throws StorageException, IOException;
}
