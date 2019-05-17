package com.ama.pedometer;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ama.pedometer.history.HistoryTask;
import com.panfei.pedometer.R;
import com.ama.pedometer.db.DataBaseManager;
import com.ama.pedometer.menu.Menu;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/* Pedro Angeles, Emily Pascua, Gian Tolentino, Daniel Kale, Abraham Vega
* Step-to-Step Credits to panfei1993 @ Github.com
* Activity Monitoring Application (AMA) is an application made for monitoring your application. The app itself is
* made for the Vuzix-M100, but also has a mode used for testing on the phone (This has to be manipulated directly
* through the code).
* */

public class MainActivity extends Activity{

    public final String TAG = "MainActivity";

    //Step count
    private static int mStepValue;
    private int mStepGoal; //Default value
    private double calories;

    //Other various views used to display other activity monitoring variables.
    private TextView stepsView;
    private TextView viewCalories;
    private PieModel sliceGoal, sliceCurrent;
    private PieChart pg;

    //Step service responsible of running step-counting  in the background.
    private StepService mService;

    //Textviews responsible for the height and weight.
    private TextView viewHeight;
    private TextView viewWeight;
    private int userWeight;
    private int userHeight;

    //Textview for the distance.
    private TextView viewDistance;
    private float distance;

    //Connects the service.
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0,intent , 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
            builder.setAutoCancel(false)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(contentIntent);
            builder.build();
            Notification notification = builder.getNotification();
            mService.startForeground(1234, notification);
            mService.registerCallback(mCallback);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    //Incase there are other messages, however in this case there is only one message.
    private static final int STEPS_MSG = 1;

    //Callback to get the steps in the background.
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
    };

    //Handles the actual step counting.
    private Handler mHandler = new Handler() {

        @Override public void handleMessage(Message msg) {

            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue++;
                    sliceCurrent.setValue(mStepValue);
                    mStepGoal =mStepValue  - (mStepValue % 100) + 100;
                    int difference = mStepValue - mStepGoal;
                    sliceGoal.setValue(difference);
                    pg.update();
                    Log.e("Slice", sliceCurrent.getValue() + " ");
                    if ((mStepGoal - mStepValue) <= 0) {
                        Toast.makeText(getApplicationContext(), "Goal of " + sliceGoal.getValue() + " has been met .", Toast.LENGTH_SHORT).show();
                    }
                    stepsView.setText("" + mStepValue + " / " + mStepGoal);
                    //                    stepsView.setText(sliceGoal.getValue());
                    calories = mStepValue * 0.04;
                    String formatCalories = String.format ("%.4f", calories);
                    distance = (float) ((mStepValue * 5 * 0.43))/63360;
                    String numberFormat = String.format ("%.4f", distance);
                    viewCalories.setText("" + formatCalories + " cal");
                    viewDistance.setText("" + numberFormat + " mi");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

    //Sets up the initial values of the application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");

        //Begin work
        PeriodicWorkRequest pwr = new PeriodicWorkRequest.Builder(
                HistoryTask.class,
                20,
                TimeUnit.MINUTES
        ).build();
        WorkManager.getInstance().enqueue(pwr);

        mStepValue = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        stepsView = (TextView) findViewById(R.id.steps);
        viewCalories = (TextView) findViewById(R.id.cal);
        viewDistance = (TextView) findViewById(R.id.distance);

        //Pie graph
        pg = (PieChart) findViewById(R.id.piechart);

        //Sets up the pie graph.
        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#CEFA01"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", 100, Color.parseColor("#818181"));
        pg.addPieSlice(sliceGoal);

        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.isAutoCenterInSlice();
        pg.startAnimation();

        viewHeight = (TextView) findViewById(R.id.height);
        viewWeight = (TextView) findViewById(R.id.weight);

        stepsView = (TextView)findViewById(R.id.steps);
        setTitle("Activity Monitoring Application");
        startService(new Intent(this, StepService.class));
    }

    //On start, re-run the userWeight and userHeight.
    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();

    }

    //Similar to the onStart, onResumes the userWeight and userHeight.
    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();

        //Get the step value from the database once the application is resumed.
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mStepValue = DataBaseManager.getInstance(MainActivity.this).queryCount();
                mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, mStepValue, 0));
            }
        });
        startService(new Intent(this, StepService.class));
        bindService(new Intent(this, StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);

        viewHeight.setText(DataBaseManager.getInstance(MainActivity.this).getUserHeight() + "");
        viewWeight.setText(DataBaseManager.getInstance(MainActivity.this).getUserWeight() + " lbs");
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        unbindService(mConnection);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

    }
    public void onClickMenu(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        this.startActivity(intent);
    }
}
