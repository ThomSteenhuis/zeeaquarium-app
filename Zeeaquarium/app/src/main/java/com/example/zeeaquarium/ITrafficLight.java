package com.example.zeeaquarium;

public interface ITrafficLight {
    String getName();

    void setValue(DeviceStatus status);

    DeviceStatus getValue();

    void setGrayoutColoring();

    void setDefaultColoring();
}
