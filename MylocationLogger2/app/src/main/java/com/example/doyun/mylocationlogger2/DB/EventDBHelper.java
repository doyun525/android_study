package com.example.doyun.mylocationlogger2.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by doyun on 2016-10-26.
 */

public class EventDBHelper extends SQLiteOpenHelper {

    public static final String F_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MyLocationLogger/Eventdb.db";
    public EventDBHelper(Context context) {
        super(context, F_path, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table EventTb ( _id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, time TEXT, lon TEXT, lat TEXT, event TEXT, place TEXT, content TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
