package com.example.zeeaquarium;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class SimpleSettingSetter implements ISettingSetter {

    private EditText editText;
    private ProgressBar syncing;
    private String name;
    private Activity activity;

    private BehaviorSubject<String> settingValueSubject = BehaviorSubject.create();
    private String value = "";
    private boolean outOfSync = false;

    public SimpleSettingSetter(EditText editText, ProgressBar syncing, String name, Activity activity) {
        this.editText = editText;
        this.syncing = syncing;
        this.name = name;
        this.activity = activity;

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                settingValueSubject.onNext(this.editText.getText().toString());
                activity.runOnUiThread(() -> this.setOutOfSync());
            }
        });
    }

    public void setSetting(String name, String value) {
        if (name.equals(this.name) && !this.editText.hasFocus()) {
            activity.runOnUiThread(() -> {
                this.value = value;
                this.editText.setText(value);
                this.setSynced();
            });
        }
    }

    public Observable<String> settingValue() {
        return this.settingValueSubject.hide();
    }

    public String getName() {
        return this.name;
    }

    private void setOutOfSync() {
        this.outOfSync = true;
        this.editText.setClickable(false);
        this.syncing.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (outOfSync) {
                activity.runOnUiThread(() -> {
                    this.setSynced();
                    this.editText.setText(this.value);
                });
            }
        }).start();
    }

    private void setSynced() {
        this.outOfSync = false;
        this.editText.setClickable(true);
        this.syncing.setVisibility(View.INVISIBLE);
    }
}
