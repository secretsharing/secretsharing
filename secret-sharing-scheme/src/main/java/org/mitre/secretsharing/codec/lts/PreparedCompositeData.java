package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface PreparedCompositeData extends CompositeData, PreparedData {

	public DataOutput prepareData(DataOutput output, int idx) throws PartfileException, IOException;
	public DataInput prepareData(DataInput input, int idx) throws PartfileException, IOException;
	public void finishData(DataOutput output, int idx) throws PartfileException, IOException;
	public void finishData(DataInput input, int idx) throws PartfileException, IOException;

}
