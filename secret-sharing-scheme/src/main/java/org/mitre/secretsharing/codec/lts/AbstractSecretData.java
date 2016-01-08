package org.mitre.secretsharing.codec.lts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mitre.secretsharing.util.Lists;

public class AbstractSecretData extends AbstractBlockData {

	protected NonNegativeLongData parameterCount;
	protected List<AbstractMetaData> parameters;
	
	public AbstractSecretData(long id, boolean forRead) {
		super(id);
		parameters = new ArrayList<AbstractMetaData>();
		parameterCount = (forRead ? new NonNegativeLongData() : new ListSizeData(parameters));
	}
	
	@Override
	public List<Data> getComposition() {
		return Lists.viewAppended(super.getComposition(), Lists.viewAppended(Collections.singletonList(parameterCount), parameters));
	}

}
