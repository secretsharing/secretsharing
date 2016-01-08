package org.mitre.secretsharing.codec.lts;

import java.util.List;

public class SecretPartStorage extends AbstractBlockData {

	protected LongData entropy;
	protected NonNegativeLongData majorVersion;
	protected NonNegativeLongData minorVersion;
	protected NonNegativeLongData metaCount;
	protected List<AbstractMetaData> meta;
	
	public SecretPartStorage() {
		super(BlockId.SECRET_PART.id());
	}


}
