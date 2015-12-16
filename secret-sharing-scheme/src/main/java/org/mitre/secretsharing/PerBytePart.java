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

public class PerBytePart extends Part {
	public static final BigInteger MODULUS = BigInteger.valueOf(65521);

	public PerBytePart(int version, int length, int requiredParts, BigPoint point) {
		super(version, length, requiredParts, MODULUS, point);
	}

	@Override
	public byte[] join(Part... otherParts) {
		PerBytePart[] parts = new PerBytePart[otherParts.length + 1];
		parts[0] = this;
		for(int i = 0; i < otherParts.length; i++)
			parts[i+1] = (PerBytePart) otherParts[i];

		return Secrets.joinPerByte(parts);
	}
}
