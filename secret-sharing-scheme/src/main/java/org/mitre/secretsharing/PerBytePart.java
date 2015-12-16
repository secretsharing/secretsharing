package org.mitre.secretsharing;

import java.math.BigInteger;

/**
 * A part of a shared secret that was first split into individual bytes.<p>
 * 
 * Unlike {@link Part}, which uses a single Y coordinate, {@link PerBytePart} uses
 * an individual two-byte Y coordinate for each byte of the shared secret.  The single X
 * coordinate is used for all Y coordinates.
 * 
 * @author Robin Kirkman
 *
 */
public class PerBytePart extends Part {
	public static final BigInteger MODULUS = BigInteger.valueOf(65521);

	/**
	 * Create a {@link PerBytePart} to hold a per-byte secret part
	 * @param version The version this part was formatted with
	 * @param length The number of bytes in the secret
	 * @param requiredParts How many parts are required to reconstruct the secret
	 * @param point The {@link BigPoint} representing this secret part
	 */
	public PerBytePart(int version, int length, int requiredParts, BigPoint point) {
		super(version, length, requiredParts, MODULUS, point);
	}

	@Override
	public byte[] join(Part... otherParts) {
		PerBytePart[] parts = new PerBytePart[otherParts.length + 1];
		parts[0] = this;
		for(int i = 0; i < otherParts.length; i++) {
			if(!(otherParts[i] instanceof PerBytePart))
				throw new IllegalArgumentException("Cannot join single-point and per-byte-point secret parts");
			parts[i+1] = (PerBytePart) otherParts[i];
		}
		return Secrets.joinPerByte(parts);
	}
}
