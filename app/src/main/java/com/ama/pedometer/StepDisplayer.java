package com.ama.pedometer;

import java.util.ArrayList;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 */

public class StepDisplayer implements StepListener {

    private int mCount = 0;

    public StepDisplayer() {
        notifyListener();
    }

    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }
    public void onStep() {
        mCount ++;
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    public void passValue() {
    }
    
    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged((int)mCount);
        }
    }
    
    
}
