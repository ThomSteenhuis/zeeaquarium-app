package com.example.zeeaquarium;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Measurement {
    private String name;
    private float value;
    private long timeStamp;

    public Measurement(String name, String value) {
        this.name = name;
        this.timeStamp = System.currentTimeMillis();

        try {
            this.value = Float.parseFloat(value);
        } catch(NumberFormatException e) {
            this.value = 0;
        }
    }

    public Measurement(String name, String date, String value) {
        this.name = name;

        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        try {
            DateTime dateTime = parser.parseDateTime(date);
            this.timeStamp = dateTime.getMillis();
        } catch (IllegalArgumentException e) {
            this.timeStamp = 0l;
        }

        try {
            this.value = Float.parseFloat(value);
        } catch(NumberFormatException e) {
            this.value = 0;
        }
    }

    public String getName() {
        return this.name;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public float getValue() {
        return this.value;
    }
}
