/*

Copyright (c) 2014, The MITRE Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
	in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived 
	from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package org.mitre.secretsharing.codec;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mitre.secretsharing.BigPoint;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.util.BytesReadable;
import org.mitre.secretsharing.util.BytesWritable;

public class PartFormats {
	public static PartFormat<String> stringFormat(int version) {
		return StringFormats.values()[version];
	}
	
	public static PartFormat<byte[]> bytesFormat(int version) {
		return BytesFormats.values()[version];
	}
	
	public static Part parse(String data) {
		return stringFormat(StringFormats.detectVersion(data)).parse(data);
	}
	
	public static Part parse(byte[] data) {
		return bytesFormat(BytesFormats.detectVersion(data)).parse(data);
	}
	
	public static PartFormat<String> currentStringFormat() {
		StringFormats[] fmt = StringFormats.values();
		return fmt[fmt.length-1];
	}
	
	public static PartFormat<byte[]> currentBytesFormat() {
		BytesFormats[] fmt = BytesFormats.values();
		return fmt[fmt.length-1];
	}
	
	private static enum StringFormats implements PartFormat<String> {
		VERSION_0 {

			private final String V = new BytesWritable().writeInt(0).toString();
			private final String DASHED32 = "((" + Base32.DIGIT.pattern() + "|-)+)";
			private final Pattern VALID = Pattern.compile(V + ":" + DASHED32 + "//" + DASHED32); 
					
			
			@Override
			public String format(Part part) {
				StringBuilder sb = new StringBuilder();
				BytesWritable w = new BytesWritable();
				
				sb.append(V + ":");
				sb.append(dash(w
						.writeInt(part.getLength())
						.writeBigInteger(part.getModulus())
						.reset()));
				sb.append("//");
				sb.append(dash(w
						.writeBigInteger(part.getPoint().getX())
						.writeBigInteger(part.getPoint().getY())
						.writeBytes(part.getChecksum().getChecksumBytes())
						.reset()));
				
				return sb.toString();
			}

			@Override
			public Part parse(String data) {
				Matcher m = VALID.matcher(data);
				if(!m.matches())
					throw new IllegalArgumentException("Not parseable by " + this);
				BytesReadable r;
				
				r = new BytesReadable(m.group(1).replace("-", ""));
				int length = r.readInt();
				BigInteger modulus = r.readBigInteger();
				
				r = new BytesReadable(m.group(3).replace("-", ""));
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				BigPoint point = new BigPoint(x, y);
				Checksum cx = new Checksum(r);
				Part part = new Part(0, length, -1, modulus, point);
				if(!cx.equals(part.getChecksum()))
					throw new IllegalArgumentException("Checksum mismatch");
				return part;
			}

			@Override
			public int getVersion() {
				return 0;
			}
			
		},
		
		VERSION_1 {

			private final String V = new BytesWritable().writeInt(1).toString();
			private final String DASHED32 = "((" + Base32.DIGIT.pattern() + "|-)+)";
			private final Pattern VALID = Pattern.compile(V + ":" + DASHED32 + "//" + DASHED32); 
					
			
			@Override
			public String format(Part part) {
				StringBuilder sb = new StringBuilder();
				BytesWritable w = new BytesWritable();
				
				sb.append(V + ":");
				sb.append(dash(w
						.writeInt(part.getLength())
						.writeInt(part.getRequiredParts())
						.writeBigInteger(part.getModulus())
						.reset()));
				sb.append("//");
				sb.append(dash(w
						.writeBigInteger(part.getPoint().getX())
						.writeBigInteger(part.getPoint().getY())
						.writeBytes(part.getChecksum().getChecksumBytes())
						.reset()));
				
				return sb.toString();
			}

			@Override
			public Part parse(String data) {
				Matcher m = VALID.matcher(data);
				if(!m.matches())
					throw new IllegalArgumentException("Not parseable by " + this);
				BytesReadable r;
				
				r = new BytesReadable(m.group(1).replace("-", ""));
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				
				r = new BytesReadable(m.group(3).replace("-", ""));
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				BigPoint point = new BigPoint(x, y);
				Checksum cx = new Checksum(r);
				Part part = new Part(0, length, requiredParts, modulus, point);
				if(!cx.equals(part.getChecksum()))
					throw new IllegalArgumentException("Checksum mismatch");
				return part;
			}

			@Override
			public int getVersion() {
				return 1;
			}
			
		}
		
		;
		
		private static String dash(String s) {
			s = s.replaceAll("(......)", "$1-");
			if(s.endsWith("-"))
				s = s.substring(0, s.length()-1);
			return s;
		}
		
		@Override
		public abstract String format(Part part);
		
		@Override
		public abstract Part parse(String data);
		
		@Override
		public abstract int getVersion();
		
		public static int detectVersion(String data) {
			return new BytesReadable(data.replaceAll(":.*", "")).readInt();
		}
	}
	
	private static enum BytesFormats implements PartFormat<byte[]> {
		VERSION_0 {

			@Override
			public byte[] format(Part part) {
				BytesWritable w = new BytesWritable();
				w.writeInt(0);
				w.writeInt(part.getLength());
				w.writeBigInteger(part.getModulus());
				w.writeBigInteger(part.getPoint().getX());
				w.writeBigInteger(part.getPoint().getY());
				return w.toByteArray();
			}

			@Override
			public Part parse(byte[] data) {
				BytesReadable r = new BytesReadable(data);
				if(r.readInt() != 0)
					throw new IllegalArgumentException("Not parsable by " + this);
				int length = r.readInt();
				BigInteger modulus = r.readBigInteger();
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				return new Part(0, length, -1, modulus, new BigPoint(x, y));
			}

			@Override
			public int getVersion() {
				return 0;
			}
			
		},
		
		VERSION_1 {

			@Override
			public byte[] format(Part part) {
				BytesWritable w = new BytesWritable();
				w.writeInt(1);
				w.writeInt(part.getLength());
				w.writeInt(part.getRequiredParts());
				w.writeBigInteger(part.getModulus());
				w.writeBigInteger(part.getPoint().getX());
				w.writeBigInteger(part.getPoint().getY());
				return w.toByteArray();
			}

			@Override
			public Part parse(byte[] data) {
				BytesReadable r = new BytesReadable(data);
				if(r.readInt() != 1)
					throw new IllegalArgumentException("Not parsable by " + this);
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				return new Part(0, length, requiredParts, modulus, new BigPoint(x, y));
			}

			@Override
			public int getVersion() {
				return 1;
			}
			
		}
		
		;
		
		@Override
		public abstract byte[] format(Part part);
		
		@Override
		public abstract Part parse(byte[] data);
		
		@Override
		public abstract int getVersion();

		public static int detectVersion(byte[] data) {
			return new BytesReadable(data).readInt();
		}
	}

	private PartFormats() {}
}
