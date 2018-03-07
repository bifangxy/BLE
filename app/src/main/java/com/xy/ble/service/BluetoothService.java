package com.xy.ble.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.xy.ble.utils.BluetoothController;
import com.xy.ble.utils.ConstantUtils;

/**
 * Created by Xieying on 2018/3/5.
 * 自定义蓝牙服务
 */

public class BluetoothService extends Service {
    private static final String LOG_TAG = BluetoothService.class.getSimpleName();

    private BluetoothController bluetooth_controller;

    private Intent my_intent;

    private Handler my_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case ConstantUtils.WM_STOP_SCAN_BLE:
                    if (bluetooth_controller.isDiscovering()) {
                        bluetooth_controller.bluetoothScan(false);
                        my_intent = new Intent(ConstantUtils.ACTION_STOP_DISCOVERY);
                        sendBroadcast(my_intent);
                    }
                    break;
                case ConstantUtils.WM_UPDATE_BLE_LIST:
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) message.obj;
                    my_intent = new Intent(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
                    my_intent.putExtra("name", bluetoothDevice.getName());
                    my_intent.putExtra("address", bluetoothDevice.getAddress());
                    my_intent.putExtra("rssi", message.arg1);
                    sendBroadcast(my_intent);
                    break;
                case ConstantUtils.WM_BLE_CONNECTED_STATE_CHANGE:
                    Bundle bundle = (Bundle) message.obj;
                    my_intent = new Intent(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE);
                    my_intent.putExtra("address", bundle.getString("address"));
                    sendBroadcast(my_intent);
                    break;
                case ConstantUtils.WM_STOP_CONNECT:
                    my_intent = new Intent(ConstantUtils.ACTION_STOP_CONNECT);
                    sendBroadcast(my_intent);
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        bluetooth_controller = BluetoothController.getInstance();
        bluetooth_controller.setServiceHandler(my_handler);
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onDestroy() {
        my_handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
