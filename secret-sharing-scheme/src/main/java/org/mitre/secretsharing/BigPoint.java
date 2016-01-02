/*

Copyright 2014 The MITRE Corporation

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

package org.mitre.secretsharing;

import java.math.BigInteger;

import org.mitre.secretsharing.util.InputValidation;

/**
 * An x/y point using {@link BigInteger} for its coordinates
 * @author Robin Kirkman
 *
 */
public class BigPoint {
	/**
	 * The X coordinate
	 */
	private BigInteger x;
	/**
	 * The Y coordinate
	 */
	private BigInteger y;
	
	/**
	 * Create a new {@link BigPoint} with the specified X and Y coordinates
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 */
	public BigPoint(BigInteger x, BigInteger y) {
		InputValidation.begin().when(x == null, "x is null").when(y == null, "y is null").validate();
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return x + "," + y;
	}

	/**
	 * Return the X coordinate
	 * @return The X coordinate of this {@link BigPoint}
	 */
	public BigInteger getX() {
		return x;
	}

	/**
	 * Return the Y coordinate
	 * @return The Y coordinate of this {@link BigPoint}
	 */
	public BigInteger getY() {
		return y;
	}
}
