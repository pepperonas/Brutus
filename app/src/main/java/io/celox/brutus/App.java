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

import android.app.Application;
import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.AndBasx;


/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        AesPrefs.init(this, "aesp", "struct-brah-plemp-slem-clez-strot-smund-skib",
            AesPrefs.LogMode.ALL);

        AndBasx.init(this);
    }

}
