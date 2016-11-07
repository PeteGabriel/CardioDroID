package com.dev.cardioid.ps.cardiodroid.rules.actions;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.rules.IAction;

/**
 * Action that makes the device's alarm sound play.
 */
public class AlarmSoundAction implements IAction {

  private Context ctx;

  private SoundPool soundPlayer;

  public AlarmSoundAction(Context ctx){
    this.ctx = ctx;
  }

  @Override
  public void execute() {
    Log.d("AlarmAction", "PLAY THE ALARM");
    play(R.raw.alarm);
  }

  @Override public String toString() {
    return "ALARM_SOUND ACTION";
  }


  private void play(int resourceToPlay) {
    MediaPlayer mPlayer2;
    mPlayer2= MediaPlayer.create(ctx, resourceToPlay);
    mPlayer2.start();
  }
}
