package com.dev.cardioid.ps.cardiodroid.events;

/**
 *  An event that represents a specific bluetooth connectivity change.
 */

public final class BluetoothConnEvent extends ConnectivityChangeEvent {
    public BluetoothConnEvent(boolean isConnected) {
        super(isConnected);
    }
}
