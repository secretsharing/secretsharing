package org.mitre.secretsharing.codec.lts;

import java.util.List;

public class ListSizeData extends NonNegativeLongData {
	
	protected List<?> list;

	public ListSizeData(List<?> list) {
		this.list = list;
	}

	@Override
	public long getValue() {
		return list.size();
	}
	
	@Override
	public void setValue(long value) {
		throw new UnsupportedOperationException();
	}

}
