package org.secretsharing.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Base32 {
	private static byte[] ENCODE_SYMBOLS = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".getBytes(Charset.forName("ASCII"));
	private static byte[] DECODE_SYMBOLS = new byte[256];
	static {
		Arrays.fill(DECODE_SYMBOLS, (byte) -1);
		for(int i = 0; i < ENCODE_SYMBOLS.length; i++) {
			byte b = ENCODE_SYMBOLS[i];
			DECODE_SYMBOLS[b] = (byte) i;
			// treat lower-case letters as upepr-case letters
			if(Character.isLetter(b))
				DECODE_SYMBOLS[Character.toLowerCase(b)] = (byte) i;
		}
		// special cases
		DECODE_SYMBOLS['O'] = DECODE_SYMBOLS['o'] = 0;
		DECODE_SYMBOLS['I'] = DECODE_SYMBOLS['i'] = 1;
		DECODE_SYMBOLS['L'] = DECODE_SYMBOLS['l'] = 1;
	}
	
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
	
	public static byte[] encode(byte[] dest, byte[] data) {
		int dlen = encodedLength(data.length);
		
		if(dest == null || dest.length < dlen)
			dest = new byte[dlen];
		
		int dpos = 0;
		int next;
		for(int i = 0; i < data.length; i += 5) {
			next = (data[i] & 0b11111000) >>> 3;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			next = (data[i] & 0b111) << 2;
			
			if(i+1 < data.length)
				next |= (data[i+1] & 0b11000000) >>> 6;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+1 == data.length)
				break;
			next = (data[i+1] & 0b111110) >>> 1;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			next = (data[i+1] & 0b1) << 4;
			
			if(i+2 < data.length)
				next |= (data[i+2] & 0b11110000) >>> 4;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+2 == data.length)
				break;
			next = (data[i+2] & 0b1111) << 1;
			
			if(i+3 < data.length)
				next |= (data[i+3] & 0b10000000) >>> 7;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+3 == data.length)
				break;
			next = (data[i+3] & 0b1111100) >>> 2;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			next = (data[i+3] & 0b11) << 3;
			
			if(i+4 < data.length)
				next |= (data[i+4] & 0b11100000) >>> 5;
			dest[dpos++] = ENCODE_SYMBOLS[next];
			if(i+4 == data.length)
				break;
			next = (data[i+4] & 0b11111);
			dest[dpos++] = ENCODE_SYMBOLS[next];
		}
		
		return dest;
	}
	
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
}
