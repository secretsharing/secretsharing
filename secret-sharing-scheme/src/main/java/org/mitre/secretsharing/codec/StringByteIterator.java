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

/**
 * {@link ByteIterator} that iterates over the bytes in a string.  Returns
 * only the least-significant byte of each two-byte {@code char} in the
 * string, and does not attempt to use any sort of character encoding.
 * @author Robin Kirkman
 *
 */
public class StringByteIterator implements ByteIterator {
	/**
	 * The string to be iterated over
	 */
	protected String data;
	/**
	 * The current position within the string
	 */
	protected int pos;
	/**
	 * The stopping position within the string
	 */
	protected int stop;

	/**
	 * Create a new {@link StringByteIterator}, with an iterated {@link String},
	 * a starting position, and a stopping position
	 * @param data The string to iterate over
	 * @param start The starting position
	 * @param stop The stopping position
	 */
	public StringByteIterator(String data, int start, int stop) {
		this.data = data;
		this.pos = start;
		this.stop = stop;
	}
	
	/**
	 * Should a character be skipped when iterating? Subclasses can override
	 * this to enable subsets of strings to be returned.
	 * @param c The candidate character
	 * @return {@code true} to skip the character
	 */
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
