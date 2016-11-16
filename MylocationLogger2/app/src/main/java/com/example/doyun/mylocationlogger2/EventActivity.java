package com.example.doyun.mylocationlogger2;

import android.*;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.doyun.mylocationlogger2.DB.EventDBHelper;
import com.example.doyun.mylocationlogger2.DB.LocationDBHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventActivity extends Activity {

    Dialog dialog;

    Calendar calendar;

    int mode;

    public static final int EVENT_POPUP_SAVE = 0;
    public static final int EVENT_POPUP_VIEW = 1;

    private int mYear, mMonth, mDay;

    String date;
    String time;
    public static final String F_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"/MyLocationLogger/";

    EditText title;
    EditText place;
    EditText content;

    Button ok;
    Button cancle;
    Button edit_date;
    Button edit_time;

    HashMap<String, Object> h;

    LatLng latLng;

    LocationDBHelper locationDBHelper;
    EventDBHelper eventDBHelper;
    SQLiteDatabase Event_db;
    SQLiteDatabase Location_db;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    EventActivity(){
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        date = (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        time = (new SimpleDateFormat("HH:mm").format(calendar.getTime()));
    }

    public void saveEvent(SQLiteDatabase event_db, SQLiteDatabase location_db, String date, String time, String event, String place, String content, LatLng latLng){
        String query = "INSERT INTO EventTb (date, time, lon, lat, event, place, content) VALUES ( #date#, #time#, #lon#, #lat#, #event#, #place#, #content#)";

        query = query.replace("#date#", "'"+date+"'");
        query = query.replace("#time#", "'"+time+"'");
        query = query.replace("#lon#", "'"+latLng.longitude+"'");
        query = query.replace("#lat#", "'"+latLng.latitude+"'");
        query = query.replace("#event#", "'"+event+"'");
        query = query.replace("#place#", "'"+place+"'");
        query = query.replace("#content#", "'"+content+"'");
        Log.d("test", date+", "+time);
        Log.d("test", query);
        event_db.execSQL(query);

        String sb = "SELECT * from contact WHERE date=? and time=?";
        Cursor cursor = location_db.rawQuery(sb, new String[]{date,time});
        Log.d("test", ""+cursor.getCount());
        if(cursor.getCount()==0){
            Log.d("test", "저장");
            query = "INSERT INTO contact (date, time, lon, lat, checkCycle) VALUES ( #date#, #time#, #lon#, #lat#, #cycle#)";

            query = query.replace("#date#", "'"+date+"'");
            query = query.replace("#time#", "'"+time+"'");
            query = query.replace("#lon#", "'"+latLng.longitude+"'");
            query = query.replace("#lat#", "'"+latLng.latitude+"'");
            query = query.replace("#cycle#", "'"+60*1000+"'");
            Log.d("test", date+", "+time);
            Log.d("test", query);
            location_db.execSQL(query);
        }


    }
    private  TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(mYear, mMonth, mDay, hourOfDay, minute);

            time = (new SimpleDateFormat("HH:mm").format(calendar.getTime()));
            edit_time.setText(time);
        }
    };

    private DatePickerDialog.OnDateSetListener mEventDataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yaerSelect, int monthOfyear, int dayOfmonth) {
            calendar.set(yaerSelect, monthOfyear, dayOfmonth);

            date =(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
            edit_date.setText(date);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            setContentView(R.layout.activity_event);
        }catch (Exception ex){
            Log.d("ex", ex.getMessage());
        }

        dialog = new Dialog(this);

            locationDBHelper = new LocationDBHelper(this);
            Location_db = locationDBHelper.getWritableDatabase();
            eventDBHelper = new EventDBHelper(this);
            Event_db = eventDBHelper.getWritableDatabase();


        Intent intent = getIntent();

        h = (HashMap<String, Object>)intent.getSerializableExtra("hashMap");

        mode = intent.getIntExtra("mode", 0);

        Log.d("test", "mode "+mode);

        title = (EditText)findViewById(R.id.event_title);
        place = (EditText)findViewById(R.id.event_place);
        content = (EditText)findViewById(R.id.event_content);

        ok = (Button)findViewById(R.id.Ok);
        cancle = (Button)findViewById(R.id.Cancle);
        edit_date = (Button)findViewById(R.id.event_date);
        edit_time = (Button)findViewById(R.id.event_time);

        edit_date.setText(date);
        edit_time.setText(time);

        if(mode == EVENT_POPUP_SAVE) {
            latLng = (LatLng) intent.getParcelableExtra("latlng");
            if(h!=null) {
                if (h.get("date") != null) {
                    date=h.get("date").toString();
                    time=h.get("time").toString();
                    String[] dateA = h.get("date").toString().split("-");
                    String[] timeA = h.get("time").toString().split(":");
                    edit_date.setText(h.get("date").toString());
                    edit_time.setText(h.get("time").toString());
                    edit_date.setEnabled(false);
                    edit_time.setEnabled(false);
                    calendar.set(Integer.parseInt(dateA[0]), Integer.parseInt(dateA[1]), Integer.parseInt(dateA[2]), Integer.parseInt(timeA[0]), Integer.parseInt(timeA[1]));
                }
            }
            datePickerDialog = new DatePickerDialog(this, mEventDataSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            timePickerDialog = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);


        }

        else if(mode == EVENT_POPUP_VIEW){

            ArrayList<HashMap<String,Object>> timeLIst;
            MyEvent myEvent = (MyEvent) h.get("event");

            if(myEvent==null) finish();

            title.setText(myEvent.getName());
            place.setText(myEvent.getPlace());
            content.setText(myEvent.getContent());
            edit_date.setText(myEvent.getDate());
            edit_time.setText(myEvent.getTime());

            title.setFocusable(false);
            title.setClickable(false);
            place.setFocusable(false);
            place.setClickable(false);
            content.setFocusable(false);
            content.setClickable(false);

            edit_date.setEnabled(false);
            edit_time.setEnabled(false);

        }

        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = title.getText().toString();
                String s_place = place.getText().toString();
                String s_content = content.getText().toString();
                Log.d("test", "mode "+mode);
                if(mode==EVENT_POPUP_SAVE) {
                    Log.d("test", "mode " + mode);
                    saveEvent(Event_db, Location_db, date, time, name, s_place, s_content, latLng);
                    Intent in = new Intent();
                    setResult(1,in);
                    finish();;
                }
                Intent in = new Intent();
                setResult(2,in);
                finish();;
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                setResult(2,in);
                finish();
            }
        });


    }
}
