package com.dev.cardioid.ps.cardiodroid.rules.actions;

import android.content.Context;

import com.dev.cardioid.ps.cardiodroid.rules.IAction;

/**
 * Used to create instances
 * of a {@link IAction} concrete implementation.
 */
public final class Action {

  public static IAction createAction(Context ctx, String type){
    switch (type){
      case IAction.Type.REMOTE_LOG_ACTION:
        return new RemoteLogAction();
      case IAction.Type.ALARM_SOUND_ACTION:
        return new AlarmSoundAction(ctx);
      case IAction.Type.VIBRATE_ACTION:
        return new VibrateAction(ctx);
      default:
        return null;
    }
  }
}
