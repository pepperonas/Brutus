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

package io.celox.brutus.adapter;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.system.DeviceUtils;
import io.celox.brutus.R;
import io.celox.brutus.custom.EditTextDispatched;
import io.celox.brutus.model.Field;
import io.celox.brutus.model.Field.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class FieldAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FieldAdapter";

    // animating OPT validity
    private List<ProgressBar> mProgressBarList = new ArrayList<>();
    private int mProgressCounter = 0;
    private boolean mCountForward = true;
    private Handler mHandler = new Handler();
    private Calendar mCalendar = Calendar.getInstance();
    private boolean mAnimating = false;
    private Timer mTimer = new Timer();
    private TimerTask mAnimationTask = new TimerTask() {
        public void run() {
            mHandler.post(() -> {
                mAnimating = true;

                mCalendar.setTime(new Date(System.currentTimeMillis()));
                int seconds = mCalendar.get(Calendar.SECOND);

                mCountForward = seconds < 30;

                if (mProgressCounter >= 300) {
                    mCountForward = false;
                }
                if (mProgressCounter <= 0) {
                    mCountForward = true;
                }
                if (mCountForward) {
                    mProgressCounter += 10;
                } else {
                    mProgressCounter -= 10;
                }

                Log.d(TAG, "progressCounter=" + mProgressCounter);
                for (ProgressBar pb : mProgressBarList) {
                    if (pb != null) {
                        pb.setProgress(mProgressCounter);
                    }
                }
            });
        }
    };


    private boolean mEditable = false;
    private Activity mActivity;
    private List<Field> mFields = new ArrayList<>();


    public boolean isEditable() { return mEditable; }

    public void setEditable(boolean editable) { this.mEditable = editable; }

    private void changeEditTextBehaviour(EditTextDispatched value, boolean editable) {
        value.setEditable(editable);
        value.setEnabled(editable);
    }

    public FieldAdapter(Activity activity, List<Field> fields) {
        this.mFields = fields;
        this.mActivity = activity;
    }

    public class FieldViewHolderBase extends RecyclerView.ViewHolder {

        public static final int DELEGATE_SIZE = 16;
        private TextView description;
        private EditTextDispatched value;
        private ImageView actionLeft;
        private ImageView actionRight;

        FieldViewHolderBase(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.row_field_description);
            value = (EditTextDispatched) view.findViewById(R.id.row_field_value);
            actionLeft = (ImageView) view.findViewById(R.id.row_field_action_left);
            actionRight = (ImageView) view.findViewById(R.id.row_field_action_right);
            actionLeft.setVisibility(View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.INVISIBLE);
            actionRight.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.close));

            try {
                final View parentR = (View) actionRight.getParent();
                parentR.post(() -> {
                    Rect rectR = new Rect();
                    actionRight.getHitRect(rectR);
                    rectR.top -= DeviceUtils.dp2px(DELEGATE_SIZE);
                    rectR.right += DeviceUtils.dp2px(DELEGATE_SIZE);
                    rectR.bottom += DeviceUtils.dp2px(DELEGATE_SIZE);
                    rectR.left -= DeviceUtils.dp2px(DELEGATE_SIZE);
                    parentR.setTouchDelegate(new TouchDelegate(rectR, actionRight));
                });
                final View parentL = (View) actionLeft.getParent();
                parentL.post(() -> {
                    Rect rectL = new Rect();
                    actionLeft.getHitRect(rectL);
                    rectL.top -= DeviceUtils.dp2px(DELEGATE_SIZE);
                    rectL.right += DeviceUtils.dp2px(DELEGATE_SIZE);
                    rectL.bottom += DeviceUtils.dp2px(DELEGATE_SIZE);
                    rectL.left -= DeviceUtils.dp2px(DELEGATE_SIZE);
                    parentL.setTouchDelegate(new TouchDelegate(rectL, actionLeft));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            changeEditTextBehaviour(value, mEditable);
            value.requestFocus();
        }

        TextView getDescription() { return description; }

        public void setDescription(TextView description) { this.description = description; }

        EditTextDispatched getValue() { return value; }

        void setValue(EditTextDispatched value) { this.value = value; }

        ImageView getActionLeft() { return actionLeft; }

        void setActionLeft(ImageView actionLeft) { this.actionLeft = actionLeft; }

        ImageView getActionRight() { return actionRight; }

        public void setActionRight(ImageView actionRight) { this.actionRight = actionRight; }
    }

    public class FieldViewHolderText extends FieldViewHolderBase {

        FieldViewHolderText(View view) {
            super(view);
        }
    }

    public class FieldViewHolderNumber extends FieldViewHolderBase {

        FieldViewHolderNumber(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    public class FieldViewHolderLogin extends FieldViewHolderBase {

        FieldViewHolderLogin(View view) {
            super(view);
            getActionLeft().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderPassword extends FieldViewHolderBase {

        FieldViewHolderPassword(View view) {
            super(view);
            getValue().setSingleLine(true);
            showPasswordField(getValue());
            getActionLeft().setVisibility(View.VISIBLE);
//            getActionLeft().setImageDrawable(mActivity.getResources().getDrawable(
//                R.drawable.cube_outline));
            getActionLeft().setImageDrawable(mActivity.getResources().getDrawable(
                R.drawable.cube_outline));

            ensureToggleVisibility(getValue(), getActionLeft());

            ensureShowGenerator(getValue(), getActionLeft());
        }
    }

    private void ensureShowGenerator(EditTextDispatched etd, ImageView leftBtn) {
        if (!mEditable) {
            ensureToggleVisibility(etd, leftBtn);
            return;
        }
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.toastShort("TODO: implement gen!");
            }
        });
    }

    private void ensureToggleVisibility(EditTextDispatched etd, ImageView leftBtn) {
        if (mEditable) {
            ensureShowGenerator(etd, leftBtn);
            return;
        }
        leftBtn.setOnClickListener(v -> {
            if (etd.getInputType() ==
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                hidePasswordField(etd);
            } else {
                showPasswordField(etd);
            }
            etd.setSelection(etd.length());
            ((ImageButton) v).setImageDrawable(mActivity.getResources().getDrawable(
                etd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    ? R.drawable.eye_off : R.drawable.eye));
        });
    }

    private void showPasswordField(EditTextDispatched etd) {
        etd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    public class FieldViewHolderOtp extends FieldViewHolderBase {

        FieldViewHolderOtp(View view) {
            super(view);
            getActionLeft().setVisibility(View.VISIBLE);
            getActionRight().setVisibility(View.VISIBLE);

            if (!mAnimating) {
                mTimer.schedule(mAnimationTask, 0, 1000);
            }
        }
    }

    public class FieldViewHolderUrl extends FieldViewHolderBase {

        FieldViewHolderUrl(View view) {
            super(view);
            getActionLeft()
                .setImageDrawable(mActivity.getResources().getDrawable(R.drawable.earth));
            getActionRight().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderMail extends FieldViewHolderBase {

        FieldViewHolderMail(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            getActionRight().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderPhone extends FieldViewHolderBase {

        FieldViewHolderPhone(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_PHONE);
            getActionRight().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderDate extends FieldViewHolderBase {

        FieldViewHolderDate(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        }
    }

    public class FieldViewHolderPin extends FieldViewHolderBase {

        FieldViewHolderPin(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            getActionLeft().setVisibility(View.GONE);
            getActionLeft().setImageDrawable(mActivity.getResources()
                .getDrawable(R.drawable.close));

            ensureToggleVisibility(getValue(), getActionLeft());
        }
    }

    public class FieldViewHolderSecret extends FieldViewHolderBase {

        FieldViewHolderSecret(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_TEXT);
            getActionLeft().setVisibility(View.GONE);
            getActionLeft().setImageDrawable(mActivity.getResources()
                .getDrawable(R.drawable.close));

            ensureToggleVisibility(getValue(), getActionLeft());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (Type.values()[viewType]) {
            case TEXT:
                return new FieldViewHolderText(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case NUMBER:
                return new FieldViewHolderNumber(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case LOGIN:
                return new FieldViewHolderLogin(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case PASSWORD:
                return new FieldViewHolderPassword(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case OTP:
                return new FieldViewHolderOtp(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case URL:
                return new FieldViewHolderUrl(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case MAIL:
                return new FieldViewHolderMail(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case PHONE:
                return new FieldViewHolderPhone(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case DATE:
                return new FieldViewHolderDate(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case PIN:
                return new FieldViewHolderPin(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            case SECRET:
                return new FieldViewHolderSecret(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Field field;
        switch (Type.values()[holder.getItemViewType()]) {
            case TEXT:
                FieldViewHolderText viewHolderText = (FieldViewHolderText) holder;
                field = mFields.get(position);
                viewHolderText.getDescription().setText(field.getDescription());
                break;
            case NUMBER:
                FieldViewHolderNumber viewHolderNumber = (FieldViewHolderNumber) holder;
                field = mFields.get(position);
                viewHolderNumber.getDescription().setText(field.getDescription());
                break;
            case LOGIN:
                FieldViewHolderLogin viewHolderLogin = (FieldViewHolderLogin) holder;
                field = mFields.get(position);
                viewHolderLogin.getDescription().setText(field.getDescription());
                break;
            case PASSWORD:
                FieldViewHolderPassword viewHolderPassword = (FieldViewHolderPassword) holder;
                field = mFields.get(position);
                viewHolderPassword.getDescription().setText(field.getDescription());
                break;
            case OTP:
                FieldViewHolderOtp viewHolderOtp = (FieldViewHolderOtp) holder;
                field = mFields.get(position);
                viewHolderOtp.getDescription().setText(field.getDescription());
                break;
            case URL:
                FieldViewHolderUrl viewHolderUrl = (FieldViewHolderUrl) holder;
                field = mFields.get(position);
                viewHolderUrl.getDescription().setText(field.getDescription());
                break;
            case MAIL:
                FieldViewHolderMail viewHolderMail = (FieldViewHolderMail) holder;
                field = mFields.get(position);
                viewHolderMail.getDescription().setText(field.getDescription());
                break;
            case PHONE:
                FieldViewHolderPhone viewHolderPhone = (FieldViewHolderPhone) holder;
                field = mFields.get(position);
                viewHolderPhone.getDescription().setText(field.getDescription());
                break;
            case DATE:
                FieldViewHolderDate viewHolderDate = (FieldViewHolderDate) holder;
                field = mFields.get(position);
                viewHolderDate.getDescription().setText(field.getDescription());
                break;
            case PIN:
                FieldViewHolderPin viewHolderPin = (FieldViewHolderPin) holder;
                field = mFields.get(position);
                viewHolderPin.getDescription().setText(field.getDescription());
                break;
            case SECRET:
                FieldViewHolderSecret viewHolderSecret = (FieldViewHolderSecret) holder;
                field = mFields.get(position);
                viewHolderSecret.getDescription().setText(field.getDescription());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mFields.get(position).getType().ordinal();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        EditTextDispatched value = (EditTextDispatched) holder.itemView
            .findViewById(R.id.row_field_value);
        ImageButton actionLeft = (ImageButton) holder.itemView
            .findViewById(R.id.row_field_action_left);
        ImageButton actionRight = (ImageButton) holder.itemView
            .findViewById(R.id.row_field_action_right);

        if (holder instanceof FieldViewHolderText
            || holder instanceof FieldViewHolderNumber
            || holder instanceof FieldViewHolderDate) {
            actionLeft.setVisibility(View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.GONE);

        } else if (holder instanceof FieldViewHolderPin
            || holder instanceof FieldViewHolderSecret) {
            actionLeft.setVisibility(mEditable ? View.GONE : View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);

        } else if (holder instanceof FieldViewHolderLogin) {
            actionLeft.setVisibility(mEditable ? View.VISIBLE : View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.GONE);

        } else if (holder instanceof FieldViewHolderPassword) {
            actionLeft.setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.GONE);

            if (mEditable) {
                actionLeft.setImageDrawable(
                    mActivity.getResources().getDrawable(R.drawable.cube_outline));
                showPasswordField(value);
                ensureShowGenerator(value, actionLeft);
            } else {
                hidePasswordField(value);
                actionLeft.setImageDrawable(mActivity.getResources().getDrawable(
                    value.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ?
                        R.drawable.eye_off : R.drawable.eye));
                ensureToggleVisibility(value, actionLeft);
            }
        } else if (holder instanceof FieldViewHolderOtp) {
            ProgressBar progressBar = (ProgressBar) holder.itemView
                .findViewById(R.id.row_field_progressbar);
            actionLeft.setVisibility(mEditable ? View.VISIBLE : View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.INVISIBLE);
            progressBar.setVisibility(mEditable ? View.INVISIBLE : View.VISIBLE);

            mProgressBarList.add(progressBar);

        } else if (holder instanceof FieldViewHolderUrl
            || holder instanceof FieldViewHolderMail
            || holder instanceof FieldViewHolderPhone) {
            actionLeft.setVisibility(mEditable ? View.GONE : View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);
        }

        changeEditTextBehaviour(value, mEditable);
    }

    private void hidePasswordField(EditTextDispatched value) {
        value.setInputType(
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }


    @Override
    public int getItemCount() {
        return mFields.size();
    }


}
