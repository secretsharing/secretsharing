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

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mitre.secretsharing.BigPoint;
import org.mitre.secretsharing.Part;
import org.mitre.secretsharing.PerBytePart;
import org.mitre.secretsharing.util.BytesReadable;
import org.mitre.secretsharing.util.BytesWritable;
import org.mitre.secretsharing.util.InputValidation;
/**
 * Static class for use when converting secret parts to and from their string or
 * byte representations.
 * @author Robin Kirkman
 *
 */
public abstract class PartFormats {
	/**
	 * Return a specific version of the {@link String} format
	 * @param version The version
	 * @return The format with that version
	 */
	public static PartFormat<String> stringFormat(int version) {
		PartFormat<String> f = null;
		for(PartFormat<String> pf : StringFormats.values()) {
			if(version == pf.getVersion())
				f = pf;
		}
		InputValidation.begin().when(f == null, "no such string format version " + version).validate();
		return f;
	}
	
	/**
	 * Return a specific version of the {@code byte[]} format
	 * @param version
	 * @return
	 */
	public static PartFormat<byte[]> bytesFormat(int version) {
		PartFormat<byte[]> f = null;
		for(PartFormat<byte[]> pf : BytesFormats.values()) {
			if(version == pf.getVersion())
				f = pf;
		}
		InputValidation.begin().when(f == null, "no such byte[] format version " + version).validate();
		return f;
	}
	
	/**
	 * Parse a string-formatted {@link Part}, detecting the version from the string
	 * @param data The string version of the {@link Part}
	 * @return The parsed {@link Part}
	 */
	public static Part parse(String data) {
		InputValidation.begin().when(data == null, "data is null").validate();
		return stringFormat(StringFormats.detectVersion(data)).parse(data);
	}
	
	/**
	 * Parse a {@code byte[]}-formatted {@link Part}, detecting the version
	 * from the argument
	 * @param data The {@code byte[]} version of the {@link Part}
	 * @return The parsed {@link Part}
	 */
	public static Part parse(byte[] data) {
		InputValidation.begin().when(data == null, "data is null").validate();
		return bytesFormat(BytesFormats.detectVersion(data)).parse(data);
	}
	
	/**
	 * Return the most recent {@link String} format
	 * @return
	 */
	public static PartFormat<String> currentStringFormat() {
		StringFormats[] fmt = StringFormats.values();
		return fmt[fmt.length-1];
	}
	
	/**
	 * Return the most recent {@code byte[]} format
	 * @return
	 */
	public static PartFormat<byte[]> currentBytesFormat() {
		BytesFormats[] fmt = BytesFormats.values();
		return fmt[fmt.length-1];
	}
	
	/**
	 * Versions of the {@link String} format
	 * @author Robin Kirkman
	 * @see PartFormat
	 */
	public static enum StringFormats implements PartFormat<String> {
		/**
		 * Format version {@code 0}
		 */
		VERSION_0 {

			private final String V = new BytesWritable().writeInt(0).toString();
			
			@Override
			@SuppressWarnings("deprecation")
			public String format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
				StringBuilder sb = new StringBuilder();
				BytesWritable w = new BytesWritable();
				
				sb.append(V + ":");
				sb.append(dash(w
						.writeInt(part.getLength())
						.writeBigInteger(part.getModulus())
						.reset()));
				sb.append("//");
				Checksum cx = new Checksum(part.getPoint());
				sb.append(dash(w
						.writeBigInteger(part.getPoint().getX())
						.writeBigInteger(part.getPoint().getY())
						.writeBytes(cx.getChecksumBytes())
						.reset()));
				
				return sb.toString();
			}

			@Override
			@SuppressWarnings("deprecation")
			public Part parse(String data) {
				InputValidation.begin().when(data == null, "data is null").validate();
				ByteIterator[] bia = split(data);
				if(bia == null)
					throw new IllegalArgumentException("Not parseable by " + this);
				BytesReadable r;
				
				r = new BytesReadable(bia[0]);
				if(r.readInt() != getVersion())
					throw new IllegalArgumentException("Not parseable by " + this);
				
				r = new BytesReadable(bia[1]);
				int length = r.readInt();
				BigInteger modulus = r.readBigInteger();
				
				r = new BytesReadable(bia[2]);
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				BigPoint point = new BigPoint(x, y);
				Checksum cx = new Checksum(r);
				Part part = new Part(0, length, -1, modulus, point);
				if(!cx.equals(new Checksum(point)))
					throw new IllegalArgumentException("Checksum mismatch");
				return part;
			}

			@Override
			public int getVersion() {
				return 0;
			}
			
		},
		
		/**
		 * Format version {@code 1}
		 */
		VERSION_1 {

			private final String V = new BytesWritable().writeInt(1).toString();
					
			
			@Override
			@SuppressWarnings("deprecation")
			public String format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
				StringBuilder sb = new StringBuilder();
				BytesWritable w = new BytesWritable();
				
				BigInteger mod = part.getModulus();
				
				sb.append(V + ":");
				sb.append(dash(w
						.writeInt(part.getLength())
						.writeInt(part.getRequiredParts())
						.writeBigInteger(mod)
						.reset()));
				sb.append("//");
				Checksum cx = new Checksum(part.getPoint());
				sb.append(dash(w
						.writeBigInteger(part.getPoint().getX())
						.writeBigInteger(part.getPoint().getY())
						.writeBytes(cx.getChecksumBytes())
						.reset()));
				
				return sb.toString();
			}

			@Override
			@SuppressWarnings("deprecation")
			public Part parse(String data) {
				InputValidation.begin().when(data == null, "data is null").validate();
				ByteIterator[] bia = split(data);
				if(bia == null)
					throw new IllegalArgumentException("Not parseable by " + this);
				BytesReadable r;
				
				r = new BytesReadable(bia[0]);
				if(r.readInt() != getVersion())
					throw new IllegalArgumentException("Not parseable by " + this);

				r = new BytesReadable(bia[1]);
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				
				r = new BytesReadable(bia[2]);
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				BigPoint point = new BigPoint(x, y);
				Checksum cx = new Checksum(r);
				Part part;
				part = new Part(1, length, requiredParts, modulus, point);
				if(!cx.equals(new Checksum(point)))
					throw new IllegalArgumentException("Checksum mismatch");
				return part;
			}

			@Override
			public int getVersion() {
				return 1;
			}
			
		},

		/**
		 * Format version {@code 2}
		 */
		VERSION_2 {

			private final String V = new BytesWritable().writeInt(2).toString();
					
			
			@Override
			@SuppressWarnings("deprecation")
			public String format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
				StringBuilder sb = new StringBuilder();
				BytesWritable w = new BytesWritable();
				
				BigInteger mod = part.getModulus();
				if(part instanceof PerBytePart)
					mod = BigInteger.valueOf(-1);
				
				sb.append(V + ":");
				sb.append(dash(w
						.writeInt(part.getLength())
						.writeInt(part.getRequiredParts())
						.writeBigInteger(mod)
						.reset()));
				sb.append("//");
				Checksum cx = new Checksum(part.getPoint());
				sb.append(dash(w
						.writeBigInteger(part.getPoint().getX())
						.writeBigInteger(part.getPoint().getY())
						.writeBytes(cx.getChecksumBytes())
						.reset()));
				
				return sb.toString();
			}

			@Override
			@SuppressWarnings("deprecation")
			public Part parse(String data) {
				InputValidation.begin().when(data == null, "data is null").validate();
				ByteIterator[] bia = split(data);
				if(bia == null)
					throw new IllegalArgumentException("Not parseable by " + this);
				BytesReadable r;
				
				r = new BytesReadable(bia[0]);
				if(r.readInt() != getVersion())
					throw new IllegalArgumentException("Not parseable by " + this);
				
				r = new BytesReadable(bia[1]);
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				
				r = new BytesReadable(bia[2]);
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				BigPoint point = new BigPoint(x, y);
				Checksum cx = new Checksum(r);
				Part part;
				if(BigInteger.valueOf(-1).equals(modulus))
					part = new PerBytePart(2, length, requiredParts, point);
				else
					part = new Part(2, length, requiredParts, modulus, point);
				if(!cx.equals(new Checksum(point)))
					throw new IllegalArgumentException("Checksum mismatch");
				return part;
			}

			@Override
			public int getVersion() {
				return 2;
			}
			
		},

		/**
		 * Format version {@code 3}
		 */
		VERSION_3 {

			private final String V = new BytesWritable().writeInt(3).toString();
			
			@Override
			public String format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
				StringBuilder sb = new StringBuilder();
				BytesWritable w = new BytesWritable();
				
				BigInteger mod = part.getModulus();
				if(part instanceof PerBytePart)
					mod = BigInteger.valueOf(-1);
				
				sb.append(V + ":");
				sb.append(dash(w
						.writeInt(part.getLength())
						.writeInt(part.getRequiredParts())
						.writeBigInteger(mod)
						.reset()));
				sb.append("//");
				sb.append(dash(w
						.writeBigInteger(part.getPoint().getX())
						.writeBigInteger(part.getPoint().getY())
						.reset()));
				
				return sb.toString();
			}

			@Override
			public Part parse(String data) {
				InputValidation.begin().when(data == null, "data is null").validate();
				ByteIterator[] bia = split(data);
				if(bia == null)
					throw new IllegalArgumentException("Not parseable by " + this);
				BytesReadable r;
				
				r = new BytesReadable(bia[0]);
				if(r.readInt() != getVersion())
					throw new IllegalArgumentException("Not parseable by " + this);
				
				r = new BytesReadable(bia[1]);
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				
				r = new BytesReadable(bia[2]);
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				BigPoint point = new BigPoint(x, y);
				Part part;
				if(BigInteger.valueOf(-1).equals(modulus))
					part = new PerBytePart(3, length, requiredParts, point);
				else
					part = new Part(3, length, requiredParts, modulus, point);
				return part;
			}

			@Override
			public int getVersion() {
				return 3;
			}
			
		}

		;
		
		/**
		 * Return an array of {@link ByteIterator}, without dashes, of the three
		 * components of a secret part.
		 * @param s The string form of the scret part
		 * @return The array of components
		 */
		private static ByteIterator[] split(String s) {
			int vidx = s.indexOf(':');
			if(vidx < 0)
				return null;
			int pidx = s.indexOf("//", vidx);
			if(pidx < 0)
				return null;
			return new ByteIterator[] {
				Base32.decode(new UndashByteIterator(s, 0, vidx)),
				Base32.decode(new UndashByteIterator(s, vidx + 1, pidx)),
				Base32.decode(new UndashByteIterator(s, pidx + 2, s.length())),
			};
		}
		
		/**
		 * Add dashes to a string, every 5 characters
		 * @param s The string to add dashes to
		 * @return A string with dashes
		 */
		private static String dash(String s) {
			StringBuilder sb = new StringBuilder(s.length() * 6 / 5 + 1);
			for(int i = 0; i < s.length(); i += 5) {
				if(i > 0)
					sb.append('-');
				sb.append(s.substring(i, Math.min(i+5, s.length())));
			}
			return sb.toString();
		}
		
		@Override
		public abstract String format(Part part);
		
		@Override
		public abstract Part parse(String data);
		
		@Override
		public abstract int getVersion();
		
		/**
		 * Detect the version of a string-formatted secret part
		 * @param data The formatted part
		 * @return The version number
		 */
		public static int detectVersion(String data) {
			InputValidation.begin().when(data == null, "data is null").validate();
			int vidx = data.indexOf(':');
			ByteIterator bi = new UndashByteIterator(data, 0, vidx);
			bi = Base32.decode(bi);
			return new BytesReadable(bi).readInt();
		}
		
		/**
		 * Subclass of {@link StringByteIterator} that removes dashes
		 * from the input string
		 * @author Robin Kirkman
		 *
		 */
		private static class UndashByteIterator extends StringByteIterator {
			/**
			 * Create a new {@link UndashByteIterator}, which removes dashes when
			 * returning string bytes
			 * @param data The string to return as bytes
			 * @param start The starting position
			 * @param stop The stopping position
			 */
			public UndashByteIterator(String data, int start, int stop) {
				super(data, start, stop);
			}

			@Override
			protected boolean skip(char c) {
				return c == '-';
			}
			
		}
	}
	
	/**
	 * Versions of the {@code byte[]} format
	 * @author Robin Kirkman
	 * @see PartFormat
	 */
	public static enum BytesFormats implements PartFormat<byte[]> {
		/**
		 * Format version {@code 0}
		 */
		VERSION_0 {

			@Override
			public byte[] format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
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
				InputValidation.begin().when(data == null, "data is null").validate();
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
		
		/**
		 * Format version {@code 1}
		 */
		VERSION_1 {

			@Override
			public byte[] format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
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
				InputValidation.begin().when(data == null, "data is null").validate();
				BytesReadable r = new BytesReadable(data);
				if(r.readInt() != 1)
					throw new IllegalArgumentException("Not parsable by " + this);
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				return new Part(1, length, requiredParts, modulus, new BigPoint(x, y));
			}

			@Override
			public int getVersion() {
				return 1;
			}
			
		},

		/**
		 * Format version {@code 2}
		 */
		VERSION_2 {

			@Override
			public byte[] format(Part part) {
				InputValidation.begin().when(part == null, "part is null").validate();
				BytesWritable w = new BytesWritable();
				w.writeInt(2);
				w.writeInt(part.getLength());
				w.writeInt(part.getRequiredParts());
				w.writeBigInteger((part instanceof PerBytePart) ? BigInteger.valueOf(-1) : part.getModulus());
				w.writeBigInteger(part.getPoint().getX());
				w.writeBigInteger(part.getPoint().getY());
				return w.toByteArray();
			}

			@Override
			public Part parse(byte[] data) {
				InputValidation.begin().when(data == null, "data is null").validate();
				BytesReadable r = new BytesReadable(data);
				if(r.readInt() != 2)
					throw new IllegalArgumentException("Not parsable by " + this);
				int length = r.readInt();
				int requiredParts = r.readInt();
				BigInteger modulus = r.readBigInteger();
				BigInteger x = r.readBigInteger();
				BigInteger y = r.readBigInteger();
				if(BigInteger.valueOf(-1).equals(modulus))
					return new PerBytePart(2, length, requiredParts, new BigPoint(x, y));
				else
					return new Part(2, length, requiredParts, modulus, new BigPoint(x, y));
			}

			@Override
			public int getVersion() {
				return 2;
			}
			
		}

		;
		
		@Override
		public abstract byte[] format(Part part);
		
		@Override
		public abstract Part parse(byte[] data);
		
		@Override
		public abstract int getVersion();

		/**
		 * Detect the version of a {@code byte[]} formatted {@link Part}
		 * @param data The data to detect
		 * @return The version
		 */
		public static int detectVersion(byte[] data) {
			InputValidation.begin().when(data == null, "data is null").validate();
			return new BytesReadable(data).readInt();
		}
	}

	private PartFormats() {}
}
