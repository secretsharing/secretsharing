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
import java.util.Arrays;

import org.mitre.secretsharing.codec.PartFormats;
import org.mitre.secretsharing.util.InputValidation;

/**
 * Utility class for holding split parts of a secret
 * @author Robin Kirkman
 *
 */
public class Part {
	/**
	 * The public components of a secret part.  These are the same
	 * for all parts of a secret.
	 * @author Robin Kirkman
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
		 * @param length The length, in bytes, of the secret
		 * @param requiredParts The number of parts required to reconstruct the secret
		 * @param modulus The modulus of the secret polynomial
		 */
		private PublicSecretPart(int length, int requiredParts, BigInteger modulus) {
			InputValidation.begin()
				.when(length < 0, "length is less than 0")
				.when(requiredParts < 1, "requiredParts is less than 1")
				.when(modulus == null, "modulus is null")
				.validate();
			this.length = length;
			this.requiredParts = requiredParts;
			this.modulus = modulus;
		}
		
		/**
		 * The length (in bytes) of the secret
		 * @return The length in bytes
		 */
		public int getLength() {
			return length;
		}

		/**
		 * The number of parts required to reconstruct the secret
		 * @return The number of required parts
		 */
		public int getRequiredParts() {
			return requiredParts;
		}
		
		/**
		 * The prime modulus for the key parts
		 * @return The polynomial modulus
		 */
		public BigInteger getModulus() {
			return modulus;
		}
	}
	
	/**
	 * The private component of a secret part.  This is different for every secret part.
	 * @author Robin Kirkman
	 *
	 */
	public static class PrivateSecretPart {
		/**
		 * The point on the polynomial
		 */
		private BigPoint point;
		
		/**
		 * Create a new {@link PrivateSecretPart}
		 * @param point The point on the polynomial
		 */
		private PrivateSecretPart(BigPoint point) {
			InputValidation.begin().when(point == null, "point is null").validate();
			this.point = point;
		}

		/**
		 * Return the point on the polynomial
		 * @return The point
		 */
		public BigPoint getPoint() {
			return point;
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
	 * @param version The format version this part was read from
	 * @param publicPart The public secret part
	 * @param privatePart The private secret part
	 */
	public Part(int version, PublicSecretPart publicPart, PrivateSecretPart privatePart) {
		InputValidation.begin()
			.when(publicPart == null, "publicPart is null")
			.when(privatePart == null, "privatePart is null")
			.validate();
		this.version = version;
		this.publicPart = publicPart;
		this.privatePart = privatePart;
	}
	
	/**
	 * Create a new {@link Part}
	 * @param length The length, in bytes, of the shared secret
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param modulus The modulus of the polynomial representing the shared secret
	 * @param point The polynomial point representing the secret part
	 */
	public Part(int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(PartFormats.currentStringFormat().getVersion(), new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}
	
	/**
	 * Create a new {@link Part}
	 * @param version The format version this Part was read from 
	 * @param length The length in bytes of the secret
	 * @param requiredParts The number of parts required to reconstruct the secret
	 * @param modulus The modulus of the secret polynomial
	 * @param point The point on the secret polynomial
	 */
	public Part(int version, int length, int requiredParts, BigInteger modulus, BigPoint point) {
		this(version, new PublicSecretPart(length, requiredParts, modulus), new PrivateSecretPart(point));
	}

	/**
	 * Copy constructor
	 * @param other The {@link Part} to copy
	 */
	private Part(Part other) {
		this(other.getVersion(), other.getPublicPart(), other.getPrivatePart());
	}
	
	/**
	 * Parse a string representation of a {@link Part}
	 * @param s The string to parse
	 */
	public Part(String s) {
		this(PartFormats.parse(s));
	}
	
	@Override
	public String toString() {
		return PartFormats.currentStringFormat().format(this);
	}

	/**
	 * Return the public part of this secret part.  This information should be
	 * shared with everyone given a part of the secret.
	 * @return The {@link PublicSecretPart}
	 */
	public PublicSecretPart getPublicPart() {
		return publicPart;
	}

	/**
	 * Return the private part of this secret part.  This information should not be
	 * shared with anyone else.
	 * @return The {@link PrivateSecretPart}
	 */
	public PrivateSecretPart getPrivatePart() {
		return privatePart;
	}
	
	/**
	 * Return the format version of this secret part.
	 * @see PartFormats#currentBytesFormat()
	 * @see PartFormats#currentStringFormat()
	 * @return The format version.
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Return the length in bytes of the secret
	 * @return The length of the secret
	 */
	public int getLength() {
		return getPublicPart().getLength();
	}
	
	/**
	 * Return the number of parts required to reconstruct the secret
	 * @return The required number of parts
	 */
	public int getRequiredParts() {
		return getPublicPart().getRequiredParts();
	}
	
	/**
	 * Return the prime modulus for the secret parts
	 * @return The prime modulus
	 */
	public BigInteger getModulus() {
		return getPublicPart().getModulus();
	}
	
	/**
	 * Return the point on the polynomial.
	 * For a {@link PerBytePart} secret part, where each point's Y value
	 * is constrained by {@link PerBytePart#MODULUS} to two bytes,
	 * multiple Y values will be encoded in the Y coordinate of this point.
	 * In this case the X value is shared for all Y values.
	 * @return The point
	 */
	public BigPoint getPoint() {
		return getPrivatePart().getPoint();
	}
	
	/**
	 * Join this {@link Part} with an array of other {@link Part}s of the same
	 * type to reconstruct a secret.  {@link Part} may not be joined with {@link PerBytePart}. 
	 * @param otherParts Array of other parts to join with this one.
	 * @return The reconstructed secret
	 */
	/*
	 * Overridden by PerBytePart
	 */
	public byte[] join(Part... otherParts) {
		InputValidation iv = InputValidation.begin()
				.when(otherParts == null, "otherParts is null")
				.validate();
		Part[] parts = Arrays.copyOf(otherParts, otherParts.length + 1);
		parts[parts.length - 1] = this;
		for(Part p : parts) {
			iv
				.when(p instanceof PerBytePart, "cannot apply multibyte join to perbyte parts")
				.validate();
		}
		return Secrets.joinMultibyte(parts);
	}
}
