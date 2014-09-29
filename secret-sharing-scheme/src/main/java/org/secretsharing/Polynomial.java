package org.secretsharing;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Polynomial {

	public Term y(BigInteger x);
}