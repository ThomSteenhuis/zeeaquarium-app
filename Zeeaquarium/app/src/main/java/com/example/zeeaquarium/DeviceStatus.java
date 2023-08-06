package com.example.zeeaquarium;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DeviceStatus {
    public String name;
    public SwitchStatus status;
    private long timeStamp;

    public DeviceStatus(String name, String value) {
        this.name = name;
        this.timeStamp = System.currentTimeMillis();

        this.status = value.equals("1")
            ? SwitchStatus.ON
            : value.equals("0")
                ? SwitchStatus.OUT
                : SwitchStatus.UNKNOWN;
    }

    public DeviceStatus(String name, String date, String value) {
        this.name = name;

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        try {
            DateTime dateTime = parser.parseDateTime(date);
            this.timeStamp = dateTime.getMillis();
        } catch (IllegalArgumentException e) {
            this.timeStamp = 0l;
        }

        this.status = value.equals("1")
            ? SwitchStatus.ON
            : value.equals("0")
                ? SwitchStatus.OUT
                : SwitchStatus.UNKNOWN;
    }

    public String getName() {
        return this.name;
    }

    public SwitchStatus getStatus() {
        return this.status;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }
}
