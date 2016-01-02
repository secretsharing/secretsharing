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
 * A part of a shared secret that was first split into individual bytes.<p>
 * 
 * Unlike {@link Part}, which uses a single Y coordinate, {@link PerBytePart} uses
 * an individual two-byte Y coordinate for each byte of the shared secret.  The single X
 * coordinate is used for all Y coordinates.
 * 
 * @author Robin Kirkman
 *
 */
public class PerBytePart extends Part {
	public static final BigInteger MODULUS = BigInteger.valueOf(65521);
	public static final int MAX_PARTS = MODULUS.intValue() - 1;

	/**
	 * Create a {@link PerBytePart} to hold a per-byte secret part
	 * @param version The version this part was formatted with
	 * @param length The number of bytes in the secret
	 * @param requiredParts How many parts are required to reconstruct the secret
	 * @param point The {@link BigPoint} representing this secret part
	 */
	public PerBytePart(int version, int length, int requiredParts, BigPoint point) {
		super(version, length, requiredParts, MODULUS, point);
	}

	@Override
	public byte[] join(Part... otherParts) {
		InputValidation iv = InputValidation.begin()
			.when(otherParts == null, "otherParts is null")
			.validate();
		PerBytePart[] parts = new PerBytePart[otherParts.length + 1];
		parts[0] = this;
		for(int i = 0; i < otherParts.length; i++) {
			iv
				.when(!(otherParts[i] instanceof PerBytePart), "cannot apply perbyte join to multibyte parts")
				.validate();
			parts[i+1] = (PerBytePart) otherParts[i];
		}
		return Secrets.joinPerByte(parts);
	}
}
