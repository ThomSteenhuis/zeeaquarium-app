package com.example.zeeaquarium;

public enum SwitchStatus {
    ON, OUT, UNKNOWN;

    public static SwitchStatus parseStatus(String status) {
        if (status.equals("True")) {
            return SwitchStatus.ON;
        } else if (status.equals("False")) {
            return SwitchStatus.OUT;
        } else {
            return SwitchStatus.UNKNOWN;
        }
    }
}
