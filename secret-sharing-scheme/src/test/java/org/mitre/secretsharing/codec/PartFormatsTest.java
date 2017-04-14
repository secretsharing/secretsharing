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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.Secrets;
import org.mitre.secretsharing.codec.PartFormats.BytesFormats;
import org.mitre.secretsharing.codec.PartFormats.StringFormats;

public class PartFormatsTest {
	@Test
	public void testLongString() {
		byte[] secret = new byte[1024*1024];
		Arrays.fill(secret, (byte) 'A');
		System.out.println("splitting");
		Part part = Secrets.splitPerByte(secret, 1, 1, new Random())[0];
		System.out.println("formatting");
		String formatted = PartFormats.currentStringFormat().format(part);
		System.out.println("- length=" + formatted.length());
		System.out.println("parsing");
		PartFormats.parse(formatted);
	}
	
	@Test
	public void testStringVersions() {
		for(PartFormat<String> pf : StringFormats.values()) {
			Assert.assertEquals(pf, PartFormats.stringFormat(pf.getVersion()));
		}
		try {
			PartFormats.stringFormat(-1);
			Assert.fail("invalid part format, no exception");
		} catch(IllegalArgumentException e) {
			// expected
		}
	}
	
	@Test
	public void testByteArrayVersions() {
		for(PartFormat<byte[]> pf : BytesFormats.values()) {
			Assert.assertEquals(pf, PartFormats.bytesFormat(pf.getVersion()));
		}
		try {
			PartFormats.bytesFormat(-1);
			Assert.fail("invalid part format, no exception");
		} catch(IllegalArgumentException e) {
			// expected
		}
	}
}
