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

package io.celox.brutus.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class EditTextDispatched extends android.support.v7.widget.AppCompatEditText {

    private static final String TAG = "EditTextDispatched";

    private boolean editable = true;

    public EditTextDispatched(Context context) {
        super(context);
    }

    public EditTextDispatched(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextDispatched(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {

        if (editable) {
            // default behaviour
            super.dispatchTouchEvent(motionEvent);
            return true;
        }

        // achieve the click-behaviour of a TextView
        ((RelativeLayout) getParent()).onTouchEvent(motionEvent);
        return true;
    }

}
