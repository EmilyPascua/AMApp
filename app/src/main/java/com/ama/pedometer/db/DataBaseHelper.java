package com.ama.pedometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Credits to: suitian @ Github
 * Helper for the SQLLite database.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context){
        super(context, "testing.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Keeps the number of steps once the application is turned off
        db.execSQL("create table sensorinfo (id integer primary key autoincrement, time long, sensor single, date String)");

        //Keeps the number of steps in the course of a week.
        db.execSQL("create table history (dow integer primary key, steps long)");
        db.execSQL("INSERT INTO history VALUES ('1', null)");
        db.execSQL("INSERT INTO history VALUES ('2', null)");
        db.execSQL("INSERT INTO history VALUES ('3', null)");
        db.execSQL("INSERT INTO history VALUES ('4', null)");
        db.execSQL("INSERT INTO history VALUES ('5', null)");
        db.execSQL("INSERT INTO history VALUES ('6', null)");
        db.execSQL("INSERT INTO history VALUES ('7', null)");

        db.execSQL("create table userInformation (id integer,weight String, height String)");
        db.execSQL("INSERT INTO userInformation VALUES (1,'150 pounds', '4 feet')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
