package com.dev.cardioid.ps.cardiodroid.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.BluetoothConnEvent;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

public class BluetoothStateReceiver extends BroadcastReceiver {
    public BluetoothStateReceiver() {
    }

    private final String TAG = Utils.makeLogTag(ConnectivityReceiver.class);


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Bluetooth Connectivity Change.");

        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

        boolean isConnected = translateState(state);
        BleEventBus.getInstance().post(new BluetoothConnEvent(isConnected));
    }

    private boolean translateState(int state){
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.ERROR:
                return false;
            case BluetoothAdapter.STATE_ON:
                return true;
            default: return false;
        }
    }
}
