package org.secretsharing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BytesSecrets {
	
	private static long toLong(byte[] b, int offset) {
		long v = 0;
		if(offset < b.length)
			v |= (b[offset] & 0xffL) << 56;
		if(offset + 1 < b.length)
			v |= (b[offset+1] & 0xffL) << 48;
		if(offset + 2 < b.length)
			v |= (b[offset+2] & 0xffL) << 40;
		if(offset + 3 < b.length)
			v |= (b[offset+3] & 0xffL) << 32;
		if(offset + 4 < b.length)
			v |= (b[offset+4] & 0xffL) << 24;
		if(offset + 5 < b.length)
			v |= (b[offset+5] & 0xffL) << 16;
		if(offset + 6 < b.length)
			v |= (b[offset+6] & 0xffL) << 8;
		if(offset + 7 < b.length)
			v |= b[offset+7] & 0xffL;
		return v;
	}
	
	private static byte[] fromLong(long v) {
		return new byte[] {
			(byte)(v >>> 56),
			(byte)(v >>> 48),
			(byte)(v >>> 40),
			(byte)(v >>> 32),
			(byte)(v >>> 24),
			(byte)(v >>> 16),
			(byte)(v >>> 8),
			(byte) v
		};
	}
	
	public static byte[][] split(byte[] secret, int totalParts, int requiredParts) {
		String[] parts = new String[totalParts];
		for(int i = 0; i < parts.length; i++)
			parts[i] = String.valueOf(secret.length) + "," + totalParts + "," + requiredParts;
		for(int pos = 0; pos < secret.length; pos += 8) {
			BigDecimal[][] part = LongSecrets.split(toLong(secret, pos), totalParts, requiredParts);
			for(int i = 0; i < parts.length; i++)
				parts[i] += "," + part[i][0] + "," + part[i][1];
		}
		
		byte[][] ret = new byte[totalParts][];
		for(int i = 0; i < parts.length; i++) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				GZIPOutputStream gout = new GZIPOutputStream(bout);
				gout.write(parts[i].getBytes("UTF-8"));
				gout.finish();
				gout.close();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			ret[i] = bout.toByteArray();
		}
		
		return ret;
	}
	
	public static byte[] join(byte[][] parts) {
		String[] p = new String[parts.length];
		for(int i = 0; i < p.length; i++) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ByteArrayInputStream bin = new ByteArrayInputStream(parts[i]);
			try {
				GZIPInputStream gin = new GZIPInputStream(bin);
				byte[] buf = new byte[1024];
				for(int r = gin.read(buf); r != -1; r = gin.read(buf))
					bout.write(buf, 0, r);
				p[i] = new String(bout.toByteArray(), "UTF-8");
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		int len = -1;
		int posmax = -1;
		int requiredParts = -1;
		String[][] ps = new String[parts.length][];
		for(int i = 0; i < p.length; i++) {
			ps[i] = p[i].split(",");
			int pl = Integer.parseInt(ps[i][0]);
			if(len == -1)
				len = pl;
			else if(len != pl)
				throw new RuntimeException("part length mismatch");
			int pm = ps[i].length;
			if(posmax == -1)
				posmax = pm;
			else if(posmax != pm)
				throw new RuntimeException("part length mismatch");
			int rp = Integer.parseInt(ps[i][2]);
			if(requiredParts == -1)
				requiredParts = rp;
			else if(requiredParts != rp)
				throw new RuntimeException("required parts mismatch");
		}
		byte[] secret = new byte[0];
		for(int pos = 3; pos < posmax; pos += 2) {
			BigDecimal[][] dp = new BigDecimal[requiredParts][2];
			for(int i = 0; i < requiredParts; i++) {
				dp[i][0] = new BigDecimal(ps[i][pos]);
				dp[i][1] = new BigDecimal(ps[i][pos+1]);
			}
			long sp = LongSecrets.join(dp);
			secret = Arrays.copyOf(secret, secret.length + 8);
			System.arraycopy(fromLong(sp), 0, secret, secret.length-8, 8);
		}
		return Arrays.copyOf(secret, len);
	}
	
	private BytesSecrets() {}
}
