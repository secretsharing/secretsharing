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

package org.mitre.secretsharing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.mitre.secretsharing.BigPoint;
import org.mitre.secretsharing.TermPolynomial;
import org.mitre.secretsharing.util.BigIntegers;

@RunWith(Parameterized.class)
public class SecretPolynomialTest {
	private static final Random rnd = new Random(0L);
	
	@Parameters
	public static Iterable<Object[]> params() {
		List<Object[]> p = new ArrayList<Object[]>();
		for(int i = 0; i < 100; i++) {
			BigInteger secret = new BigInteger(8, rnd);
			int powx = 2 + (i % 7);
			p.add(new Object[] {secret, powx});
		}
		return p;
	}
	
	private BigInteger secret;
	private int powx;
	
	public SecretPolynomialTest(BigInteger secret, int powx) {
		this.secret = secret;
		this.powx = powx;
	}
	
	@Test
	public void testReconstructSecret() {
		TermPolynomial sp = new TermPolynomial(secret, secret.bitLength(), powx, rnd);
		BigPoint[] pts = sp.p(BigIntegers.range(1, powx+2));
		TermPolynomial lp = new TermPolynomial(pts, sp.getModulus());
		BigInteger s = lp.wholeY(BigInteger.ZERO);
		Assert.assertEquals(secret, s);
	}
}
