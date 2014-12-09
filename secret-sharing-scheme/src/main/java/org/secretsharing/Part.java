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

import org.secretsharing.codec.Checksum;
import org.secretsharing.codec.PartFormats;

/**
 * Utility class for holding split parts of a secret
 * @author robin
 *
 */
public class Part {
	/**
	 * The public components of a secret part.  These are the same
	 * for all parts of a secret.
	 * @author robin
	 *
	 */
	public static class PublicSecretPart {
		/**
		 * The number of bytes in the secret
		 */
		private int length;
		/**
		 * The number of parts required to reconstruct the secret
		 */
		private int requiredParts = -1;
		/**
		 * The prime modulo for the secret parts
		 */
		private BigInteger modulus;
		
		/**
		 * Create a new {@link PublicSecretPart}
		 * @param length
		 * @param modulus
		 */
		private PublicSecretPart(int length, int requiredParts, BigInteger modulus) {
			if(modulus == null)
				throw new IllegalArgumentException();
			this.length = length;
			this.requiredParts = requiredParts;
			this.modulus = modulus;
		}
		
		/**
		 * The length (in bytes) of the secret
		 * @return
		 */
		public int getLength() {
			return length;
		}

		/**
		 * The number of parts required to reconstruct the secret
		 * @return
		 */
		public int getRequiredParts() {
			return requiredParts;
		}
		
		/**
		 * The prime modulus for the key parts
		 * @return
		 */
		public BigInteger getModulus() {
			return modulus;
		}
	}
	
	/**
	 * The private component of a secret part.  This is different for every secret part.
	 * @author robin
	 *
	 */
	public static class PrivateSecretPart {
		/**
		 * The point on the polynomial
		 */
		private BigPoint point;
		/**
		 * The checksum of that point
		 */
		private Checksum cx;
		
		/**
		 * Create a new {@link PrivateSecretPart}
		 * @param point
		 */
		private PrivateSecretPart(BigPoint point) {
			if(point == null)
				throw new IllegalArgumentException();
			this.point = point;
			cx = new Checksum(point);
		}

		/**
		 * Return the point on the polynomial
		 * @return
		 */
		public BigPoint getPoint() {
			return point;
		}
		
		public Checksum getChecksum() {
			return cx;
		}
	}
	
	/**
	 * The format version of this {@link Part}
	 */
	private int version;
	/**
	 * The public component of the part
	 */
	private PublicSecretPart publicPart;
	/**
	 * The private component of the part
	 */
	private PrivateSecretPart privatePart;
	
	/**
	 * Create a new {@link Part}
	 * @param version
	 * @param publicPart
	 * @param privatePart
	 */
	public Part(int version, PublicSecretPart publicPart, PrivateSecretPart privatePart) {
		this.version = version;
		this.publicPart = publicPart;
		this.privatePart = privatePart;
	}
	
	/**
	 * Create a new {@link Part}
	 * @param length
	 * @param modulus
	 * @param point
	 */
	public Part(int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(PartFormats.currentStringFormat().getVersion(), new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}
	
	/**
	 * Create a new {@link Part}
	 * @param length
	 * @param modulus
	 * @param point
	 */
	public Part(int version, int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(version, new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}

	private Part(Part other) {
		this(other.getVersion(), other.getPublicPart(), other.getPrivatePart());
	}
	
	/**
	 * Parse a string representation of a {@link Part}
	 * @param s
	 */
	public Part(String s) {
		this(PartFormats.parse(s));
	}
	
	@Override
	public String toString() {
		return PartFormats.currentStringFormat().format(this);
	}

	/**
	 * Return the public part of this secret part
	 * @return
	 */
	public PublicSecretPart getPublicPart() {
		return publicPart;
	}

	/**
	 * Return the private part of this secret part
	 * @return
	 */
	public PrivateSecretPart getPrivatePart() {
		return privatePart;
	}
	
	/**
	 * Return the format version of this secret part
	 * @return
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Return the length in bytes of the secret
	 * @return
	 */
	public int getLength() {
		return getPublicPart().getLength();
	}
	
	public int getRequiredParts() {
		return getPublicPart().getRequiredParts();
	}
	
	/**
	 * Return the prime modulus for the secret parts
	 * @return
	 */
	public BigInteger getModulus() {
		return getPublicPart().getModulus();
	}
	
	/**
	 * Return the point on the polynomial
	 * @return
	 */
	public BigPoint getPoint() {
		return getPrivatePart().getPoint();
	}
	
	public Checksum getChecksum() {
		return getPrivatePart().getChecksum();
	}
}
