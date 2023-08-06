package com.example.zeeaquarium;

import android.app.Activity;
import android.graphics.Color;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.Section;
import com.github.anastr.speedviewlib.components.Style;

import io.reactivex.subjects.BehaviorSubject;

public class WaterVolumeReservoirMeter implements IMeter {
    private static final String NAME = "watervolume_reservoir";

    private Speedometer meter;
    private Activity activity;
    private Measurement value = new Measurement(NAME, "");

    private BehaviorSubject<Measurement> valueSubject = BehaviorSubject.create();

    public WaterVolumeReservoirMeter(Speedometer meter, Activity activity) {
        this.meter = meter;
        this.activity = activity;

        this.createSections();
        this.setGrayoutColoring();

        valueSubject.subscribe(
                value -> {
                    this.value = value;
                    this.activity.runOnUiThread(() -> this.meter.speedTo(this.value.getValue(), 1000));
                },
                error -> System.out.println("Error: " + error.getMessage())
        );
    }

    public String getName() {
        return NAME;
    }

    public void setValue(Measurement value) {
        if (value.getName().equals(NAME)) {
            this.valueSubject.onNext(value);
        }
    }

    public Measurement getValue() {
        return value;
    }

    public void setGrayoutColoring() {
        meter.setBackgroundCircleColor(Color.LTGRAY);
        meter.getIndicator().setColor(Color.GRAY);
        meter.getSections().get(0).setColor(Color.rgb(100, 100, 100));
        meter.getSections().get(1).setColor(Color.rgb(130, 130, 130));
        meter.getSections().get(2).setColor(Color.rgb(160, 160, 160));
    }

    public void setDefaultColoring() {
        meter.setBackgroundCircleColor(Color.WHITE);
        meter.getIndicator().setColor(Color.rgb(50, 200, 255));
        meter.getSections().get(0).setColor(Color.RED);
        meter.getSections().get(1).setColor(Color.YELLOW);
        meter.getSections().get(2).setColor(Color.GREEN);
    }

    private void createSections() {
        meter.clearSections();
        meter.addSections(
                new Section(
                        0.0f,
                        0.1f,
                        Color.RED,
                        meter.getSpeedometerWidth(),
                        Style.BUTT),
                new Section(
                        0.1f,
                        0.2f,
                        Color.YELLOW,
                        meter.getSpeedometerWidth(),
                        Style.BUTT),
                new Section(
                        0.2f,
                        1f,
                        Color.GREEN,
                        meter.getSpeedometerWidth(),
                        Style.BUTT)
        );
    }
}
