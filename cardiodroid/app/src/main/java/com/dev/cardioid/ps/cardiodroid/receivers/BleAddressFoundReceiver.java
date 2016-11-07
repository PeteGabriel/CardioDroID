package com.dev.cardioid.ps.cardiodroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.events.BleEventBus;
import com.dev.cardioid.ps.cardiodroid.events.DeviceAddressEvent;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * This receiver intends to deal with the broadcast of BLE device's address
 * found after a search done from one of the services
 * ({@link com.dev.cardioid.ps.cardiodroid.services.ble.SearchBleDeviceService}).
 *
 */
public class BleAddressFoundReceiver extends BroadcastReceiver {

  public static final String TAG = Utils.makeLogTag(BleAddressFoundReceiver.class);

  public BleAddressFoundReceiver() {
  }

  //action deste receiver
  public static final String DEVICE_ADDRESS_HANDLER_ACTION = "ble.device.address.handler.action";


  public static final String DEVICE_ADDRESS_HANDLER_KEY = "ble.address.handler.intent.key";

  @Override
  public void onReceive(Context context, Intent intent) {
    String address = intent.getStringExtra(DEVICE_ADDRESS_HANDLER_KEY);

    String msg = address == null ? "Address Not Found" : "Address Received: " + address;
    Log.d(TAG, msg);

    BleEventBus.getInstance().post(new DeviceAddressEvent(address));
  }

}
