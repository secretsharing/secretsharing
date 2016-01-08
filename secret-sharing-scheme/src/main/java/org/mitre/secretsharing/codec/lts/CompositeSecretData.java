package org.mitre.secretsharing.codec.lts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mitre.secretsharing.util.Lists;

public class CompositeSecretData extends AbstractSecretData {

	protected NonNegativeLongData subsecretCount;
	protected List<AbstractSecretData> subsecrets;
	
	public CompositeSecretData(long id, boolean forRead) {
		super(id, forRead);
		subsecrets = new ArrayList<AbstractSecretData>();
		subsecretCount = (forRead ? new NonNegativeLongData() : new ListSizeData(subsecrets));
	}

	@Override
	public List<Data> getComposition() {
		return Lists.viewAppended(super.getComposition(), Lists.viewAppended(Collections.singletonList(subsecretCount), subsecrets));
	}
}
