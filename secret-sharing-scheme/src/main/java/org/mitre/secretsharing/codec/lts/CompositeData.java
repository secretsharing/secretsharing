package org.mitre.secretsharing.codec.lts;

import java.util.List;

public interface CompositeData extends Data {
	public List<Data> getComposition();
}
