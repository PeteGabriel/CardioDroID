package com.dev.cardioid.ps.cardiodroid.events;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * TODO
 */
public class BleEventBus extends Bus {
  private static final BleEventBus ourInstance = new BleEventBus(ThreadEnforcer.ANY);

  public static BleEventBus getInstance() {
    return ourInstance;
  }

  public BleEventBus(ThreadEnforcer enforcer) {
    super(enforcer, "CardioDroid-Ble-Bus");
  }
}