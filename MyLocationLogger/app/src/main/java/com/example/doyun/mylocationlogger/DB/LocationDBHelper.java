package com.example.doyun.mylocationlogger.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by doyun on 2016-10-26.
 */

public class LocationDBHelper extends SQLiteOpenHelper {

    public static final String F_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MyLocationLogger/locationdb.db";
    public LocationDBHelper(Context context, String name) {
        super(context, F_path, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contact ( _id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, time TEXT, lon TEXT, lat TEXT, checkCycle TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS contact");
        onCreate(db);
    }
}
