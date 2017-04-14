/*

Copyright 2017 The MITRE Corporation

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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class MSBOneTest {

	@Test
	public void testMSBOneMultibyte() {
		byte[] secret = new byte[] {(byte) 0x80};
		Part[] parts = Secrets.splitMultibyte(secret, 2, 2, new Random());
		try {
			byte[] reconstructed = Secrets.joinMultibyte(parts);
			Assert.assertArrayEquals(secret, reconstructed);
		} catch(ArrayIndexOutOfBoundsException e) {
			Assert.fail("github issue #4, msb one of multibyte secret");
		}
	}

	@Test
	public void testMSBOnePerByte() {
		byte[] secret = new byte[] {(byte) 0x80};
		PerBytePart[] parts = Secrets.splitPerByte(secret, 2, 2, new Random());
		try {
			byte[] reconstructed = Secrets.joinPerByte(parts);
			Assert.assertArrayEquals(secret, reconstructed);
		} catch(ArrayIndexOutOfBoundsException e) {
			Assert.fail("github issue #4, msb one of multibyte secret");
		}
	}

}
