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
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pepperonas.andbasx.base.ToastUtils;
import io.celox.brutus.R;
import io.celox.brutus.Utilities;
import io.celox.brutus.custom.EditTextDispatched;
import io.celox.brutus.dialogs.DialogPasswordGenerator;
import io.celox.brutus.model.Field;
import io.celox.brutus.model.Field.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Field adapter.
 *
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class FieldAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FieldAdapter";

    private static final int OTP_TIMER_PERIOD = 999;

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
                boolean switched = false;

                mCalendar.setTime(new Date(System.currentTimeMillis()));

                if (mProgressCounter >= 300) {
                    mCountForward = false;
                    switched = true;
                }
                if (mProgressCounter <= 0) {
                    mCountForward = true;
                    switched = true;
                }

                if (switched) {
                    new OtpUpdate(vhOtps, otpKeys);
                }

                if (mCountForward) {
                    mProgressCounter += 10;
                } else {
                    mProgressCounter -= 10;
                }

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
    private ArrayList<FieldViewHolderOtp> vhOtps = new ArrayList<>();
    private ArrayList<String> otpKeys = new ArrayList<>();
    private String url = "";


    /**
     * Is editable boolean.
     *
     * @return the boolean
     */
    public boolean isEditable() { return mEditable; }

    /**
     * Sets editable.
     *
     * @param editable the editable
     */
    public void setEditable(boolean editable) {
        this.mEditable = editable;
    }

    /**
     * Change edit text behaviour.
     *
     * @param base the base
     */
    public void changeEditTextBehaviour(View base) {
        try {
            EditTextDispatched etd = (EditTextDispatched) base.findViewById(R.id.row_field_value);
            etd.setEditable(mEditable);
            etd.setEnabled(mEditable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates a new Field adapter.
     *
     * @param activity the activity
     * @param fields the fields
     */
    public FieldAdapter(Activity activity, List<Field> fields) {
        this.mFields = fields;
        this.mActivity = activity;
    }

    /**
     * The type Field view holder base.
     */
    public class FieldViewHolderBase extends RecyclerView.ViewHolder {

        /**
         * The constant DELEGATE_SIZE.
         */
        private TextView description;
        private EditTextDispatched value;
        private ImageView actionLeft;
        private ImageView actionRight;

        /**
         * Instantiates a new Field view holder base.
         *
         * @param view the view
         */
        FieldViewHolderBase(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.row_field_description);
            value = (EditTextDispatched) view.findViewById(R.id.row_field_value);
            actionLeft = (ImageView) view.findViewById(R.id.row_field_action_left);
            actionRight = (ImageView) view.findViewById(R.id.row_field_action_right);
            actionLeft.setVisibility(View.GONE);
            actionRight.setVisibility(mEditable ? View.VISIBLE : View.INVISIBLE);
            actionRight.setImageDrawable(getIcon(R.drawable.close));

            Utilities.expandTouchArea(actionLeft);
            Utilities.expandTouchArea(actionRight);

            changeEditTextBehaviour(view);
            value.requestFocus();
        }

        /**
         * Gets description.
         *
         * @return the description
         */
        TextView getDescription() { return description; }

        /**
         * Sets description.
         *
         * @param description the description
         */
        public void setDescription(TextView description) { this.description = description; }

        /**
         * Gets value.
         *
         * @return the value
         */
        EditTextDispatched getValue() { return value; }

        /**
         * Sets value.
         *
         * @param value the value
         */
        void setValue(EditTextDispatched value) { this.value = value; }

        /**
         * Gets action left.
         *
         * @return the action left
         */
        ImageView getActionLeft() { return actionLeft; }

        /**
         * Sets action left.
         *
         * @param actionLeft the action left
         */
        void setActionLeft(ImageView actionLeft) { this.actionLeft = actionLeft; }

        /**
         * Gets action right.
         *
         * @return the action right
         */
        ImageView getActionRight() { return actionRight; }

        /**
         * Sets action right.
         *
         * @param actionRight the action right
         */
        public void setActionRight(ImageView actionRight) { this.actionRight = actionRight; }
    }

    /**
     * The type Field view holder text.
     */
    public class FieldViewHolderText extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder text.
         *
         * @param view the view
         */
        FieldViewHolderText(View view) {
            super(view);
        }
    }

    /**
     * The type Field view holder number.
     */
    public class FieldViewHolderNumber extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder number.
         *
         * @param view the view
         */
        FieldViewHolderNumber(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    /**
     * The type Field view holder login.
     */
    public class FieldViewHolderLogin extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder login.
         *
         * @param view the view
         */
        FieldViewHolderLogin(View view) {
            super(view);
        }
    }

    /**
     * The type Field view holder password.
     */
    public class FieldViewHolderPassword extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder password.
         *
         * @param view the view
         */
        FieldViewHolderPassword(View view) {
            super(view);

        }
    }

    /**
     * The type Field view holder otp.
     */
    public class FieldViewHolderOtp extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder otp.
         *
         * @param view the view
         */
        FieldViewHolderOtp(View view) {
            super(view);
            if (!mAnimating) {
                mTimer.schedule(mAnimationTask, 0, OTP_TIMER_PERIOD);
            }
        }
    }

    /**
     * The type Field view holder url.
     */
    public class FieldViewHolderUrl extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder url.
         *
         * @param view the view
         */
        FieldViewHolderUrl(View view) {
            super(view);
        }
    }

    /**
     * The type Field view holder mail.
     */
    public class FieldViewHolderMail extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder mail.
         *
         * @param view the view
         */
        FieldViewHolderMail(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
    }

    /**
     * The type Field view holder phone.
     */
    public class FieldViewHolderPhone extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder phone.
         *
         * @param view the view
         */
        FieldViewHolderPhone(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_PHONE);
        }
    }

    /**
     * The type Field view holder date.
     */
    public class FieldViewHolderDate extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder date.
         *
         * @param view the view
         */
        FieldViewHolderDate(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        }
    }

    /**
     * The type Field view holder pin.
     */
    public class FieldViewHolderPin extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder pin.
         *
         * @param view the view
         */
        FieldViewHolderPin(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }
    }

    /**
     * The type Field view holder secret.
     */
    public class FieldViewHolderSecret extends FieldViewHolderBase {

        /**
         * Instantiates a new Field view holder secret.
         *
         * @param view the view
         */
        FieldViewHolderSecret(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (Type.values()[viewType]) {
            case TEXT: {
                return new FieldViewHolderText(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case NUMBER: {
                return new FieldViewHolderNumber(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case LOGIN: {
                return new FieldViewHolderLogin(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case PASSWORD: {
                return new FieldViewHolderPassword(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case OTP: {
                return new FieldViewHolderOtp(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case URL: {
                return new FieldViewHolderUrl(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case MAIL: {
                return new FieldViewHolderMail(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case PHONE: {
                return new FieldViewHolderPhone(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case DATE: {
                return new FieldViewHolderDate(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case PIN: {
                return new FieldViewHolderPin(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
            case SECRET: {
                return new FieldViewHolderSecret(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_field, parent, false));
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Field field;
        switch (Type.values()[holder.getItemViewType()]) {

            case TEXT: {
                FieldViewHolderText vhText = (FieldViewHolderText) holder;
                field = mFields.get(position);
                vhText.getDescription().setText(field.getDescription());

                vhText.getActionLeft().setVisibility(View.GONE);
                vhText.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                vhText.getActionRight().setOnClickListener(v -> {
                    vhText.getValue().setText("");
                    removeField(vhText);
                });
                break;
            }

            case NUMBER: {
                FieldViewHolderNumber vhNumber = (FieldViewHolderNumber) holder;
                field = mFields.get(position);
                vhNumber.getDescription().setText(field.getDescription());

                vhNumber.getActionLeft().setVisibility(View.GONE);
                vhNumber.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                vhNumber.getActionRight().setOnClickListener(v -> {
                    vhNumber.getValue().setText("");
                    removeField(vhNumber);
                });
                break;
            }

            case LOGIN: {
                FieldViewHolderLogin vhLogin = (FieldViewHolderLogin) holder;
                field = mFields.get(position);
                vhLogin.getDescription().setText(field.getDescription());

                vhLogin.getActionLeft().setVisibility(mEditable ? View.VISIBLE : View.GONE);
                vhLogin.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                vhLogin.getActionLeft().setImageDrawable(getIcon(R.drawable.account));
                vhLogin.getActionLeft().setOnClickListener(v -> {
                    ToastUtils.toastLong("contacts...");
                });

                vhLogin.getActionRight().setOnClickListener(v -> {
                    vhLogin.getValue().setText("");
                    removeField(vhLogin);
                });

                break;
            }

            case PASSWORD: {
                FieldViewHolderPassword vhPassword = (FieldViewHolderPassword) holder;
                field = mFields.get(position);
                vhPassword.getDescription().setText(field.getDescription());
                vhPassword.getActionLeft().setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);
                vhPassword.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                if (mEditable) {
                    vhPassword.getActionLeft().setImageDrawable(getIcon(R.drawable.cube_outline));
                    showPasswordField(vhPassword.getValue());

                    vhPassword.getActionLeft().setOnClickListener(v -> {
                        showDialogPasswordGenerator(vhPassword.getValue());
                    });

                    vhPassword.getActionRight().setOnClickListener(v -> {
                        vhPassword.getValue().setText("");
                        removeField(vhPassword);
                    });
                } else {
                    hidePasswordField(vhPassword.getValue());
                    vhPassword.getActionLeft().setImageDrawable(
                        getIcon(vhPassword.getValue().getInputType()
                            == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            ? R.drawable.eye_off : R.drawable.eye));
                    ensureToggleVisibility(vhPassword.getValue(), vhPassword.getActionLeft());
                }
                break;
            }

            case OTP: {
                FieldViewHolderOtp vhOtp = (FieldViewHolderOtp) holder;
                field = mFields.get(position);
                vhOtp.getDescription().setText(field.getDescription());

                ProgressBar progressBar = (ProgressBar) holder.itemView
                    .findViewById(R.id.row_field_progressbar);
                vhOtp.getActionLeft().setVisibility(mEditable ? View.VISIBLE : View.GONE);
                vhOtp.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.INVISIBLE);

                vhOtp.getActionLeft().setImageDrawable(getIcon(R.drawable.qrcode_scan));

                vhOtp.getActionLeft().setOnClickListener(null);

                vhOtp.getActionRight().setOnClickListener(mEditable ? v -> {
                    vhOtp.getValue().setText("");
                    otpKeys.remove(vhOtp.getValue().getText().toString());
                    vhOtps.remove(vhOtp);
                    removeField(vhOtp);
                } : null);

                progressBar.setVisibility(mEditable ? View.INVISIBLE : View.VISIBLE);

                mProgressBarList.add(progressBar);

                // TODO 2017-04-01 00-37: remove otpKey
                String otpKey = "RZD5U2VM7UKCKTNVQ4KZ75X5LR34RM5I";
                vhOtp.getValue().setText(Utilities.generateOneTimePassword(otpKey));
                vhOtps.add(vhOtp);
                otpKeys.add(otpKey);
                break;
            }

            case URL: {
                FieldViewHolderUrl vhUrl = (FieldViewHolderUrl) holder;
                field = mFields.get(position);
                vhUrl.getDescription().setText(field.getDescription());

                vhUrl.getActionLeft().setVisibility(mEditable ? View.GONE : View.GONE);
                vhUrl.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);

                if (mEditable) {
                    vhUrl.getActionRight().setImageDrawable(getIcon(R.drawable.close));
                    vhUrl.getActionRight().setOnClickListener(v -> {
                        vhUrl.getValue().setText("");
                        removeField(vhUrl);
                    });
                } else {
                    vhUrl.getActionRight().setImageDrawable(getIcon(R.drawable.earth));
                    vhUrl.getActionRight().setOnClickListener(v -> openUrl(vhUrl.getValue()));
                }

                vhUrl.getValue().setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        url = "";
                    } else {
                        if (url != null && !url.isEmpty()) {
                            url = vhUrl.getValue().getText().toString()
                                .replace(" ", "").toLowerCase();
                        }
                    }
                });

                break;
            }

            case MAIL: {
                FieldViewHolderMail vhMail = (FieldViewHolderMail) holder;
                field = mFields.get(position);
                vhMail.getDescription().setText(field.getDescription());

                vhMail.getActionLeft().setVisibility(mEditable ? View.GONE : View.GONE);
                vhMail.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);

                if (mEditable) {
                    vhMail.getActionRight().setImageDrawable(getIcon(R.drawable.close));
                    vhMail.getActionRight().setOnClickListener(v -> {
                        vhMail.getValue().setText("");
                        removeField(vhMail);
                    });
                } else {
                    vhMail.getActionRight()
                        .setImageDrawable(getIcon(R.drawable.message_text_outline));
                    vhMail.getActionRight().setOnClickListener(v -> sendMail(vhMail.getValue()));
                }
                break;
            }

            case PHONE: {
                FieldViewHolderPhone vhPhone = (FieldViewHolderPhone) holder;
                field = mFields.get(position);
                vhPhone.getDescription().setText(field.getDescription());

                vhPhone.getActionLeft().setVisibility(mEditable ? View.GONE : View.GONE);
                vhPhone.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.VISIBLE);

                if (mEditable) {
                    vhPhone.getActionRight().setImageDrawable(getIcon(R.drawable.close));
                    vhPhone.getActionRight().setOnClickListener(v -> {
                        vhPhone.getValue().setText("");
                        removeField(vhPhone);
                    });
                } else {
                    vhPhone.getActionRight().setImageDrawable(getIcon(R.drawable.phone));
                    vhPhone.getActionRight().setOnClickListener(v -> phoneCall(vhPhone.getValue()));
                }
                break;
            }

            case DATE: {
                FieldViewHolderDate vhDate = (FieldViewHolderDate) holder;
                field = mFields.get(position);
                vhDate.getDescription().setText(field.getDescription());

                vhDate.getActionLeft().setVisibility(View.GONE);
                vhDate.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                vhDate.getActionRight().setOnClickListener(v -> {
                    vhDate.getValue().setText("");
                    removeField(vhDate);
                });
                break;
            }

            case PIN: {
                FieldViewHolderPin vhPin = (FieldViewHolderPin) holder;
                field = mFields.get(position);
                vhPin.getDescription().setText(field.getDescription());

                vhPin.getActionLeft().setVisibility(mEditable ? View.GONE : View.VISIBLE);
                vhPin.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                vhPin.getActionRight().setOnClickListener(v -> {
                    vhPin.getValue().setText("");
                    removeField(vhPin);
                });

                if (mEditable) {
                    vhPin.getActionLeft().setImageDrawable(getIcon(R.drawable.close));
                    showPasswordFieldPin(vhPin.getValue());
                } else {
                    hidePasswordFieldPin(vhPin.getValue());
                    vhPin.getActionLeft().setImageDrawable(getIcon(vhPin.getValue().getInputType()
                        == (InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED)
                        ? R.drawable.eye_off : R.drawable.eye));

                    ensureToggleVisibilityPin(vhPin.getValue(), vhPin.getActionLeft());
                }

                break;
            }

            case SECRET: {
                FieldViewHolderSecret vhSecret = (FieldViewHolderSecret) holder;
                field = mFields.get(position);
                vhSecret.getDescription().setText(field.getDescription());

                vhSecret.getActionLeft().setVisibility(mEditable ? View.GONE : View.VISIBLE);
                vhSecret.getActionRight().setVisibility(mEditable ? View.VISIBLE : View.GONE);

                if (mEditable) {
                    vhSecret.getActionLeft().setImageDrawable(getIcon(R.drawable.close));
                    showPasswordField(vhSecret.getValue());
                    vhSecret.getActionRight().setOnClickListener(v -> {
                        vhSecret.getValue().setText("");
                        removeField(vhSecret);
                    });
                } else {
                    hidePasswordField(vhSecret.getValue());
                    vhSecret.getActionLeft()
                        .setImageDrawable(getIcon(vhSecret.getValue().getInputType()
                            == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            ? R.drawable.eye_off : R.drawable.eye));

                    ensureToggleVisibility(vhSecret.getValue(), vhSecret.getActionLeft());
                }
            }
            break;
        }
    }

    private void openUrl(EditTextDispatched value) {
        ToastUtils.toastShort("openUrl");
    }

    private void phoneCall(EditTextDispatched value) {
        ToastUtils.toastShort("call");
    }

    private void sendMail(EditTextDispatched value) {
        ToastUtils.toastShort("mail");
    }

    private void ensureToggleVisibility(EditTextDispatched etd, ImageView leftBtn) {
        if (mEditable) {
            return;
        }
        leftBtn.setOnClickListener(v -> {
            if (etd.getInputType()
                == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                hidePasswordField(etd);
            } else {
                showPasswordField(etd);
            }

            etd.setSelection(etd.length());

            ((ImageButton) v).setImageDrawable(getIcon(etd.getInputType()
                == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ? R.drawable.eye_off : R.drawable.eye));
        });
    }

    private void hidePasswordField(EditTextDispatched value) {
        value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void showPasswordField(EditTextDispatched etd) {
        etd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    private void ensureToggleVisibilityPin(EditTextDispatched etd, ImageView leftBtn) {
        if (mEditable) {
            return;
        }

        leftBtn.setOnClickListener(v -> {
            if (etd.getInputType() ==
                (InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED)) {
                hidePasswordFieldPin(etd);
            } else {
                showPasswordFieldPin(etd);
            }

            etd.setSelection(etd.length());
            ((ImageButton) v).setImageDrawable(getIcon(
                etd.getInputType() == (InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED)
                    ? R.drawable.eye_off : R.drawable.eye));
        });
    }

    private void hidePasswordFieldPin(EditTextDispatched value) {
        value.setInputType(InputType.TYPE_CLASS_NUMBER
            | InputType.TYPE_NUMBER_FLAG_DECIMAL
            | InputType.TYPE_NUMBER_FLAG_SIGNED
            | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }

    private void showPasswordFieldPin(EditTextDispatched etd) {
        etd.setInputType(InputType.TYPE_CLASS_NUMBER
            | InputType.TYPE_NUMBER_FLAG_DECIMAL
            | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    private void showDialogPasswordGenerator(EditTextDispatched etd) {
        if (!mEditable) {
            return;
        }
        new DialogPasswordGenerator(mActivity, etd);
    }

    private void removeField(FieldViewHolderBase viewHolderBase) {
        mFields.remove(viewHolderBase.getLayoutPosition());
        notifyDataSetChanged();
    }

    private Drawable getIcon(@DrawableRes int iconId) {
        return mActivity.getResources().getDrawable(iconId);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mFields.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return mFields.size();
    }

    public String getRecentUrl() {
        if (url == null && !url.isEmpty()) {
            Log.w(TAG, "no url!");
            return null;
        }
        try {
            new URL(url);
            Log.i(TAG, "good url: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "bad url: " + url);
        return null;
    }


    private class OtpUpdate {

        OtpUpdate(ArrayList<FieldViewHolderOtp> vhOtps, ArrayList<String> otpKeys) {
            for (int i = 0; i < vhOtps.size(); i++) {
                try {
                    vhOtps.get(i).getValue()
                        .setText(Utilities.generateOneTimePassword(otpKeys.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
