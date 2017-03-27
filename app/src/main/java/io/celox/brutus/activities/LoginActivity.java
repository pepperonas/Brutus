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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.celox.brutus.R;
import io.celox.brutus.crypto.CryptoX;

/**
 * A login screen that offers to set the password.
 */
public class LoginActivity extends AppCompatActivity {

    boolean testMakeCryptoConfig = false;

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

        mEtEnterPassword.setText("mm");
        mEtEnterPasswordAgain.setText("mm");

        Button btnSetPassword = (Button) findViewById(R.id.btn_set_password);
        btnSetPassword.setOnClickListener(view -> attemptStorePassword());

        mViewSetPassword = findViewById(R.id.set_password_form);

        if (testMakeCryptoConfig) {
            new CryptoX("hallo");
        } else {
            CryptoX.testDecryption("hallo");
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

