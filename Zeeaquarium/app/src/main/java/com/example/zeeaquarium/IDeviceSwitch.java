package com.example.zeeaquarium;

import io.reactivex.Observable;

public interface IDeviceSwitch {
    String getName();

    void setStatus(DeviceSwitchStatus status);

    Observable<DeviceSwitchStatus> status();
}
