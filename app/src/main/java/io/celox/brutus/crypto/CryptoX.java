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


import android.util.Log;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.jbasx.security.CryptUtils;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CryptoX {

    private static final String TAG = "CryptoX";

    private static final int ITERATIONS = 10;
    private static final int KEY_LENGTH = 4096;


    public static String hashLoopAesHash(String password) {
        String hashedPassword = toHexString(password.getBytes());
        for (int i = 0; i < AesPrefs.getInt("iterations_aes", -1); i++) {
            hashedPassword = CryptUtils
                .aesEncrypt(password, hashedPassword, AesPrefs.getLong("iv", -1L));
        }

        hashedPassword = toHexString(hashedPassword.getBytes());

        String encryptedKey = AesPrefs.get("the_key", "");

        return CryptUtils.aesEncrypt(encryptedKey, hashedPassword, AesPrefs.getLong("iv", -1L));
    }


    public CryptoX(String password) {
        String encryptionKey;

        long start = System.currentTimeMillis();

        /**
         * time based IV (stored as clear text along with the encryption)
         * */
        Long iv = System.currentTimeMillis();

        // hash the user's password
        String hashedPassword = toHexString(password.getBytes());

        /**
         * encrypt the hash with the password. since this is a time consuming task,
         * multiple iterations will protect the app from brute-force.
         * */
        for (int i = 0; i < ITERATIONS; i++) {
            // abc -> abc.def -> abc.def.ghi
            hashedPassword = CryptUtils.aesEncrypt(password, hashedPassword, iv);
        }

        // hash again
        hashedPassword = toHexString(hashedPassword.getBytes());

        /**
         * generate a random string to encrypt the hashed password.
         * by this the encryption key get's derived.
         * IMPORTANT: {@link encryptionKey} MUST NEVER be stored!!!
         * */
        String key = generateString(KEY_LENGTH);

        /**
         * deriving the {@link encryptionKey}
         * */
        encryptionKey = CryptUtils.aesEncrypt(key, hashedPassword, iv);

        // store the setup
        AesPrefs.putLong("iv", iv);
        AesPrefs.putInt("iterations_aes", ITERATIONS);
        AesPrefs.put("the_key", key);

        ToastUtils.toastShort(
            (System.currentTimeMillis() - start) + "ms for " + ITERATIONS + " iterations");

        Log.e(TAG, "~~~~~~ REMOVE ~~~~~~");
        Log.v(TAG, "CryptoX:            key: " + key);
        Log.w(TAG, "CryptoX: password plain: " + password);
        Log.v(TAG, "CryptoX:             iv: " + iv);
        Log.v(TAG, "CryptoX:         hashed: " + hashedPassword);
        Log.e(TAG, "CryptoX: enc (sec key): " + encryptionKey);
        Log.e(TAG,
            "CryptoX: dec  (key was): " + CryptUtils.aesDecrypt(hashedPassword, encryptionKey, iv));
        Log.e(TAG, "~~~~~~ REMOVE ~~~~~~");

        // TODO 2017-03-24 04-14: remove!
        testEncryption(encryptionKey);
    }

    private void testEncryption(String encrypted) {
        long iv = System.currentTimeMillis();
        AesPrefs.putLong("iv_2", iv);
        String testData = "Hallo, ich bin der Fritz!";
        AesPrefs.put("test_data", CryptUtils.aesEncrypt(encrypted, testData, iv));
    }

    public static void testDecryption(String password) {
        Log.i(TAG, "testDecryption: " + CryptUtils
            .aesDecrypt(hashLoopAesHash(password), AesPrefs.get("test_data", ""),
                AesPrefs.getLong("iv_2", 0)));
    }


    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return sb.toString();
    }

    private int generateInteger() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return ThreadLocalRandom.current().nextInt(12, 29 + 1);
        } else {
            return new Random().nextInt((29 - 12) + 1) + 12;
        }
    }

    private String generateString(int len) {
        return new StringGenerator(len).randomString();
    }


    private class StringGenerator {

        private int len = -1;

        private StringGenerator(int len) {
            this.len = len;
        }

        static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        String randomString() {
            StringBuilder sb = new StringBuilder(this.len);
            for (int i = 0; i < this.len; i++) {
                sb.append(AB.charAt(rnd.nextInt(AB.length())));
            }
            return sb.toString();
        }
    }

}