package org.mitre.secretsharing.codec.lts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompositeData extends AbstractData implements CompositeData {

	protected List<Data> composition = new ArrayList<Data>();
	
	@Override
	public List<Data> getComposition() {
		return composition;
	}

	protected void writeData(DataOutput output, int idx) throws PartfileException, IOException {
		if(this instanceof PreparedCompositeData)
			prepareData(output, idx);
		try {
			getComposition().get(idx).write(output);
		} finally {
			if(this instanceof PreparedCompositeData)
				finishData(output, idx);
		}
	}
	
	protected void readData(DataInput input, int idx) throws PartfileException, IOException {
		if(this instanceof PreparedCompositeData) 
			prepareData(input, idx);
		try {
			getComposition().get(idx).read(input);
		} finally {
			if(this instanceof PreparedCompositeData)
				finishData(input, idx);
		}
	}

	@Override
	protected void writeData(DataOutput output) throws PartfileException, IOException {
		for(int idx = 0; idx < getComposition().size(); idx++)
			writeData(output, idx);
	}
	
	@Override
	protected void readData(DataInput input) throws PartfileException, IOException {
		for(int idx = 0; idx < getComposition().size(); idx++)
			readData(input, idx);
	}

	public DataOutput prepareData(DataOutput output, int idx) throws PartfileException, IOException {
		return output;
	}

	public DataInput prepareData(DataInput input, int idx) throws PartfileException, IOException {
		return input;
	}

	public void finishData(DataOutput output, int idx) throws PartfileException, IOException {
	}

	public void finishData(DataInput input, int idx) throws PartfileException, IOException {
	}

}
