package com.ama.pedometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Credits to: suitian.
 * Sets up the SQLite database.
 */
public class DataBaseManager {

    private static final String TAG = "DataBaseManager";
    private volatile static DataBaseManager instance;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private int count;
    private String date;


    protected DataBaseManager(Context context){
        dataBaseHelper = new DataBaseHelper(context);

        db = dataBaseHelper.getWritableDatabase();
    }

    public static DataBaseManager getInstance(){
        return instance;
    }

    public static DataBaseManager getInstance(Context context){
        if (instance == null){
            synchronized (DataBaseManager.class){
                if (instance == null){
                    instance = new DataBaseManager(context);
                }
            }
        }

        return instance;
    }

    //Keeps track of the sensor and the time.
    public synchronized void addData(long time, float sensor){
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        contentValues.put("sensor", sensor);
        //takes in the date the sensor value was inputted
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        contentValues.put("date", date);

        db.beginTransaction();
        try {
            long row = db.insert("sensorinfo",null,contentValues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void clearTable() throws Exception{
        synchronized (this){
            String sql = "DELETE FROM sensorinfo;";
            db.execSQL(sql);
            sql = "update sqlite_sequence set seq=0 where name='sensorinfo'";
            db.execSQL(sql);
        }
    }

    //Returns the number of step count from the pedometer
    public synchronized int queryCount(){
        String sql = "select * from sensorinfo";
        Cursor c = db.rawQuery(sql, null);
        count = c.getCount();
        Log.e(TAG, "queryPedometerCount : " + count);
        c.close();
        return count;
    }

    //Returns the date for the current step count
    public synchronized String returnDate(){
        String sql = "select MAX(date) from sensorinfo";
        //id integer primary key autoincrement, time long, sensor single, date String
        Cursor c = db.rawQuery(sql, null);
        Log.v("TEst", DatabaseUtils.dumpCursorToString(c));
        c.moveToLast();
        date = c.getString(0);
        Log.e(TAG, "date of item : " + date);
        c.close();
        return date;
    }

    public synchronized ArrayList<Long> returnHistorySteps(){
        String sql = "select * from history";
        Cursor c = db.rawQuery(sql, null);
        ArrayList<Long> steps = new ArrayList<>();
        while(c.moveToNext()){
            steps.add(c.getLong(1));
        }
        return steps;
    }
    //Returns the date for the current step count
    public synchronized void updateHistory(int dow,long steps){
        ContentValues cv = new ContentValues();
        cv.put("steps", steps);
        Log.e("TEst", "Success");
        Log.e("test", ""+db.update("history", cv, "dow="+dow,null));
    }

    //Returns the date for the current step count
    public synchronized void updateUserInfo(String weightRange, String height){
        ContentValues cv = new ContentValues();
        cv.put("weight", weightRange);
        cv.put("height", height);
        Log.e("TEst", "Success update");
        Log.e("test", ""+db.update("userInformation", cv, "id="+1,null));
    }

    public synchronized String getUserHeight(){
        String x = "";
        String sql = "select height from userInformation where id=1";
        Cursor c = db.rawQuery(sql, null);
        c.moveToLast();
        x = c.getString(0);
        c.close();
        return x;
    }

    public synchronized String getUserWeight(){
        String x = "";
        String sql = "select weight from userInformation where id=1";
        Cursor c = db.rawQuery(sql, null);
        c.moveToLast();
        x = c.getString(0);
        c.close();
        return x;
    }

    public synchronized int queryPedometerCount(long start, long end){
        String sql = "select * from sensorinfo where time between " + start + " and " + end;
        Cursor c = db.rawQuery(sql, null);
        count = c.getCount();
        Log.e(TAG, "queryPedometerCount : " + count);
        c.close();
        return count;
    }

    public void closeDB() {
        db.close();
    }
}
