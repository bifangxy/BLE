package com.xy.ble.data;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/5.
 */

public class BleDevice implements Serializable {
    private String device_name;

    private String device_address;

    private String device_rssi;

    private int device_state;

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }

    public String getDevice_rssi() {
        return device_rssi;
    }

    public void setDevice_rssi(String device_rssi) {
        this.device_rssi = device_rssi;
    }

    public int getDevice_state() {
        return device_state;
    }

    public void setDevice_state(int device_state) {
        this.device_state = device_state;
    }
}
