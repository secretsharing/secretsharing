package org.secretsharing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BytesSecrets {
	
	private static int toInt(byte[] b, int offset) {
		int i = 0;
		if(offset < b.length)
			i |= (b[offset] & 0xff) << 24;
		if(offset + 1 < b.length)
			i |= (b[offset+1] & 0xff) << 16;
		if(offset + 2 < b.length)
			i |= (b[offset+2] & 0xff) << 8;
		if(offset + 3 < b.length)
			i |= b[offset+3] & 0xff;
		return i;
	}
	
	private static byte[] fromInt(int i) {
		return new byte[] {
			(byte)(i >>> 24),
			(byte)(i >>> 16),
			(byte)(i >>> 8),
			(byte) i
		};
	}
	
	public static byte[][] split(byte[] secret, int totalParts, int requiredParts) {
		String[] parts = new String[totalParts];
		for(int i = 0; i < parts.length; i++)
			parts[i] = String.valueOf(secret.length);
		for(int pos = 0; pos < secret.length; pos += 4) {
			BigDecimal[][] part = IntSecrets.split(toInt(secret, pos), totalParts, requiredParts);
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
		String[][] ps = new String[parts.length][];
		for(int i = 0; i < p.length; i++) {
			ps[i] = p[i].split(",");
			int pl = Integer.parseInt(ps[i][0]);
			if(len == -1)
				len = pl;
			else if(len != pl)
				throw new RuntimeException("part length mismatch");
		}
		byte[] secret = new byte[0];
		for(int pos = 1; pos < ps[0].length; pos += 2) {
			BigDecimal[][] dp = new BigDecimal[ps.length][2];
			for(int i = 0; i < ps.length; i++) {
				dp[i][0] = new BigDecimal(ps[i][pos]);
				dp[i][1] = new BigDecimal(ps[i][pos+1]);
			}
			int sp = IntSecrets.join(dp);
			secret = Arrays.copyOf(secret, secret.length + 4);
			System.arraycopy(fromInt(sp), 0, secret, secret.length-4, 4);
		}
		return Arrays.copyOf(secret, len);
	}
	
	private BytesSecrets() {}
}
