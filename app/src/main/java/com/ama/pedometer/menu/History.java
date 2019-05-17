package com.ama.pedometer.menu;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ama.pedometer.db.DataBaseManager;
import com.panfei.pedometer.R;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private BarChart mBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Long> history = DataBaseManager.getInstance().returnHistorySteps();
        setContentView(R.layout.activity_history);
        setTitle("History");
        mBarChart = (BarChart) findViewById(R.id.barchart);
        for(int i = 0; i < history.size(); i++) {
            updateGui(i + 1, history.get(i));
        }
        mBarChart.startAnimation();

    }
    //Updates the GUI as it traverses through the database arraylist
    public void updateGui(int dayOfWeek, long steps){
        switch(dayOfWeek) {
            case 1:
                mBarChart.addBar(new BarModel("Monday", steps, Color.parseColor("#CEFA01")));
                break;
            case 2:
                mBarChart.addBar(new BarModel("Tuesday", steps,  Color.parseColor("#CEFA01")));
                break;
            case 3:
                mBarChart.addBar(new BarModel("Wednesday", steps, Color.parseColor("#CEFA01")));
                break;
            case 4:
                mBarChart.addBar(new BarModel("Thursday", steps,  Color.parseColor("#CEFA01")));
                break;
            case 5:
                mBarChart.addBar(new BarModel("Friday", steps, Color.parseColor("#CEFA01")));
                break;
            case 6:
                mBarChart.addBar(new BarModel("Saturday", steps, Color.parseColor("#CEFA01")));
                break;
            case 7:
                mBarChart.addBar(new BarModel("Sunday", steps, Color.parseColor("#CEFA01")));
                break;
            default:
                Log.e("Failure", "Week doesn't exist.");
        }
    }
}
