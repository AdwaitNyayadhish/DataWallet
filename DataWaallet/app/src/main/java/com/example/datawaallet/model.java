package com.example.datawaallet;

import java.text.SimpleDateFormat;

public class model
{
    public Integer roll;
    public String time;


    public model(Integer roll, String time) {
        this.roll = roll;
        this.time = time;
    }

    public Integer getRoll() {
        return roll;
    }

    public void setRoll(Integer roll) {
        this.roll = roll;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
