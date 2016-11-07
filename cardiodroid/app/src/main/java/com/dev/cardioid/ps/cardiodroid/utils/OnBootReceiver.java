package com.dev.cardioid.ps.cardiodroid.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receiver that listens for boot events.
 * Some functions of our app are wiped clean after the device
 * turns off (i.e. the scheduled web services that request data updates).
 *
 * After receiving this notifications, the app can setup things properly after
 * the device is properly initiated.
 *
 */
public class OnBootReceiver extends BroadcastReceiver {
  public OnBootReceiver() {
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    // TODO: This method is called when the BroadcastReceiver is receiving
    // an Intent broadcast.
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
