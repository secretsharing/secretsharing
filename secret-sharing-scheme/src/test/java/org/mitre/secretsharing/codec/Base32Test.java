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

package org.mitre.secretsharing.codec;

import java.nio.charset.Charset;

import org.junit.Test;
import org.mitre.secretsharing.codec.Base32;

public class Base32Test {
	private static final Charset ASCII = Charset.forName("ASCII");
	
	@Test
	public void testEncode() throws Exception {
		byte[] enc = Base32.encode(null, "Hello World".getBytes(ASCII));
		System.out.println(new String(enc, ASCII));
		byte[] dec = Base32.decode(null, enc);
		System.out.println(new String(dec, ASCII));
	}
}
