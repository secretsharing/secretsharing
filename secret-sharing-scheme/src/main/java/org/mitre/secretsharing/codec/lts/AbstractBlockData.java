package org.mitre.secretsharing.codec.lts;

import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractBlockData extends AbstractCompositeData {

	protected MagicLongData blockId;
	protected NonNegativeLongData blockExtension;

	public AbstractBlockData(long id) {
		composition.add(blockId = new MagicLongData(id));
		composition.add(blockExtension = new NonNegativeLongData());
	}
	
	@Override
	public CompositeData seal() {
		if(!isSealed()) 
			blockExtension.setValue(this.getLength() - (blockId.getLength() + blockExtension.getLength()));
		return super.seal();
	}
}
