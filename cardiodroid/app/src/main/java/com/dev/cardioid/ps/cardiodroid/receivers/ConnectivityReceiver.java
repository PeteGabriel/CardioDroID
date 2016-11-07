package com.dev.cardioid.ps.cardiodroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.NetworkConnEvent;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * In order to know when the device has connected to some network
 * we listen to the connectivity event sent by the system to everyone who
 * subscribe to it.
 *
 * Every time the connectivity changes, an event is sent
 * making those who subscribe to it aware of this fact.
 *
 */
public class ConnectivityReceiver extends BroadcastReceiver{

  private final String TAG = Utils.makeLogTag(ConnectivityReceiver.class);


  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Internet Connectivity Change.");
    long connType = PreferencesUtils.getConnectionSpecifiedByUser(context);
    //if connected to the type specified in settings...
    boolean isConnected = NetworkUtils.isConnected(context, connType);
    Log.d(TAG, "Connectivity Change: " + (isConnected ?  "Connected": "Disconnected"));
    BleEventBus.getInstance().post(new NetworkConnEvent(isConnected)); //TODO receber este evento no dashboard
  }
}
