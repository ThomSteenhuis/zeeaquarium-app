package com.example.zeeaquarium;

public class DeviceSwitchStatus {
    private String name;
    private boolean status;

    public DeviceSwitchStatus(String name, String status) {
        this.name = name;
        this.status = status.equals("True");
    }

    public DeviceSwitchStatus(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public boolean getStatus() {
        return this.status;
    }
}
