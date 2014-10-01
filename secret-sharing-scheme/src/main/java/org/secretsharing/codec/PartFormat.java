package org.secretsharing.codec;

import org.secretsharing.Part;

public interface PartFormat<T> {
	
	public T format(Part part);
	public Part parse(T data);
	public int getVersion();
	
}
