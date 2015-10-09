package org.mitre.secretsharing;

import java.math.BigInteger;
import java.util.Arrays;

public class PerBytePart extends Part {
	public static final BigInteger MODULUS = BigInteger.valueOf(65521);

	public PerBytePart(int version, int length, int requiredParts, BigPoint point) {
		super(version, length, requiredParts, MODULUS, point);
	}

	@Override
	public byte[] join(Part... otherParts) {
		PerBytePart[] parts = new PerBytePart[otherParts.length + 1];
		parts[0] = this;
		for(int i = 0; i < otherParts.length; i++)
			parts[i+1] = (PerBytePart) otherParts[i];

		return Secrets.joinPerByte(parts);
	}
}
