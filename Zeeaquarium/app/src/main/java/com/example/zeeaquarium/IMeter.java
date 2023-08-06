package com.example.zeeaquarium;

public interface IMeter {
    String getName();

    void setValue(Measurement value);

    Measurement getValue();

    void setGrayoutColoring();

    void setDefaultColoring();
}
