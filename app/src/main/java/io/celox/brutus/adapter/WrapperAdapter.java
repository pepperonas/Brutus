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
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import io.celox.brutus.R;
import io.celox.brutus.Utilities;
import io.celox.brutus.activities.WrapperDetailActivity;
import io.celox.brutus.adapter.WrapperAdapter.WrapperViewHolder;
import io.celox.brutus.model.Wrapper;
import java.util.List;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class WrapperAdapter extends RecyclerView.Adapter<WrapperViewHolder> {

    private Activity activity;

    private List<Wrapper> wrappers;

    class WrapperViewHolder extends RecyclerView.ViewHolder {

        private TextView title, preview;
        private ImageView icon;
        private ImageButton star;

        WrapperViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.row_wrapper_title);
            preview = (TextView) view.findViewById(R.id.row_wrapper_preview);
            icon = (ImageView) view.findViewById(R.id.row_wrapper_icon);
            star = (ImageButton) view.findViewById(R.id.row_wrapper_star);
        }
    }

    public WrapperAdapter(Activity activity, List<Wrapper> wrappers) {
        this.activity = activity;
        this.wrappers = wrappers;
    }

    @Override
    public WrapperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WrapperViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_wrapper, parent, false));
    }

    @Override
    public void onBindViewHolder(WrapperViewHolder holder, int position) {
        Wrapper wrapper = wrappers.get(position);
        try {
            ImageButton star = holder.star;
            star.setOnClickListener(view -> {
                wrapper.setStarred(!wrapper.isStarred());
                star.setImageDrawable(activity.getResources().getDrawable(wrapper.isStarred()
                    ? R.drawable.star_off : R.drawable.star));
            });

            Utilities.expandTouchArea(star);

            holder.title.setText(wrapper.getTitle());
            holder.preview.setText(String.valueOf(wrapper.getFields().size()));
            holder.itemView.setOnClickListener(
                v -> {
                    activity.startActivity(new Intent(activity, WrapperDetailActivity.class));
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wrappers.size();
    }

}
