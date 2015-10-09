package org.mitre.secretsharing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mitre.secretsharing.codec.PartFormats;

@RunWith(Parameterized.class)
public class PerByteSecretsTest {

	@Parameters
	public static Iterable<Object[]> params() {
		List<Object[]> p = new ArrayList<Object[]>();
		Random rnd = new Random(0L);
		for(int i = 1; i <= 32; i++) {
			byte[] b = new byte[i];
			rnd.nextBytes(b);
			p.add(new Object[] {b});
		}
		return p;
	}

	private byte[] b;
	
	public PerByteSecretsTest(byte[] b) {
		this.b = b;
	}
	
	@Test
	public void testSecret() {
		PerBytePart[] parts = Secrets.splitPerByte(b, 5, 3, new Random(0L));
		byte[] r = Secrets.joinPerByte(Arrays.copyOf(parts, 3));
		Assert.assertTrue(Arrays.equals(b, r));
	}
}
