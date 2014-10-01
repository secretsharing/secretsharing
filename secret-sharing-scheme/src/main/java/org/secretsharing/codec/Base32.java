package org.secretsharing.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Encoder/decoder for Base-32 binary representation.
 * @author robin
 *
 */
public class Base32 {
	public static final Pattern DIGIT = Pattern.compile("[0-9a-tv-zA-TV-Z]");
	public static final Pattern ENCODED = Pattern.compile("(D{8})*(DD(DD(D(DD)?)?)?)?".replace("D", DIGIT.pattern()));
	
	private static final Charset ASCII = Charset.forName("ASCII");
	private static final byte[] ENCODE_SYMBOLS = "0123456789abcdefghjkmnpqrstvwxyz".getBytes(Charset.forName("ASCII"));
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
}
