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

package org.secretsharing;

import java.math.BigInteger;
import java.util.Random;

import org.secretsharing.util.BigIntegers;

/**
 * Utility class for splitting and joining secret and secret parts
 * @author robin
 *
 */
public class Secrets {
	/**
	 * Split a secret into a number of parts
	 * @param secret The secret to split
	 * @param totalParts The number of parts to create
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param rnd A source of random
	 * @return
	 */
	public static Part[] split(byte[] secret, int totalParts, int requiredParts, Random rnd) {
		int secretBytes = secret.length;
		TermPolynomial poly = new TermPolynomial(new BigInteger(secret), secretBytes * 8, requiredParts-1, rnd);
		BigPoint[] pts = poly.p(BigIntegers.range(1, totalParts + 1));
		Part[] s = new Part[totalParts];
		for(int i = 0; i < totalParts; i++)
			s[i] = new Part(secretBytes, requiredParts, poly.getModulus(), pts[i]);
		return s;
	}
	
	/**
	 * Recover a secret from its parts
	 * @param parts
	 * @return
	 */
	public static byte[] join(Part[] parts) {
		BigPoint[] pts = new BigPoint[parts.length];
		Integer secretLength = null;
		Integer requiredParts = null;
		BigInteger prime = null;
		for(int i = 0; i < pts.length; i++) {
			int sl = parts[i].getLength();
			BigInteger p = parts[i].getModulus();
			
			if(secretLength == null)
				secretLength = sl;
			else if(!secretLength.equals(sl))
				throw new IllegalArgumentException();
			if(requiredParts == null)
				requiredParts = parts[i].getRequiredParts();
			else if(!requiredParts.equals(parts[i].getRequiredParts()))
				throw new IllegalArgumentException();
			if(prime == null)
				prime = p;
			else if(!prime.equals(p))
				throw new IllegalArgumentException();
			pts[i] = parts[i].getPoint();
		}
		if(requiredParts > 0 && parts.length < requiredParts)
			throw new IllegalArgumentException(requiredParts + " parts are required but only " + parts.length + " supplied");
		TermPolynomial poly = new TermPolynomial(pts, prime);
		byte[] secret = poly.y(BigInteger.ZERO).getNumerator().mod(prime).add(prime).mod(prime).toByteArray();
		byte[] ret = new byte[secretLength];
		System.arraycopy(secret, 0, ret, ret.length - secret.length, secret.length);
		return ret;
	}
	
	private Secrets() {}
}
