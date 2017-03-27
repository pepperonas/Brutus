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
import android.os.AsyncTask;
import android.os.Bundle;
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
import io.celox.brutus.R;
import io.celox.brutus.adapter.FieldAdapter;
import io.celox.brutus.model.Field;
import io.celox.brutus.model.Field.Type;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_detail);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.sample_title));

        mTvModified = (TextView) findViewById(R.id.tv_modified);
        String modified = getString(R.string.modified) + ": " + new Date().getTime();

        ImageView ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivIcon.setOnClickListener(view -> {
            PopupMenu mp = new PopupMenu(WrapperDetailActivity.this, ivIcon);
            mp.inflate(R.menu.popup_icon_chooser);
            mp.show();
            mp.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.popup_icon_chooser_web:
                        fetchWebIcon(ivIcon, "https://celox.io");
                        break;
                }
                return false;
            });
        });

        mBtnAddField = (Button) findViewById(R.id.btn_add_field);
        mBtnAddField.setVisibility(View.GONE);
        mBtnAddField.setOnClickListener(view -> {
            PopupMenu mp = new PopupMenu(WrapperDetailActivity.this,
                findViewById(R.id.btn_add_field));
            mp.inflate(R.menu.popup_field_chooser);
            mp.show();
            mp.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_field_chooser_text:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_text), "T", Type.TEXT));
                        break;
                    case R.id.popup_field_chooser_number:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_number), "N", Type.NUMBER));
                        break;
                    case R.id.popup_field_chooser_login:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_login), "L", Type.LOGIN));
                        break;
                    case R.id.popup_field_chooser_password:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_password), "P", Type.PASSWORD));
                        break;
                    case R.id.popup_field_chooser_otp:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_otp), "O", Type.OTP));
                        break;
                    case R.id.popup_field_chooser_url:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_url), "U", Type.URL));
                        break;
                    case R.id.popup_field_chooser_mail:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_mail), "M", Type.MAIL));
                        break;
                    case R.id.popup_field_chooser_phone:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_phone), "Ph", Type.PHONE));
                        break;
                    case R.id.popup_field_chooser_date:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_date), "D", Type.DATE));
                        break;
                    case R.id.popup_field_chooser_pin:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_pin), "PIN", Type.PIN));
                        break;
                    case R.id.popup_field_chooser_secret:
                        fields.add(new Field(this, System.currentTimeMillis(),
                            getString(R.string.field_secret), "S", Type.SECRET));
                        break;
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
            case R.id.action_edit:
                mFieldAdapter.setEditable(!mFieldAdapter.isEditable());
                mFieldAdapter.notifyDataSetChanged();
                mBtnAddField.setVisibility(mFieldAdapter.isEditable() ? View.VISIBLE : View.GONE);
                mTvModified.setVisibility(!mFieldAdapter.isEditable() ? View.VISIBLE : View.GONE);

                stayAtBottom();
                break;
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
        AsyncTask.execute(() -> {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(siteUrl);
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                setWebIcon(icon, siteUrl, sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setWebIcon(ImageView icon, String siteUrl, String htmlSource) {
        AsyncTask.execute(() -> {
            URL url;
            String imgUrl = null;
            if (htmlSource.contains("<link rel=\"shortcut icon\" href=\"")) {
                imgUrl = htmlSource
                    .split("<link rel=\"shortcut icon\" " + "href=\"")[1]
                    .split("\"/>")[0];
            }
            try {
                if (imgUrl == null) {
                    url = new URL(
                        "http://s2.googleusercontent" + ".com/s2/favicons?domain_url=" + siteUrl);
                } else {
                    url = new URL(imgUrl);
                }
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                runOnUiThread(() -> icon.setImageBitmap(bmp));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        });
    }

}
