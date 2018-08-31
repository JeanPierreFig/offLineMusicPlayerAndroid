/*
  Created by jeanpierre on 4/1/18.
  // jean pierre
  // JAV2 - 1804
  // ${FILE}
 */

package com.example.jeanpierre.figaredojeanpierre_ce02;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class ControlFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, AsyncTimer.TimerInterface{

    public static final String TAG = "ControlFragment.TAG";
    private SeekBar seekBar;
    private ImageView TrackImage;
    private AsyncTimer timerThread = null;
    private boolean isSeekActive = false;
    private Music currentTrack;
    private Switch loopSwitch;
    private TextView title;

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (isSeekActive) {
            mListener.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekActive = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekActive = false;
    }

    @Override
    public void onProgress() {
        if(mListener.isPlaying()) {
            if (currentTrack != mListener.getCurrentTrack() && mListener.getCurrentTrack() != null) {
                currentTrack = mListener.getCurrentTrack();
                TrackImage.setImageResource(currentTrack.getImage());
                title.setText(currentTrack.getTitle());
            }
            seekBar.setMax(mListener.setmax());
            int current = mListener.getCurrentPosition();
            seekBar.setProgress(current);
        }
    }

    public interface PlaybackCommandListener {
        void play();
        void pause();
        void stop();
        void seekTo(int index);
        void skipNext();
        void skipPreviouse();
        void loopSong(Boolean isActive);
        void shuffle();
        boolean isPlaying();
        int getCurrentPosition();
        int setmax();
        Music getCurrentTrack();
    }

    private PlaybackCommandListener mListener;

    public static ControlFragment newInstance() {
        return new ControlFragment();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof  PlaybackCommandListener) {
            Log.i("-------------", "onActivityCreated: attach");

            mListener = (PlaybackCommandListener)context;

        }
    }

    @Override
    public void onClick(View view) {

        if(mListener == null ) {
            return;
        }
        if(view.getId() == R.id.button_play) {
            mListener.play();
            if (timerThread == null) {
                    timerThread = new AsyncTimer(this);
                    timerThread.execute();
            }

        } else if (view.getId() == R.id.button_pause) {
            mListener.pause();
        }
        else if (view.getId() == R.id.button_stop) {
            mListener.stop();
        }
        else if (view.getId() == R.id.skip_next) {
            mListener.skipNext();
            loopSwitch.setChecked(false);
        }
        else if (view.getId() == R.id.skip_previous) {
            mListener.skipPreviouse();
            loopSwitch.setChecked(false);
        }
        else if (view.getId() == R.id.loop_switch) {
            mListener.loopSong(loopSwitch.isChecked());
        }
        else if (view.getId() == R.id.shuffle) {
            mListener.shuffle();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.control_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View root = getView();
        if(root != null) {
            root.findViewById(R.id.button_play).setOnClickListener(this);
            root.findViewById(R.id.button_stop).setOnClickListener(this);
            root.findViewById(R.id.button_pause).setOnClickListener(this);
            root.findViewById(R.id.skip_next).setOnClickListener(this);
            root.findViewById(R.id.skip_previous).setOnClickListener(this);
            root.findViewById(R.id.shuffle).setOnClickListener(this);
            title = root.findViewById(R.id.titleView);
            loopSwitch = root.findViewById(R.id.loop_switch);
            loopSwitch.setOnClickListener(this);
            TrackImage = root.findViewById(R.id.imageView);
            seekBar = root.findViewById(R.id.seekBar);
            seekBar.setOnSeekBarChangeListener(this);

            if (timerThread == null) {
                timerThread = new AsyncTimer(this);
                timerThread.execute();
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerThread.cancel(true);
    }

}