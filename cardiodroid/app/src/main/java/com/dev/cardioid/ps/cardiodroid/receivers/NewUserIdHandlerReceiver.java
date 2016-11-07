package com.dev.cardioid.ps.cardiodroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

public class NewUserIdHandlerReceiver extends BroadcastReceiver {

  public final static String NEW_USER_ID_ACTION = "ble.device.new.user.id.handler.action";
  public final static String NEW_USER_ID_KEY = "ble.device.new.user.id.handler.action.key";

  private final String TAG = Utils.makeLogTag(NewUserIdHandlerReceiver.class);

  public static Intent getHandlerIntent(String extra){
    return new Intent(NEW_USER_ID_ACTION).putExtra(NEW_USER_ID_KEY, extra);
  }

  public NewUserIdHandlerReceiver() {
  }



  @Override public void onReceive(Context context, Intent intent) {
    boolean userHasID = intent.hasExtra(NEW_USER_ID_KEY);
    if (!userHasID) return;

    String newUserId = intent.getStringExtra(NEW_USER_ID_KEY);
    Log.d(TAG, "User ID Received: " + newUserId);
    CardioDroidApplication app = (CardioDroidApplication)context.getApplicationContext();
    PreferencesUtils.saveUserRegisterId(app, newUserId);
    app.setUserAsIdentified(userHasID);
  }
}
