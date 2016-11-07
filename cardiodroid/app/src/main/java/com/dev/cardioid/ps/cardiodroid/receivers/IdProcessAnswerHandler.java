package com.dev.cardioid.ps.cardiodroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.services.ble.BleDefinedUuid;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

public class IdProcessAnswerHandler extends BroadcastReceiver {

  private final String TAG = Utils.makeLogTag(IdProcessAnswerHandler.class);

  public static final String ID_PROCESS_ANSWER_RECEIVED_ACTION = "id.process.answer.received.action";

  public static final String ID_PROCESS_ANSWER_RECEIVED_KEY = "id.process.answer.received.key";

  public IdProcessAnswerHandler() {
  }


  private boolean isAnswerGood(String value){
    return value.equals(BleDefinedUuid.Protocol.OK_ANSWER);
  }

  @Override public void onReceive(Context context, Intent intent) {
    String value = intent.getStringExtra(ID_PROCESS_ANSWER_RECEIVED_KEY);
    Log.d(TAG, "Response received from Service: " + value);

    boolean isIdentified = isAnswerGood(value);
    //((CardioDroidApplication)context.getApplicationContext()).markUserAsIdentified(isIdentified);

    String goodSign = context.getResources().getString(R.string.user_is_identified),
        badSig = context.getResources().getString(R.string.user_is_not_identified);
    String barMessage = isIdentified ? goodSign : badSig;
    ToastUtils.showMessage(barMessage, context);
  }
}
