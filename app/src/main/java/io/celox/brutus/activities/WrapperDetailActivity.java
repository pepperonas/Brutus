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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.pepperonas.andbasx.concurrency.LoaderTaskUtils;
import com.pepperonas.andbasx.concurrency.LoaderTaskUtils.Action;
import com.pepperonas.andbasx.concurrency.LoaderTaskUtils.Builder;
import com.pepperonas.andbasx.interfaces.LoaderTaskListener;
import io.celox.brutus.R;
import io.celox.brutus.adapter.FieldAdapter;
import io.celox.brutus.custom.EditTextDispatched;
import io.celox.brutus.model.Field;
import io.celox.brutus.model.Field.Type;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The type Wrapper detail activity.
 */
public class WrapperDetailActivity extends AppCompatActivity {

    private static final String TAG = "WrapperDetailActivity";

    private Button mBtnAddField;
    private TextView mTvModified;

    private FieldAdapter mFieldAdapter;
    private List<Field> fields = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private ImageView mIcon;
    private EditTextDispatched mEtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_detail);

        mEtTitle = (EditTextDispatched) findViewById(R.id.et_title);
        mEtTitle.clearFocus();

        mTvModified = (TextView) findViewById(R.id.tv_modified);

        String modified = makeInfoModified();
        mTvModified.setText(modified);

        mBtnAddField = (Button) findViewById(R.id.btn_add_field);
        mBtnAddField.setVisibility(View.GONE);
        mBtnAddField.setOnClickListener(view -> {
            PopupMenu mp = new PopupMenu(WrapperDetailActivity.this, mBtnAddField);
            mp.inflate(R.menu.popup_field_chooser);
            mp.show();
            mp.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_field_chooser_text: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_text), "T", Type.TEXT));
                        break;
                    }
                    case R.id.popup_field_chooser_number: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_number), "N", Type.NUMBER));
                        break;
                    }
                    case R.id.popup_field_chooser_login: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_login), "L", Type.LOGIN));
                        break;
                    }
                    case R.id.popup_field_chooser_password: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_password), "P", Type.PASSWORD));
                        break;
                    }
                    case R.id.popup_field_chooser_otp: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_otp), "O", Type.OTP));
                        break;
                    }
                    case R.id.popup_field_chooser_url: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_url), "U", Type.URL));
                        break;
                    }
                    case R.id.popup_field_chooser_mail: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_mail), "M", Type.MAIL));
                        break;
                    }
                    case R.id.popup_field_chooser_phone: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_phone), "Ph", Type.PHONE));
                        break;
                    }
                    case R.id.popup_field_chooser_date: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_date), "D", Type.DATE));
                        break;
                    }
                    case R.id.popup_field_chooser_pin: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_pin), "PIN", Type.PIN));
                        break;
                    }
                    case R.id.popup_field_chooser_secret: {
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_secret), "S", Type.SECRET));
                        break;
                    }
                }
                mFieldAdapter.notifyDataSetChanged();
                return false;
            });
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mFieldAdapter = new FieldAdapter(this, fields);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFieldAdapter);

        mFieldAdapter.notifyDataSetChanged();

        mIcon = (ImageView) findViewById(R.id.iv_icon);
        mIcon.setOnClickListener(view -> {
            PopupMenu mp = new PopupMenu(WrapperDetailActivity.this, mIcon);
            mp.inflate(R.menu.popup_icon_chooser);

            for (int i = 0; i < mLayoutManager.getItemCount(); i++) {
                if (mRecyclerView.getAdapter().getItemViewType(i) == Type.URL.ordinal()) {
                    mp.getMenu().findItem(R.id.popup_icon_chooser_web)
                        .setEnabled(mFieldAdapter.getRecentUrl() != null);
                }
            }

            mp.show();
            mp.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.popup_icon_chooser_web: {
                        fetchWebIcon(mIcon, mFieldAdapter.getRecentUrl());
                        break;
                    }
                }
                return false;
            });
        });
    }


    @NonNull
    private String makeInfoModified() {
        Locale locale = this.getResources().getConfiguration().locale;
        SimpleDateFormat sdf;
        if (locale == Locale.GERMAN) {
            sdf = new SimpleDateFormat("EEEE dd/MM.yyyy HH:mm", locale);
        } else {
            sdf = new SimpleDateFormat("EEEE MM/dd/yyyy HH:mm", locale);
        }
        return String.format("%s %s", getString(R.string.modified),
            sdf.format(new Date(System.currentTimeMillis())));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wrapper_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit: {
                mFieldAdapter.setEditable(!mFieldAdapter.isEditable());
                mEtTitle.clearFocus();
                mEtTitle.setEditable(mFieldAdapter.isEditable());

                for (int i = 0; i < mFieldAdapter.getItemCount(); i++) {
                    // update each item itself
                    mFieldAdapter.notifyItemChanged(i, fields.get(i));
                    mFieldAdapter.changeEditTextBehaviour(mLayoutManager.getChildAt(i));
                }
                mBtnAddField.setVisibility(mFieldAdapter.isEditable() ? View.VISIBLE : View.GONE);
                mTvModified.setVisibility(!mFieldAdapter.isEditable() ? View.VISIBLE : View.GONE);

                stayAtBottom();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Ensure {@link RecyclerView} stays on bottom when switching mode.
     */
    void stayAtBottom() {
        if (mLayoutManager.findLastCompletelyVisibleItemPosition()
            == mFieldAdapter.getItemCount() - 1) {
            mRecyclerView.scrollToPosition(mFieldAdapter.getItemCount() - 1);
        }
    }

    public void fetchWebIcon(ImageView icon, String siteUrl) {

        new Builder(this, new LoaderTaskListener() {
            @Override
            public void onLoaderTaskSuccess(Action action, String msg) {
                downloadIconImage(siteUrl, msg);
            }

            @Override
            public void onLoaderTaskFailed(Action action, String msg) { }

            @Override
            public void onLoaderTaskSuccess(Action action, InputStream inputStream) { }

            @Override
            public void onLoaderTaskFailed(Action action, InputStream inputStream) { }
        }, mFieldAdapter.getRecentUrl())
            .showProgressDialog(getString(R.string.dialog_title_loading),
                getString(R.string.dialog_msg_loading))
            .launch();
    }

    private void downloadIconImage(String siteUrl, String htmlSource) {
        String imgUrl = null;
        if (htmlSource.contains("<link rel=\"shortcut icon\" href=\"")) {
            imgUrl = htmlSource
                .split("<link rel=\"shortcut icon\" " + "href=\"")[1]
                .split("\"/>")[0];
        }

        if (imgUrl == null) {
            imgUrl = "http://s2.googleusercontent.com/s2/favicons?domain_url=" + siteUrl;
        }

        new LoaderTaskUtils.Builder(this, new LoaderTaskListener() {
            @Override
            public void onLoaderTaskSuccess(Action action, String msg) { }

            @Override
            public void onLoaderTaskFailed(Action action, String s) { }

            @Override
            public void onLoaderTaskSuccess(Action action, InputStream inputStream) {
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                runOnUiThread(() -> mIcon.setImageBitmap(bmp));
            }

            @Override
            public void onLoaderTaskFailed(Action action, InputStream inputStream) { }

        }, imgUrl).launch();

    }

}
