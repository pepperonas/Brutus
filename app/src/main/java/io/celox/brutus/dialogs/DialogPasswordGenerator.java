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

package io.celox.brutus.dialogs;

import android.content.Context;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.MaterialDialog.ButtonCallback;
import io.celox.brutus.R;
import io.celox.brutus.custom.EditTextDispatched;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class DialogPasswordGenerator {

    public DialogPasswordGenerator(Context ctx, EditTextDispatched editTextDispatched) {
        MaterialDialog dialog = new MaterialDialog.Builder(ctx)
            .title(R.string.dialog_title_password_generator)
            .customView(R.layout.dialog_password_generator)
            .positiveText(R.string.ok)
            .neutralText(R.string.gen)
            .negativeText(R.string.cancel)
            .buttonCallback(new ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    super.onPositive(dialog);
                }

                @Override
                public void onNeutral(MaterialDialog dialog) {
                    super.onNeutral(dialog);
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);
                }
            }).build();

        dialog.show();
    }
}
