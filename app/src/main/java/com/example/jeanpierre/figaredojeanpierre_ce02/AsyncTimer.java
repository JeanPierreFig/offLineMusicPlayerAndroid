/*
  Created by jeanpierre on 4/1/18.
  // jean pierre
  // JAV2 - 1804
  //
 */
package com.example.jeanpierre.figaredojeanpierre_ce02;

import android.os.AsyncTask;
import android.util.Log;


class AsyncTimer extends AsyncTask<Void,Void,Void>{

    interface TimerInterface {
        void onProgress();
    }
    private final TimerInterface mlistener;

    AsyncTimer(TimerInterface _interface) {
        mlistener = _interface;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        long previousTime  = System.currentTimeMillis();

        // I understand this is kinda over kill but I was having error with other timers implementations.
        while (!isCancelled()) {
            Long curentTime = System.currentTimeMillis();

            if (curentTime - previousTime > 1000) {
                publishProgress();
                previousTime = curentTime;
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.i("-------------", "onActivityCreated: updating from the async");
        mlistener.onProgress();
    }
}
