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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import io.celox.brutus.R;
import io.celox.brutus.custom.EditTextDispatched;
import io.celox.brutus.model.Field;
import io.celox.brutus.model.Field.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class FieldAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FieldAdapter";

    private boolean editable = false;
    private Activity activity;
    private List<Field> fields = new ArrayList<>();

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        Log.d(TAG, "setEditable: " + editable);
        this.editable = editable;
    }

    public FieldAdapter(Activity activity, List<Field> fields) {
        this.fields = fields;
        this.activity = activity;
    }


    private void changeEditTextBehaviour(EditTextDispatched value, boolean editable) {
        value.setEditable(editable);
        value.setEnabled(editable);
    }

    public class FieldViewHolderBase extends RecyclerView.ViewHolder {

        private TextView description;
        private EditTextDispatched value;
        private ImageView actionLeft;
        private ImageView actionRight;

        FieldViewHolderBase(View view) {
            super(view);
            description = view.findViewById(R.id.row_field_description);
            value = view.findViewById(R.id.row_field_value);
            actionLeft = view.findViewById(R.id.row_field_action_left);
            actionRight = view.findViewById(R.id.row_field_action_right);
            actionLeft.setVisibility(View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.INVISIBLE);
            actionRight.setImageDrawable(activity.getResources().getDrawable(R.drawable.close));

            changeEditTextBehaviour(value, editable);
            value.requestFocus();
        }

        public TextView getDescription() {
            return description;
        }

        public void setDescription(TextView description) {
            this.description = description;
        }

        public EditTextDispatched getValue() {
            return value;
        }

        public void setValue(EditTextDispatched value) {
            this.value = value;
        }

        public ImageView getActionLeft() {
            return actionLeft;
        }

        public void setActionLeft(ImageView actionLeft) {
            this.actionLeft = actionLeft;
        }

        public ImageView getActionRight() {
            return actionRight;
        }

        public void setActionRight(ImageView actionRight) {
            this.actionRight = actionRight;
        }
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
            getValue().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            getActionLeft().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderOtp extends FieldViewHolderBase {

        FieldViewHolderOtp(View view) {
            super(view);
            getActionLeft().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderUrl extends FieldViewHolderBase {

        FieldViewHolderUrl(View view) {
            super(view);
            getActionLeft().setImageDrawable(activity.getResources().getDrawable(R.drawable.earth));
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
            getActionRight().setVisibility(View.VISIBLE);
        }
    }

    public class FieldViewHolderSecret extends FieldViewHolderBase {

        FieldViewHolderSecret(View view) {
            super(view);
            getValue().setInputType(InputType.TYPE_CLASS_TEXT);
            getActionRight().setVisibility(View.VISIBLE);
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
                field = fields.get(position);
                viewHolderText.getDescription().setText(field.getDescription());
                break;
            case NUMBER:
                FieldViewHolderNumber viewHolderNumber = (FieldViewHolderNumber) holder;
                field = fields.get(position);
                viewHolderNumber.getDescription().setText(field.getDescription());
                break;
            case LOGIN:
                FieldViewHolderLogin viewHolderLogin = (FieldViewHolderLogin) holder;
                field = fields.get(position);
                viewHolderLogin.getDescription().setText(field.getDescription());
                break;
            case PASSWORD:
                FieldViewHolderPassword viewHolderPassword = (FieldViewHolderPassword) holder;
                field = fields.get(position);
                viewHolderPassword.getDescription().setText(field.getDescription());
                break;
            case OTP:
                FieldViewHolderOtp viewHolderOtp = (FieldViewHolderOtp) holder;
                field = fields.get(position);
                viewHolderOtp.getDescription().setText(field.getDescription());
                break;
            case URL:
                FieldViewHolderUrl viewHolderUrl = (FieldViewHolderUrl) holder;
                field = fields.get(position);
                viewHolderUrl.getDescription().setText(field.getDescription());
                break;
            case MAIL:
                FieldViewHolderMail viewHolderMail = (FieldViewHolderMail) holder;
                field = fields.get(position);
                viewHolderMail.getDescription().setText(field.getDescription());
                break;
            case PHONE:
                FieldViewHolderPhone viewHolderPhone = (FieldViewHolderPhone) holder;
                field = fields.get(position);
                viewHolderPhone.getDescription().setText(field.getDescription());
                break;
            case DATE:
                FieldViewHolderDate viewHolderDate = (FieldViewHolderDate) holder;
                field = fields.get(position);
                viewHolderDate.getDescription().setText(field.getDescription());
                break;
            case PIN:
                FieldViewHolderPin viewHolderPin = (FieldViewHolderPin) holder;
                field = fields.get(position);
                viewHolderPin.getDescription().setText(field.getDescription());
                break;
            case SECRET:
                FieldViewHolderSecret viewHolderSecret = (FieldViewHolderSecret) holder;
                field = fields.get(position);
                viewHolderSecret.getDescription().setText(field.getDescription());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return fields.get(position).getType().ordinal();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        EditTextDispatched value = holder.itemView.findViewById(R.id.row_field_value);
        ImageButton actionLeft = holder.itemView.findViewById(R.id.row_field_action_left);
        ImageButton actionRight = holder.itemView.findViewById(R.id.row_field_action_right);

        if (holder instanceof FieldViewHolderText
            || holder instanceof FieldViewHolderNumber
            || holder instanceof FieldViewHolderDate) {
            actionLeft.setVisibility(View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.GONE);

        } else if (holder instanceof FieldViewHolderPin
            || holder instanceof FieldViewHolderSecret) {
            actionLeft.setVisibility(editable ? View.GONE : View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.VISIBLE);

        } else if (holder instanceof FieldViewHolderLogin) {
            actionLeft.setVisibility(editable ? View.VISIBLE : View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.GONE);

        } else if (holder instanceof FieldViewHolderPassword) {
            actionLeft.setVisibility(editable ? View.VISIBLE : View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.VISIBLE);

        } else if (holder instanceof FieldViewHolderOtp) {
            actionLeft.setVisibility(editable ? View.VISIBLE : View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.VISIBLE);

        } else if (holder instanceof FieldViewHolderUrl
            || holder instanceof FieldViewHolderMail
            || holder instanceof FieldViewHolderPhone) {
            actionLeft.setVisibility(editable ? View.GONE : View.GONE);
            actionRight.setVisibility(editable ? View.VISIBLE : View.VISIBLE);
        }

        changeEditTextBehaviour(value, editable);
    }


    @Override
    public int getItemCount() {
        return fields.size();
    }


}
