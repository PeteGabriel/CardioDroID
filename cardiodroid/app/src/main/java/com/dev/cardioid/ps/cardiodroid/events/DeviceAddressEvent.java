package com.dev.cardioid.ps.cardiodroid.events;

import com.dev.cardioid.ps.cardiodroid.receivers.BleAddressFoundReceiver;
import com.dev.cardioid.ps.cardiodroid.services.ble.SearchBleDeviceService;

/**
 * Event thrown to let subscribers know if an address
 * was found or not.
 *
 * @see SearchBleDeviceService
 * @see BleAddressFoundReceiver
 */
public class DeviceAddressEvent {

    private String address;

    public DeviceAddressEvent(String address){
        this.address = address;
    }

    public boolean hasAddress(){
        return this.address != null;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Address Found: " + this.address;
    }
}
