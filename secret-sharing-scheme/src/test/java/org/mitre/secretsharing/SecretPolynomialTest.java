/*

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

 */

package org.mitre.secretsharing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
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
