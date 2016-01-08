package org.mitre.secretsharing.codec.lts;

public enum BlockId {
	SECRET_PART(0x5353535353535353L),
	
	;
	
	public static final long REQUIRED_MASK = 0x1L;
	
	public static boolean isRequired(long id) {
		return (id & REQUIRED_MASK) == REQUIRED_MASK;
	}
	
	private final long id;
	
	private BlockId(long id) {
		this.id = id;
	}
	
	public long id() {
		return id;
	}
	
	public boolean isRequired() {
		return isRequired(id());
	}
}
