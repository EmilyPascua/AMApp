package com.ama.pedometer.history;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ama.pedometer.MainActivity;
import com.ama.pedometer.db.DataBaseManager;
import com.ama.pedometer.menu.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.work.Worker;

public class HistoryTask extends Worker {

//    final History historyInstance = new History();

    @NonNull
    @Override
    public Result doWork() {
        String date = DataBaseManager.getInstance().returnDate();
        long steps =  DataBaseManager.getInstance().queryCount();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date df = format1.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(df);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Log.e("Day of The Week", dayOfWeek + "");
            DataBaseManager.getInstance().updateHistory(dayOfWeek, steps);
            return Result.SUCCESS;

        } catch (ParseException e) {
            e.printStackTrace();
            return Result.FAILURE;
        }
    }
}
