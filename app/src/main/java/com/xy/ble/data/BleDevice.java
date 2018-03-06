package com.xy.ble.data;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/5.
 */

public class BleDevice implements Serializable {
    private String device_name;

    private String device_address;

    private int device_rssi;

    /**
     * 0,未连接 1，已连接 2，正在连接 3，连接断开
     */
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

    public int getDevice_rssi() {
        return device_rssi;
    }

    public void setDevice_rssi(int device_rssi) {
        this.device_rssi = device_rssi;
    }

    public int getDevice_state() {
        return device_state;
    }

    public void setDevice_state(int device_state) {
        this.device_state = device_state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BleDevice) {
            BleDevice bleDevice = (BleDevice) obj;
            return this.device_name.equals(bleDevice.device_name)
                    && this.device_address.equals(device_address);
        }
        return false;
    }
}
