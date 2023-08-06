package com.example.zeeaquarium;

import io.reactivex.Observable;

public interface ISettingSetter {
    void setSetting(String name, String value);

    Observable<String> settingValue();

    String getName();
}
