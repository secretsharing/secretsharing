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
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Encoder/decoder for Base-32 binary representation.<p>
 * 
 * See http://www.crockford.com/wrmg/base32.html for a description
 * of the encoding/decoding symbols
 * @author Robin Kirkman
 *
 */
public abstract class Base32 {
	public static final Pattern DIGIT = Pattern.compile("[0-9a-tv-zA-TV-Z]");
	public static final Pattern ENCODED = Pattern.compile("(D{8})*(DD(DD(D(DD)?)?)?)?".replace("D", DIGIT.pattern()));
	
	private static final Charset ASCII = Charset.forName("ASCII");
	private static final byte[] ENCODE_SYMBOLS = "0123456789abcdefghjkmnpqrstvwxyz".getBytes(ASCII);
	private static final byte[] DECODE_SYMBOLS = new byte[256];
	static {
		Arrays.fill(DECODE_SYMBOLS, (byte) -1);
		for(int i = 0; i < ENCODE_SYMBOLS.length; i++) {
			byte b = ENCODE_SYMBOLS[i];
			DECODE_SYMBOLS[b] = (byte) i;
			// treat lower-case letters as upepr-case letters
			if(Character.isLetter(b))
				DECODE_SYMBOLS[Character.toUpperCase(b)] = (byte) i;
		}
		// special cases
		DECODE_SYMBOLS['O'] = DECODE_SYMBOLS['o'] = 0;
		DECODE_SYMBOLS['I'] = DECODE_SYMBOLS['i'] = 1;
		DECODE_SYMBOLS['L'] = DECODE_SYMBOLS['l'] = 1;
	}
	
	/**
	 * Return the length of the base32-encoded result for data
	 * of the argument length
	 * @param dataLength
	 * @return
	 */
	public static int encodedLength(int dataLength) {
		int len = (dataLength / 5) * 8;
		int addl = dataLength % 5;
		if(addl == 1)
			len += 2;
		else if(addl == 2)
			len += 4;
		else if(addl == 3)
			len += 5;
		else if(addl == 4)
			len += 7;
		return len;
	}
	
	/**
	 * Return the length of the base32-decoded result
	 * for encoded data of the argument length
	 * @param dataLength
	 * @return
	 */
	public static int decodedLength(int dataLength) {
		int len = (dataLength / 8) * 5;
		int addl = dataLength % 8;
		if(addl == 0)
			;
		else if(addl == 2)
			len += 1;
		else if(addl == 4)
			len += 2;
		else if(addl == 5)
			len += 3;
		else if(addl == 7)
			len += 4;
		else
			throw new IllegalArgumentException("Not a valid base32 encoded data length:" + dataLength);
		
		return len;
	}
	
	/**
	 * Encode bytes to a Base 32 string
	 * @param data The data to encode
	 * @return A string of Base 32 representation
	 */
	public static String encode(byte[] data) {
		return new String(encode(null, data), ASCII);
	}
	
	/**
	 * Encode bytes to a Base 32 byte array, returning an array of encoded data.
	 * If the destination array is null or too small to fit the data then
	 * a new array is created and returned.
	 * @param dest The destination array.  May be {@code null}.
	 * @param data The data to encode
	 * @return
	 */
	public static byte[] encode(byte[] dest, byte[] data) {
		int dlen = encodedLength(data.length);
		
		if(dest == null || dest.length < dlen)
			dest = new byte[dlen];
		
		int dpos = 0;
		int next;
		for(int i = 0; i < data.length; i += 5) {
			next = (data[i] & 0xF8) >>> 3; // 0b11111000
			dest[dpos++] = ENCODE_SYMBOLS[next];
			next = (data[i] & 0x07) << 2; // 0b00000111
			
			if(i+1 < data.length)
				next |= (data[i+1] & 0xC0) >>> 6; // 0b11000000
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+1 == data.length)
				break;
			next = (data[i+1] & 0x3E) >>> 1; // 0b00111110
			dest[dpos++] = ENCODE_SYMBOLS[next];
			next = (data[i+1] & 0x01) << 4; // 0b00000001
			
			if(i+2 < data.length)
				next |= (data[i+2] & 0xF0) >>> 4; // 0b11110000
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+2 == data.length)
				break;
			next = (data[i+2] & 0x0F) << 1; // 0b00001111
			
			if(i+3 < data.length)
				next |= (data[i+3] & 0x80) >>> 7; // 0b10000000
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+3 == data.length)
				break;
			next = (data[i+3] & 0x7C) >>> 2; // 0b01111100
			dest[dpos++] = ENCODE_SYMBOLS[next];
			next = (data[i+3] & 0x03) << 3; // 0b00000011
			
			if(i+4 < data.length)
				next |= (data[i+4] & 0xE0) >>> 5; // 0b11100000
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+4 == data.length)
				break;
			next = (data[i+4] & 0x1F); // 0b00011111
			dest[dpos++] = ENCODE_SYMBOLS[next];
		}
		
		return dest;
	}
	
	/**
	 * Decode a string of Base 32 representation to the original bytes
	 * @param data The string to decode
	 * @return The original bytes as a new array
	 */
	public static byte[] decode(String data) {
		return decode(null, data.getBytes(ASCII));
	}
	
	/**
	 * Decode an array of Base 32 encoded data to its original bytes.
	 * If the destination array is null or too small, a new array is
	 * created and returned.
	 * @param dest The destination array.  May be {@code null}
	 * @param data The data to decode
	 * @return
	 */
	public static byte[] decode(byte[] dest, byte[] data) {
		int dlen = decodedLength(data.length);
		
		if(dest == null || dest.length < dlen)
			dest = new byte[dlen];
		
		int dpos = 0;
		int next;
		
		for(int i = 0; i < data.length; i += 8) {
			next = DECODE_SYMBOLS[data[i]] << 3;
			next |= DECODE_SYMBOLS[data[i+1]] >>> 2;
			dest[dpos++] = (byte) next;
			
			if(i+2 == data.length)
				break;
			next = DECODE_SYMBOLS[data[i+1]] << 6;
			next |= DECODE_SYMBOLS[data[i+2]] << 1;
			next |= DECODE_SYMBOLS[data[i+3]] >>> 4;
			dest[dpos++] = (byte) next;
			
			if(i+4 == data.length)
				break;
			next = DECODE_SYMBOLS[data[i+3]] << 4;
			next |= DECODE_SYMBOLS[data[i+4]] >>> 1;
			dest[dpos++] = (byte) next;
			
			if(i+5 == data.length)
				break;
			next = DECODE_SYMBOLS[data[i+4]] << 7;
			next |= DECODE_SYMBOLS[data[i+5]] << 2;
			next |= DECODE_SYMBOLS[data[i+6]] >>> 3;
			dest[dpos++] = (byte) next;
			
			if(i+7 == data.length)
				break;
			next = DECODE_SYMBOLS[data[i+6]] << 5;
			next |= DECODE_SYMBOLS[data[i+7]];
			dest[dpos++] = (byte) next;
		}
		
		return dest;
	}
	
	private Base32() {}
}
