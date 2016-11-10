package com.example.doyun.mylocationlogger;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by doyun on 2016-10-25.
 */

public class myLocation {

    double longitude; //경도
    double latitude;   //위도
    int year, month, day, hour, min;
    public int checkCycle;
    String query;
    String s_date;
    String s_time;

    myLocation(double longitude, double latitude){
        setLocation(longitude, latitude);
    }

    myLocation(String s_date, String s_time, String longitude, String latitude, String checkCycle){
        String2date(s_date, s_time);
        setLocation(longitude, latitude);
        this.checkCycle = Integer.parseInt(checkCycle);
    }
    public int getTime(){
        return hour*60+min;
    }

    private void setLocation(String longitude, String latitude){
        this.longitude = Double.parseDouble(longitude);
        this.latitude = Double.parseDouble(latitude);
    }
    private void setLocation(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public LatLng getLatLng(){
        LatLng latLng = new LatLng(latitude, longitude);
        return latLng;
    }
    private void String2date(String s_date, String s_time){
        this.s_date = s_date;
        this.s_time = s_time;
        String d[] = s_date.split("-");
        String t[] = s_time.split(":");
        year=Integer.parseInt(d[0],10);
        month=Integer.parseInt(d[1],10);
        day=Integer.parseInt(d[2],10);
        hour=Integer.parseInt(t[0],10);
        min = Integer.parseInt(t[1],10);
    }

    public void saveLocation(SQLiteDatabase db, Calendar calendar, int checkCycle){
        query = "INSERT INTO contact (date, time, lon, lat, checkCycle) VALUES ( #date#, #time#, #lon#, #lat#, #cycle#)";
        Date a = calendar.getTime();
        String date = (new SimpleDateFormat("yyyy-MM-dd").format(a));
        String time = (new SimpleDateFormat("HH:mm").format(a));

        query = query.replace("#date#", "'"+date+"'");
        query = query.replace("#time#", "'"+time+"'");
        query = query.replace("#lon#", "'"+longitude+"'");
        query = query.replace("#lat#", "'"+latitude+"'");
        query = query.replace("#cycle#", "'"+checkCycle+"'");
        Log.d("test", date+", "+time);
        Log.d("test", query);
        db.execSQL(query);
    }
}
