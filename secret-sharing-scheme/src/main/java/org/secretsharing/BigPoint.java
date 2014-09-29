package org.secretsharing;

import java.math.BigInteger;

public class BigPoint {
	private BigInteger x;
	private BigInteger y;
	
	public BigPoint(BigInteger x, BigInteger y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public BigInteger getX() {
		return x;
	}

	public BigInteger getY() {
		return y;
	}
}
