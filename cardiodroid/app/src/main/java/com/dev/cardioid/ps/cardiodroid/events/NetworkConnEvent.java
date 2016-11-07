package com.dev.cardioid.ps.cardiodroid.events;

/**
 *  An event that represents a specific network connectivity change.
 */

public final class NetworkConnEvent extends ConnectivityChangeEvent {

    public NetworkConnEvent(boolean isConnected) {
        super(isConnected);
    }

}
