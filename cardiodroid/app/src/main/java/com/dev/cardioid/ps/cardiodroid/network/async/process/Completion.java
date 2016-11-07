package com.dev.cardioid.ps.cardiodroid.network.async.process;

import com.dev.cardioid.ps.cardiodroid.services.ble.SearchBleDeviceService;

public interface Completion<T> {

  /**
   * The callback invoked when the result is available.
   */
  void onResult(CallResult<T>  result);
  //void onResult();
}