package com.example.zeeaquarium;

import android.widget.ImageView;

import io.reactivex.subjects.BehaviorSubject;

public class TrafficLight implements ITrafficLight {
    private String name;
    private ImageView imageView;
    private DeviceStatus status;

    private BehaviorSubject<DeviceStatus> statusSubject = BehaviorSubject.create();

    public TrafficLight(String name, ImageView imageView) {
        this.name = name;
        this.imageView = imageView;
        this.status = new DeviceStatus(name, "unknown");

        this.statusSubject.subscribe(
                status -> {
                    this.status = status;
                },
                error -> System.out.println("Error: " + error.getMessage())
        );
    }

    public String getName() {
        return this.name;
    }

    public void setValue(DeviceStatus status) {
        if (status.getName().equals(this.name)) {
            this.statusSubject.onNext(status);
        }
    }

    public DeviceStatus getValue() {
        return this.status;
    }

    public void setGrayoutColoring() {
        this.imageView.setImageResource(R.drawable.grey_light);
    }

    public void setDefaultColoring() {
        if (this.status.getStatus().equals(SwitchStatus.ON)) {
            this.imageView.setImageResource(R.drawable.green_light);
        } else {
            this.imageView.setImageResource(R.drawable.red_light);
        }
    }
}
