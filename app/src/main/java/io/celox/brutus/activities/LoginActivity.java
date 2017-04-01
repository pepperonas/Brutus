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

package io.celox.brutus.activities;

import static android.Manifest.permission.READ_CONTACTS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.celox.brutus.R;
import io.celox.brutus.crypto.Crypt;
import io.celox.brutus.crypto.Crypt.CryptSet;
import io.celox.brutus.crypto.Crypt.KeySet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;

/**
 * A login screen that offers to set the password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    boolean testMakeCryptoConfig = true;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private TextView mTvHeadline;
    private View mViewSetPassword;
    private EditText mEtEnterPassword;
    private EditText mEtEnterPasswordAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEtEnterPassword = (EditText) findViewById(R.id.et_enter_password);
        mEtEnterPasswordAgain = (EditText) findViewById(R.id.et_enter_password_again);

        mEtEnterPassword.setText(R.string.password_debug);
        mEtEnterPasswordAgain.setText(R.string.password_debug);

        Button btnSetPassword = (Button) findViewById(R.id.btn_set_password);
        btnSetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptStorePassword();
            }
        });

        mViewSetPassword = findViewById(R.id.set_password_form);

        processEncryption();

//        processOneTimePassword("");

//        scan();
    }

    private void processEncryption() {
        long start = System.currentTimeMillis();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        if (testMakeCryptoConfig) {

            KeySet keySet = Crypt.getSecretKey("hallo", null);
            SecretKey key = keySet.getSecretKey();
            byte[] srBytes = keySet.getSalt();

            Log.d(TAG, "proCrypt: deltaE0=" + (System.currentTimeMillis() - start) + " ms");
            CryptSet enc1 = Crypt.enc(key, "Franz!");
            Log.d(TAG, "proCrypt: deltaE1=" + (System.currentTimeMillis() - start) + " ms");
            CryptSet enc2 = Crypt.enc(key, "Fronz!");
            CryptSet enc3 = Crypt.enc(key, "Frinz!");
            CryptSet enc4 = Crypt.enc(key, "Frünz!");
            Log.d(TAG, "proCrypt: deltaE2=" + (System.currentTimeMillis() - start) + " ms");

            String ee1 = Base64.encodeToString(enc1.getEncrypted(), Base64.DEFAULT);
            String ei1 = Base64.encodeToString(enc1.getIv(), Base64.DEFAULT);
            String ee2 = Base64.encodeToString(enc2.getEncrypted(), Base64.DEFAULT);
            String ei2 = Base64.encodeToString(enc2.getIv(), Base64.DEFAULT);
            String ee3 = Base64.encodeToString(enc3.getEncrypted(), Base64.DEFAULT);
            String ei3 = Base64.encodeToString(enc3.getIv(), Base64.DEFAULT);
            String ee4 = Base64.encodeToString(enc4.getEncrypted(), Base64.DEFAULT);
            String ei4 = Base64.encodeToString(enc4.getIv(), Base64.DEFAULT);
            Log.d(TAG, "proCrypt: deltaE3=" + (System.currentTimeMillis() - start) + " ms");

            String secRand = Base64.encodeToString(srBytes, Base64.DEFAULT);

            preferences.edit().putString("sr", secRand).apply();
            preferences.edit().putString("ee1", ee1).apply();
            preferences.edit().putString("ei1", ei1).apply();
            preferences.edit().putString("ee2", ee2).apply();
            preferences.edit().putString("ei2", ei2).apply();
            preferences.edit().putString("ee3", ee3).apply();
            preferences.edit().putString("ei3", ei3).apply();
            preferences.edit().putString("ee4", ee4).apply();
            preferences.edit().putString("ei4", ei4).apply();
            Log.d(TAG, "proCrypt: deltaE4=" + (System.currentTimeMillis() - start) + " ms");

        } else {
            byte[] ee1 = Base64.decode(preferences.getString("ee1", ""), Base64.DEFAULT);
            byte[] ei1 = Base64.decode(preferences.getString("ei1", ""), Base64.DEFAULT);
            Log.d(TAG, "proCrypt: deltaD0=" + (System.currentTimeMillis() - start) + " ms");
            byte[] ee2 = Base64.decode(preferences.getString("ee2", ""), Base64.DEFAULT);
            byte[] ei2 = Base64.decode(preferences.getString("ei2", ""), Base64.DEFAULT);
            byte[] ee3 = Base64.decode(preferences.getString("ee3", ""), Base64.DEFAULT);
            byte[] ei3 = Base64.decode(preferences.getString("ei3", ""), Base64.DEFAULT);
            byte[] ee4 = Base64.decode(preferences.getString("ee4", ""), Base64.DEFAULT);
            byte[] ei4 = Base64.decode(preferences.getString("ei4", ""), Base64.DEFAULT);
            Log.d(TAG, "proCrypt: deltaD1=" + (System.currentTimeMillis() - start) + " ms");

            byte[] srBytes = Base64.decode(preferences.getString("sr", ""), Base64.DEFAULT);

            KeySet keySet = Crypt.getSecretKey("hallo", srBytes);
            SecretKey key = keySet.getSecretKey();

            Log.d(TAG, "proCrypt: deltaD2=" + (System.currentTimeMillis() - start) + " ms");

            Log.d(TAG, "e1: " + Crypt.dec(key, ei1, ee1));
            Log.d(TAG, "proCrypt: deltaD3=" + (System.currentTimeMillis() - start) + " ms");
            Log.d(TAG, "e2: " + Crypt.dec(key, ei2, ee2));
            Log.d(TAG, "e3: " + Crypt.dec(key, ei3, ee3));
            Log.d(TAG, "e4: " + Crypt.dec(key, ei4, ee4));
            Log.d(TAG, "proCrypt: deltaD4=" + (System.currentTimeMillis() - start) + " ms");
        }

        loadPreferences();
    }

    @SuppressWarnings("unchecked")
    public void loadPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Map<String, ?> prefs = preferences.getAll();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal = key + " : " + (Boolean) pref;
            }
            if (pref instanceof Float) {
                printVal = key + " : " + (Float) pref;
            }
            if (pref instanceof Integer) {
                printVal = key + " : " + (Integer) pref;
            }
            if (pref instanceof Long) {
                printVal = key + " : " + (Long) pref;
            }
            if (pref instanceof String) {
                printVal = key + " : " + (String) pref;
            }
            if (pref instanceof Set<?>) {
                printVal = key + " : " + (Set<String>) pref;
            }

            Log.d(TAG, "loadPreferences: " + printVal);
        }
    }


    private void scan() {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Uri uri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(marketIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.d(TAG, "onActivityResult: " + contents);
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to store the password.
     */
    private void attemptStorePassword() {

        mEtEnterPassword.setError(null);
        mEtEnterPasswordAgain.setError(null);

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mEtEnterPassword.getText()) && !isPasswordValid(
            mEtEnterPassword.getText().toString())) {
            mEtEnterPassword.setError(getString(R.string.error_password_not_secure));
            mEtEnterPassword.requestFocus();
            return;
        }

        if (mEtEnterPassword.getText().toString()
            .equals(mEtEnterPasswordAgain.getText().toString())) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            mEtEnterPasswordAgain.requestFocus();
            mEtEnterPasswordAgain.setError(getString(R.string.error_password_not_matching));
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

}

