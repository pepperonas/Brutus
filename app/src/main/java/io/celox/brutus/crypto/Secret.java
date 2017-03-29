/*
 * Copyright (c) 2017 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.celox.brutus.crypto;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.binary.Base32;

/**
 * Utility class for generating TOTP Secrets
 * 
 * @author Gregor "hrax" Magdolen
 */
public abstract class Secret {

	private static final Random rand = new Random();
	
	public enum Size {
		DEFAULT(20),
		MEDIUM(32),
		LARGE(64);
		
		private int size;
		
		Size(int size) {
			this.size = size;
		}
		
		public int getSize() {
			return size;
		}
	}
	
	/**
	 * Generates random 20 bytes
	 * 
	 * @return generated secret
	 */
	public static final byte[] generate() {
		return generate(Size.DEFAULT);
	}
	
	/**
	 * Generates random bytes of given size
	 * 
	 * @return generated secret
	 */
	public static final byte[] generate(Size size) {
		byte[] b = new byte[size.getSize()];
		rand.nextBytes(b);
		return Arrays.copyOf(b, size.getSize());
	}
	
	/**
	 * Encodes TOTP Secret to Base32
	 * 
	 * @param secret the secret to use
	 * @return encoded secret
	 * @see Base32
	 */
	public static final String toBase32(byte[] secret) {
		return new String(new Base32().encode(secret));
	}
	
	/**
	 * Decodes Base32 TOTP Secret to bytes
	 * 
	 * @param base32 the base32 to use
	 * @return decoded secret
	 * @see Base32
	 */
	public static final byte[] fromBase32(String base32) {
		return new Base32().decode(base32);
	}
	
	public static final String toHex(byte[] secret) {
		return String.format("%x", new BigInteger(1, secret));
	}
	
	public static final byte[] fromHex(String hex) {
		// Adding one byte to get the right conversion
        // Values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex,16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length; i++)
            ret[i] = bArray[i+1];
        return ret;
	}
	
}
