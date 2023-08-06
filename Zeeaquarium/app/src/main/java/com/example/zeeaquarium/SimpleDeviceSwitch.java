package com.example.zeeaquarium;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;


import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;

public class SimpleDeviceSwitch implements IDeviceSwitch {

    private Switch deviceSwitch;
    private ProgressBar syncing;
    private String name;
    private Activity activity;

    private BehaviorSubject<DeviceSwitchStatus> statusChangedSubject = BehaviorSubject.create();
    private boolean outOfSync = false;

    public SimpleDeviceSwitch(Switch deviceSwitch, ProgressBar syncing, String name, Activity activity) {
        this.deviceSwitch = deviceSwitch;
        this.syncing = syncing;
        this.name = name;
        this.activity = activity;

        deviceSwitch.setOnClickListener(v -> {
            String on = this.deviceSwitch.isChecked() ? "True" : "False";
            statusChangedSubject.onNext(new DeviceSwitchStatus(this.name, on));
            activity.runOnUiThread(() -> this.setOutOfSync());
        });
    }

    public String getName() {
        return this.name;
    }

    public void setStatus(DeviceSwitchStatus status) {
        if (status.getName().equals(this.name)) {
            activity.runOnUiThread(() -> {
                this.deviceSwitch.setChecked(status.getStatus());
                this.setSynced();
            });
        }
    }

    public Observable<DeviceSwitchStatus> status() {
        return this.statusChangedSubject.hide();
    }

    private void setOutOfSync() {
        this.outOfSync = true;
        this.deviceSwitch.setClickable(false);
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
                    this.deviceSwitch.setChecked(!this.deviceSwitch.isChecked());
                });
            }
        }).start();
    }

    private void setSynced() {
        this.outOfSync = false;
        this.deviceSwitch.setClickable(true);
        this.syncing.setVisibility(View.INVISIBLE);
    }
}
