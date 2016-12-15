/*

Copyright 2016 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing.codec;

import java.util.NoSuchElementException;

//TODO javadoc
public class StringByteIterator implements ByteIterator {
	protected String data;
	protected int pos;
	protected int stop;

	public StringByteIterator(String data, int start, int stop) {
		this.data = data;
		this.pos = start;
		this.stop = stop;
	}
	
	protected boolean skip(char c) {
		return false;
	}

	@Override
	public boolean hasNext() {
		while(pos < stop && skip(data.charAt(pos)))
			pos++;
		return pos < stop;
	}

	@Override
	public byte next() {
		if(!hasNext())
			throw new NoSuchElementException();
		return (byte) data.charAt(pos++);
	}

}
