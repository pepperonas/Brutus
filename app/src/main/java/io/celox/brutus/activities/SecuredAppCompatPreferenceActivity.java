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


import android.content.Intent;
import com.pepperonas.aesprefs.AesPrefs;

public class SecuredAppCompatPreferenceActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if ((AesPrefs.getLong("lua", 0) + 500) < System.currentTimeMillis()) {
            AesPrefs.putLong("lua", System.currentTimeMillis());
            Intent intent = new Intent(this, LoginActivity.class);
            String extra = "";
            if (this instanceof SettingsActivity) {
                extra = "settings";
                intent.putExtra("panel", ((SettingsActivity) this).getPanel());
            }
            intent.putExtra("loa", extra);
            startActivity(intent);
            finish();
        }
    }
}
