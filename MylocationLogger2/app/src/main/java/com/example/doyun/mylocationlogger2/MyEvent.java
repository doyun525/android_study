package com.example.doyun.mylocationlogger2;

import java.io.Serializable;

/**
 * Created by doyun on 2016-11-13.
 */

public class MyEvent implements Serializable{
    String date;
    String time;
    String name;
    String place;
    String content;

    MyEvent(String date, String time, String name, String place, String content){
        this.date = date;
        this.time = time;
        this.name = name;
        this.place = place;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getPlace() {
        return place;
    }

    public String getName() {
        return name;
    }
}
