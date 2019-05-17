package com.ama.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.ama.pedometer.db.DataBaseManager;

/*
* Peak-to-Peak Implementation by panfei1993
* The Peak-to-Peak Algorithm finds the peak of the accelerometer signals.
* */

public class StepDetector implements SensorEventListener
{

    //Listens for Steps
    private StepListener mStepListener;

    //Original accelerometer signals
    float[] oriValues = new float[3];
    final int valueNum = 4;

    //Temp values for the acceleration as the peak is found.
    float[] tempValue = new float[valueNum];

    //Temp count from the temp values.
    int tempCount = 0;

    //Boolean to detect the direct up or down.
    boolean isDirectionUp = false;

    //Responsible for counting if the accelerometer values are going up or not.
    int continueUpCount = 0;

    //Temp value to keep track of the previous values.
    int continueUpFormerCount = 0;

    boolean lastStatus = false;

    //The following variables are responsible for keeping track of the peak and the time of the peak.
    float peakOfWave = 0;
    float valleyOfWave = 0;
    long timeOfThisPeak = 0;
    long timeOfLastPeak = 0;
    long timeOfNow = 0;

    //Old r value and new r value.
    float gravityNew = 0;
    float gravityOld = 0;

    //Threshold values
    final float initialValue = (float) 1.3;
    float ThreadValue = (float) 2.0;

    //Database manager instance responsible for keeping the values even if the application
    //is turned off.
    private DataBaseManager dataBaseManager;

    //Step detection instance
    public StepDetector(Context context) {
        dataBaseManager = DataBaseManager.getInstance(context);
    }

    //Step listener instance
    public void setStepLinstener(StepListener mStepListener){
        this.mStepListener = mStepListener;
    }

    //Whenever there is a new accelerometer value.
    //Device is always listening for values.
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
            return;
        }

        for (int i = 0; i < 3; i++) {
            oriValues[i] = event.values[i];
        }
        //Finds the gravity values by squaring and square rooting the values.
        gravityNew = (float) Math.sqrt(oriValues[0] * oriValues[0]
                + oriValues[1] * oriValues[1] + oriValues[2] * oriValues[2]);

        //Here is where the new steps determined.
        if(Math.abs(oriValues[0]) < 2 && Math.abs(oriValues[2]) < 2) {
            DetectorNewStep(gravityNew);
        }
    }

    //Takes acclerometer values. Finds if the values are going up or down.
    public void DetectorNewStep(float values) {
        if (gravityOld == 0) {
            gravityOld = values;
        } else {
            if (DetectorPeak(values, gravityOld)) {
                timeOfLastPeak = timeOfThisPeak;
                timeOfNow = System.currentTimeMillis();
                if (timeOfNow - timeOfLastPeak >= 250
                        && (peakOfWave - valleyOfWave >= ThreadValue)) {
                    timeOfThisPeak = timeOfNow;
                    //Constantly adds data into the database
                    dataBaseManager.addData(System.currentTimeMillis(), values);
                    if (mStepListener != null){
                        mStepListener.onStep();
                    }
                }
                if (timeOfNow - timeOfLastPeak >= 250
                        && (peakOfWave - valleyOfWave >= initialValue)) {
                    timeOfThisPeak = timeOfNow;
                    ThreadValue = Peak_Valley_Thread(peakOfWave - valleyOfWave);
                }
            }
        }
        gravityOld = values;
    }

    //Detects the peak by finding if the values are increasingly going up or down.
    public boolean DetectorPeak(float newValue, float oldValue) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }

        if (!isDirectionUp && lastStatus
                && (continueUpFormerCount >= 2 || oldValue >= 20)) {
            peakOfWave = oldValue;
            return true;
        } else if (!lastStatus && isDirectionUp) {
            valleyOfWave = oldValue;
            return false;
        } else {
            return false;
        }
    }

    //Some values are considered a valley in which they are computed differently with a threshold.
    public float Peak_Valley_Thread(float value) {
        float tempThread = ThreadValue;
        if (tempCount < valueNum) {
            tempValue[tempCount] = value;
            tempCount++;
        } else {
            tempThread = averageValue(tempValue, valueNum);
            for (int i = 1; i < valueNum; i++) {
                tempValue[i - 1] = tempValue[i];
            }
            tempValue[valueNum - 1] = value;
        }
        return tempThread;

    }

    //Averages the values in a threshold so that not all values are considered.
    public float averageValue(float value[], int n) {
        float ave = 0;
        for (int i = 0; i < n; i++) {
            ave += value[i];
        }
        ave = ave / valueNum;
        if (ave >= 8)
            ave = (float) 4.3;
        else if (ave >= 7 && ave < 8)
            ave = (float) 3.3;
        else if (ave >= 4 && ave < 7)
            ave = (float) 2.3;
        else if (ave >= 3 && ave < 4)
            ave = (float) 2.0;
        else {
            ave = (float) 1.3;
        }
        return ave;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

}