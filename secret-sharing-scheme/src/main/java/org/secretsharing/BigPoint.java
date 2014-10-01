package org.secretsharing;

import java.math.BigInteger;

import org.secretsharing.util.BytesReadable;
import org.secretsharing.util.BytesWritable;

/**
 * An x/y point using {@link BigInteger} for its coordinates
 * @author robin
 *
 */
public class BigPoint {
	/**
	 * The X coordinate
	 */
	private BigInteger x;
	/**
	 * The Y coordinate
	 */
	private BigInteger y;
	
	/**
	 * Create a new {@link BigPoint} with the specified X and Y coordinates
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 */
	public BigPoint(BigInteger x, BigInteger y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return x + "," + y;
	}

	/**
	 * Return the X coordinate
	 * @return
	 */
	public BigInteger getX() {
		return x;
	}

	/**
	 * Return the Y coordinate
	 * @return
	 */
	public BigInteger getY() {
		return y;
	}
}
