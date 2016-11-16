package com.example.doyun.mylocationlogger2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by doyun on 2016-11-13.
 */

public class MarkerData implements Parcelable {
    private String markerId;
    private LatLng latLng;

    public LatLng getLatLng() {
        return latLng;
    }

    ArrayList<HashMap<String,Object>> timeLIst = new ArrayList<HashMap<String,Object>>();
    HashMap<String,Object> itme;

    public MarkerData(String markerId, LatLng latLng) {
        this.markerId = markerId;
        this.latLng = latLng;
    }
    public MarkerData(Parcel in){
        in.readList(timeLIst, MarkerData.class.getClassLoader());
        latLng= (LatLng) in.readValue(Marker.class.getClassLoader());
    }

    public void addTime(String date, String time){
        itme = new HashMap<String,Object>();
        itme.put("date", date);
        itme.put("time",time);
        timeLIst.add(itme);
    }

    public void addEvent(MyEvent event){
        for(int i=0;i<timeLIst.size();i++){
            if(timeLIst.get(i).containsValue(event.getTime())){
                timeLIst.get(i).put("name", event.getName());
                timeLIst.get(i).put("event", event);
                break;
            }
        }
    }

    public ArrayList<HashMap<String, Object>> getTimeLIst() {
        return timeLIst;
    }

    public String getMarkerId() {
        return markerId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(timeLIst);
        dest.writeValue(latLng);
    }
    public static final Parcelable.Creator<MarkerData> CREATOR = new Parcelable.Creator<MarkerData>() {
        public MarkerData createFromParcel(Parcel in) {
            return new MarkerData(in);
        }

        public MarkerData[] newArray(int size) {
            return new MarkerData[size];
        }
    };
}
