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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.MaterialDialog.Builder;
import com.pepperonas.materialdialog.MaterialDialog.ButtonCallback;
import com.pepperonas.materialdialog.MaterialDialog.ShowListener;
import io.celox.brutus.R;
import io.celox.brutus.custom.EditTextDispatched;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class DialogPasswordGenerator {

    private static final String TAG = "DialogPasswordGenerator";

    private static final int AMOUNT_OF_DICTIONARIES = 96;
    private static final int WORDS_IN_DICT = 450;

    private Random mRnd;

    public DialogPasswordGenerator(Context ctx, EditTextDispatched etd) {

        MaterialDialog dialog = new Builder(ctx)
            .title(R.string.dialog_title_password_generator)
            .customView(R.layout.dialog_password_generator)
            .positiveText(R.string.ok)
            .neutralText(R.string.gen)
            .negativeText(R.string.cancel)
            .showListener(new ShowListener() {
                @Override
                public void onShow(AlertDialog dialog) {
                    super.onShow(dialog);
                    TextView textView = (TextView) dialog.findViewById(R.id.tv);
                    textView.setTypeface(Typeface.MONOSPACE);
                    SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
                    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress,
                            boolean fromUser) {
                            progress = progress < 6 ? 6 : progress;
                            String pw = generatePassword(ctx, progress);
                            textView.setText(pw);
                            if (progress <= 18) {
                                textView.setTextSize(20f);
                            } else if (progress <= 24) {
                                textView.setTextSize(16f);
                            } else if (progress <= 36) {
                                textView.setTextSize(13f);
                            } else {
                                textView.setTextSize(11f);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) { }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) { }
                    });

                    seekBar.setProgress(15);

                    dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
                        String pw = generatePassword(ctx, seekBar.getProgress());
                        textView.setText(pw);
                    });
                }
            })
            .buttonCallback(new ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    super.onPositive(dialog);
                    etd.setText(((TextView) (dialog.findViewById(R.id.tv))).getText());
                }

            }).build();

        dialog.show();
    }


    private int getRndInt(int amount, int offset) {
        return new Random().nextInt(amount) + offset;
    }

    private String generatePassword(Context ctx, int length) {
        int line = getRndInt(WORDS_IN_DICT, 1);
        BufferedReader r = null;
        String pw = "";
        try {
            while (pw.length() < length) {
                try {
                    r = new BufferedReader(new InputStreamReader(ctx.getAssets().open("dict_" +
                        String.format("%02d", getRndInt(AMOUNT_OF_DICTIONARIES, 0)) + ".txt")));
                    List<String> words = new ArrayList<>();
                    int i;
                    for (i = 0; i < WORDS_IN_DICT; i++) {
                        words.add(r.readLine());
                    }
                    String part = getWord(words);

                    pw = addPart(length, pw, words, part);
                    if (pw.length() > length) {
                        pw = pw.substring(0, length);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pw;
    }

    private String addPart(int length, String pw, List<String> words, String part) {
        while (part.length() >= length || part.length() >= 7) {
            part = getWord(words);
        }
        pw += part + generateSpecialChar();
        return pw;
    }

    private String generateSpecialChar() {
        String s = "6;<2=>?3{9}[8]7-_*#4,%$§5/():!.'+01";
        return String.valueOf(s.charAt(getRndInt(s.length(), 0)));
    }

    private String getWord(List<String> words) {
        return words.get(getRndInt(WORDS_IN_DICT, 0));
    }
}