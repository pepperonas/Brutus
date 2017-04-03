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

package io.celox.brutus;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import io.celox.brutus.crypto.TotpManager;
import java.math.BigInteger;
import org.apache.commons.codec.binary.Base32;

/**
 * The type Utilities.
 *
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Utilities {

    private static final String TAG = "Utilities";


    /**
     * To hex string.
     *
     * @param secret the secret
     * @return the string
     */
    public static final String toHex(@NonNull byte[] secret) {
        return String.format("%x", new BigInteger(1, secret));
    }


    /**
     * From base 32 byte [ ].
     *
     * @param base32 the base 32
     * @return the byte [ ]
     */
    public static final byte[] fromBase32(@NonNull String base32) {
        return new Base32().decode(base32);
    }


    /**
     * Generate one time password string.
     *
     * @param privateKey the private key
     * @return the string
     */
    public static String generateOneTimePassword(@NonNull String privateKey) {
        try {
            byte[] decodedKey = Utilities.fromBase32(privateKey);
            String hexKey = Utilities.toHex(decodedKey);
            TotpManager totpManager = new TotpManager();
            String code = totpManager.generate(decodedKey);
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Expand touch area.
     *
     * @param view the view
     */
    public static void expandTouchArea(View view) {
        final View parent = (View) view.getParent();
        parent.post(() -> {
            final Rect r = new Rect();
            view.getHitRect(r);
            r.top -= 8;
            r.bottom += 8;
            parent.setTouchDelegate(new TouchDelegate(r, view));
        });
    }

    public static void hideKeyboard(View view, Context ctx) {
        if (view != null) {
            InputMethodManager imm =
                (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
