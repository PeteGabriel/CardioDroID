package com.dev.cardioid.ps.cardiodroid.rules.actions;

import android.content.Context;
import android.os.Vibrator;

import com.dev.cardioid.ps.cardiodroid.rules.IAction;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;

/**
 * Action that makes the device's vibrate.
 */
public class VibrateAction implements IAction{

    private Context mCtx;
    private int duration;

    public VibrateAction(Context ctx){
        mCtx = ctx;
        duration = PreferencesUtils.getVibrateActionDuration(mCtx);
    }

    @Override
    public void execute() {
        ((Vibrator)mCtx.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(duration);
    }

    @Override
    public String toString() {
        return "VIBRATE ACTION";
    }
}
