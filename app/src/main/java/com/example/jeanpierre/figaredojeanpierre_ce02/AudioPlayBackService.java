/*
  Created by jeanpierre on 4/1/18.
  // jean pierre
  // JAV2 - 1804
  //
 */
package com.example.jeanpierre.figaredojeanpierre_ce02;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class AudioPlayBackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_PREPARING = 2;
    private static final int STATE_PREPARED = 3;
    private static final int STATE_STARTED = 4;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_STOPPED = 6;
    private static final int STATE_PLAYBACK_COMPLETED = 7;
    private static final int STATE_END = 8;

    private int mState;
    private MediaPlayer mPlayer;
    private static final int NOTIFICATION_ID = 0x01001;
    private final ArrayList<Music> album = new ArrayList<>();
    private int songIndex = 0;
    private boolean isLoopActiviated = false;



    public AudioPlayBackService() {
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "play":
                        play();
                        break;
                    case "pause":
                        pause();
                        break;
                    case "stop":
                        stop();
                        break;
                    case "skip_next":
                        skipNext();
                        break;
                    case "skip_back":
                        skipPrevius();
                        break;
                }
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i("=---------------", "onCompletion: "+isLoopActiviated);
        if (!isLoopActiviated) {
            songIndex++;
            if (songIndex > 2) {
                songIndex = 0;
            }
        }
        mState = STATE_END;
        play();
    }

    public class AudioServiceBinder extends Binder {
        public AudioPlayBackService getService() {
            return AudioPlayBackService.this;
        }

    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_skip_next_black_24dp);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),album.get(songIndex).getImage()));
        builder.setContentTitle(album.get(songIndex).getTitle());
        builder.setOngoing(true);

        Intent skipback = new Intent();
        skipback.setAction("skip_back");
        PendingIntent pendingSkipback = PendingIntent.getBroadcast(this, 12345, skipback, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_skip_previous_black_24dp, "skip_back", pendingSkipback);

        Intent play = new Intent();
        play.setAction("play");
        PendingIntent pendingPlay = PendingIntent.getBroadcast(this, 12345, play, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_play_arrow_black_24dp, "play", pendingPlay);

        Intent pause = new Intent();
        pause.setAction("pause");
        PendingIntent pendingPause = PendingIntent.getBroadcast(this, 12345, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_pause_black_24dp, "pause", pendingPause);

        Intent stop = new Intent();
        stop.setAction("stop");
        PendingIntent pendingStop = PendingIntent.getBroadcast(this, 12345, stop, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_stop_black_24dp, "stop", pendingStop);

        Intent skipNext = new Intent();
        skipNext.setAction("skip_next");
        PendingIntent pendingNext = PendingIntent.getBroadcast(this, 12345, skipNext, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_skip_next_black_24dp, "skip_next", pendingNext);

        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle());

        Intent activityIntet = new Intent(this, MainActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,0, activityIntet, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(activityPendingIntent);


        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AudioServiceBinder();
    }

    public int getSongLength() {
        return  mPlayer.getDuration();
    }
    public int getCurentPosition() {
        return  mPlayer.getCurrentPosition();
    }

    public Music getcurrentTrack() {
        return album.get(songIndex);
    }

    public boolean isRuning(){
        Log.i("-------------", "isRuning: "+ mPlayer.isPlaying());
       return mPlayer.isPlaying();
    }

    public void shuffle() {
        Random index = new Random();
        songIndex =  index.nextInt(3);
        mState = STATE_END;
        play();
    }

    public void skipNext() {
        songIndex++;
        if (songIndex > 2) {
            songIndex = 0;
        }
        mState = STATE_END;
        play();
    }

    public void skipPrevius() {
        songIndex--;
        if (songIndex < 0) {
            songIndex = 2;
        }
        mState = STATE_END;
        play();
    }

    public void loopSong(Boolean isActive) {
        isLoopActiviated = isActive;
    }

    public void seekTo(int i){
        mPlayer.seekTo(i);
    }

    public void play() {
        if (mState == STATE_PAUSED){
            mPlayer.start();
            mState = STATE_STARTED;

        }
        else if (mState != STATE_STARTED && mState != STATE_PREPARING) {

            mPlayer.reset();
            mState = STATE_IDLE;

            try {
                Uri songUri = Uri.parse("android.resource://" + getPackageName() + "/" + album.get(songIndex).getResorce());
                Log.i("wdw", "play: "+songUri);
                mPlayer.setDataSource(this, songUri);
                mPlayer.setOnCompletionListener(this);
                mState = STATE_INITIALIZED;
            } catch (IOException ignored) {

            }

            if (mState == STATE_INITIALIZED) {

                mPlayer.prepareAsync();
                mState = STATE_PREPARING;
                Notification ongoing = buildNotification();
                startForeground(NOTIFICATION_ID, ongoing);
            }


        }
    }


    public void  pause() {
        if(mState == STATE_STARTED) {
            mPlayer.pause();
            mState = STATE_PAUSED;
        }
    }

    public void stop() {

        if(mState == STATE_STARTED || mState == STATE_PAUSED || mState == STATE_PLAYBACK_COMPLETED) {
            mPlayer.stop();
            mState = STATE_STOPPED;
            stopForeground(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        album.add(new Music("Honey, I'm good.",R.raw.m01,R.mipmap.p01));
        album.add(new Music("Hot & cool",R.raw.m02,R.mipmap.p03));
        album.add(new Music("New years eve mix",R.raw.m03,R.mipmap.p02));
        mPlayer = new MediaPlayer();
        mState = STATE_IDLE;
        mPlayer.setOnPreparedListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("play");
        filter.addAction("pause");
        filter.addAction("stop");
        filter.addAction("skip_next");
        filter.addAction("skip_back");
        filter.addAction("skip_back");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mState = STATE_END;

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mState = STATE_PREPARED;
        mPlayer.start();
        mState = STATE_STARTED;
    }



}
