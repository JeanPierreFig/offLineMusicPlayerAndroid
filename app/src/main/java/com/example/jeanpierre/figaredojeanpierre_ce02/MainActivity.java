/*
  Created by jeanpierre on 4/1/18.
  // jean pierre
  // JAV2 - 1804
  //
 */
package com.example.jeanpierre.figaredojeanpierre_ce02;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements ServiceConnection, ControlFragment.PlaybackCommandListener {

    private AudioPlayBackService mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            ControlFragment frag = ControlFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.FragmentContainer, frag, ControlFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBound = false;
        Intent serviceIntent = new Intent(this, AudioPlayBackService.class);
        bindService(serviceIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        mBound = false;

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i("-------------", "getCurrentPosition: started");
        AudioPlayBackService.AudioServiceBinder binder  = (AudioPlayBackService.AudioServiceBinder) iBinder;
        mService = binder.getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.i("-------------", "getCurrentPosition: disconected");
        mBound = false;
    }


    @Override
    public void play() {
        if (mBound) {
            mService.play();
            Intent serviceIntent = new Intent(this, AudioPlayBackService.class);
            startService(serviceIntent);
        }

    }

    @Override
    public void pause() {
        if (mBound) {
            mService.pause();
        }
    }

    @Override
    public void stop() {
        if (mBound) {
            mService.stop();
            Intent serviceIntent = new Intent(this, AudioPlayBackService.class);
            stopService(serviceIntent);
        }
    }

    @Override
    public int setmax() {
        if (mBound) {
            return mService.getSongLength();
        }
        return 100;
    }

    @Override
    public Music getCurrentTrack() {
        if (mBound) {
            return mService.getcurrentTrack();
        }
        return null;
    }

    @Override
    public void seekTo(int index) {
        if (mBound) {
            mService.seekTo(index);
        }
    }

    @Override
    public void skipNext() {
        if (mBound) {
            mService.skipNext();
        }
    }

    @Override
    public void skipPreviouse() {
        if (mBound) {

            mService.skipPrevius();
        }
    }

    @Override
    public void loopSong(Boolean isActive) {
        if (mBound) {
            mService.loopSong(isActive);
        }
    }

    @Override
    public void shuffle() {
        if (mBound) {
            mService.shuffle();
        }
    }

    @Override
    public boolean isPlaying() {
        return mBound && mService.isRuning();
    }

    @Override
    public int getCurrentPosition() {
        if (mBound) {
            Log.i("-------------", "getCurrentPosition: call");
            return mService.getCurentPosition();
        }
        return 0;
    }


}
