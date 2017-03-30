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

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * The type Crypt.
 *
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 *
 * Loading times measured on device:
 *
 * PHONE = Google Pixel (late 2016)
 *
 * LAPTOP = Macbook Pro (mid 2015)
 */
public class Crypt {

    /**
     * The constant DERIVATION_ITERATION_COUNT.
     */
    public static final int DERIVATION_ITERATION_COUNT = 65536;
    /**
     * The constant KEY_SIZE.
     */
    public static final int KEY_SIZE = 256;

    /**
     * The type Crypt set.
     */
    public static class CryptSet {

        private byte[] encrypted;
        private byte[] iv;

        /**
         * Instantiates a new Crypt set.
         *
         * @param encrypted the encrypted
         * @param iv the iv
         */
        public CryptSet(byte[] encrypted, byte[] iv) {
            this.encrypted = encrypted;
            this.iv = iv;
        }

        /**
         * Get encrypted byte [ ].
         *
         * @return the byte [ ]
         */
        public byte[] getEncrypted() {
            return encrypted;
        }

        /**
         * Get iv byte [ ].
         *
         * @return the byte [ ]
         */
        public byte[] getIv() {
            return iv;
        }
    }

    /**
     * Encrypt crypt set.
     *
     * @param password the password
     * @param salt the salt
     * @param text the text
     * @return the crypt set
     */
    public static CryptSet encrypt(String password, String salt, String text) {
        byte[] ciphertext = null, iv = null;
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
            DERIVATION_ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = null;
        try {
            if (factory != null) {
                tmp = factory.generateSecret(spec);
            }
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        SecretKey secret = null;
        if (tmp != null) {
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                cipher.init(Cipher.ENCRYPT_MODE, secret);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                ciphertext = cipher.doFinal(text.getBytes("UTF-8"));
            }
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AlgorithmParameters params = null;
        if (cipher != null) {
            params = cipher.getParameters();
        }
        try {
            if (params != null) {
                iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            }
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return new CryptSet(ciphertext, iv);
    }

    /**
     * Decrypt string.
     *
     * @param password the password
     * @param salt the salt
     * @param iv the iv
     * @param encryptedBytes the encryptedBytes
     * @return the string
     */
    public static String decrypt(String password, String salt, byte[] iv, byte[] encryptedBytes) {
        String decrypted = null;
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
            DERIVATION_ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = null;
        try {
            if (factory != null) {
                tmp = factory.generateSecret(spec);
            }
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        SecretKey secret = null;
        if (tmp != null) {
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            }
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                decrypted = new String(cipher.doFinal(encryptedBytes), "UTF-8");
            }
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return decrypted;
    }


    /**
     * Gets secret key.
     *
     * It's time-consuming! Derive as less as possible.
     *
     * Phone: ~ 1500ms
     * Laptop: ~ 330ms
     *
     * @param password the password
     * @param salt the salt
     * @return the secret key
     */
    public static SecretKey getSecretKey(String password, String salt) {
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
            DERIVATION_ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = null;
        try {
            if (factory != null) {
                tmp = factory.generateSecret(spec);
            }
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        if (tmp != null) {
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        }
        return null;
    }


    /**
     * Enc byte [ ].
     *
     * Phone: ~ 5ms
     * Laptop: ~ 1ms
     *
     * @param secret the secret
     * @param text the text
     * @return the byte [ ]
     */
    public static CryptSet enc(SecretKey secret, String text) {
        byte[] encrypted = null;
        byte[] iv = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                cipher.init(Cipher.ENCRYPT_MODE, secret);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        AlgorithmParameters params = null;
        if (cipher != null) {
            params = cipher.getParameters();
        }
        try {
            if (params != null) {
                iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            }
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            }
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new CryptSet(encrypted, iv);
    }


    /**
     * Dec string.
     *
     * Phone: ~ 5ms
     * Laptop: < 1ms
     *
     * @param secret the secret
     * @param iv the iv
     * @param encrypted the encrypted
     * @return the string
     */
    public static String dec(SecretKey secret, byte[] iv, byte[] encrypted) {
        String decrypted = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            }
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                decrypted = new String(cipher.doFinal(encrypted), "UTF-8");
            }
        } catch (UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decrypted;
    }

}
