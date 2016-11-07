package com.dev.cardioid.ps.cardiodroid.events;

/**
 * An event that represents a general connectivity change.
 */
public abstract class ConnectivityChangeEvent {

  private boolean isConnected;

  public ConnectivityChangeEvent(boolean isConnected){
    this.isConnected = isConnected;
  }

  public boolean isConnected() {
    return isConnected;
  }
}
