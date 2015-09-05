/*

Copyright 2015 The MITRE Corporation

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

package org.mitre.secretsharing.cli.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		for(int r = in.read(buf); r >= 0; r = in.read(buf))
			bytes.write(buf, 0, r);
		return bytes.toByteArray();
	}
	
	public static List<String> readLines(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "ASCII"));
		List<String> lines = new ArrayList<String>();
		for(String line = br.readLine(); line != null; line = br.readLine())
			lines.add(line);
		return lines;
	}
}
