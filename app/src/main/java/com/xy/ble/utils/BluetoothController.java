package com.xy.ble.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.xy.ble.MyApplication;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class BluetoothController {
    private static final String LOG_TAG = BluetoothController.class.getSimpleName();

    private static BluetoothController mInstance;

    private BluetoothAdapter bleAdapter;

    private BluetoothLeScanner bleScanner;

    private BluetoothGatt bleGatt;

    private BluetoothGattCharacteristic bleGattCharacteristic;

    private BluetoothGattCharacteristic bandWriteCharacteristic;

    private BluetoothGattCharacteristic bandNotifyCharacteristic;

    private Handler mServiceHandler;

    private String deviceAddress;

    private boolean isScan = false;


    public BluetoothController() {
    }

    public static BluetoothController getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothController.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothController();
                }
            }
        }
        return mInstance;
    }

    public void setServiceHandler(Handler handler) {
        mServiceHandler = handler;
    }


    public boolean initBLE() {
        if (!MyApplication.getInstance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) MyApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();
        if (bleAdapter == null) {
            return false;
        }
        return true;
    }

    public boolean isBleOpen() {
        return bleAdapter.isEnabled();
    }

    public void startScan() {
        bleScanner = bleAdapter.getBluetoothLeScanner();
        bleScanner.startScan(scanCallback);
        isScan = true;
        if (mServiceHandler != null) {
            mServiceHandler.sendEmptyMessageDelayed(ConstantUtils.WM_STOP_SCAN_BLE, 10000);
        } else {

        }
    }

    public boolean isDiscovering() {
        return isScan;
    }

    public void sendStopScanMessage() {
        if (mServiceHandler != null) {
            mServiceHandler.sendEmptyMessage(ConstantUtils.WM_STOP_SCAN_BLE);
        }
    }

    public void connectDevice(String address) {
        deviceAddress = address;
        BluetoothDevice localBluetoothDevice = bleAdapter.getRemoteDevice(deviceAddress);
        if (bleGatt != null) {
            bleGatt.disconnect();
            bleGatt.close();
            bleGatt = null;
        }
        bleGatt = localBluetoothDevice.connectGatt(MyApplication.getInstance(), false, bleGattCallback);
    }

    public void stopScan() {
        bleScanner.stopScan(scanCallback);
        isScan = false;
    }


    public boolean write(byte byteArray[]) {
        if (bleGattCharacteristic == null) {
            return false;
        }
        if (bleGatt == null) {
            return false;
        }
        bleGattCharacteristic.setValue(byteArray);
        return bleGatt.writeCharacteristic(bleGattCharacteristic);
    }


    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothDevice device = result.getDevice();
                String deviceName = device.getName();
                if (deviceName == null) {
                    return;
                } else if (mServiceHandler != null && !deviceName.isEmpty()) {
                    Message message = new Message();
                    message.what = ConstantUtils.WM_UPDATE_BLE_LIST;
                    message.obj = device;
                    message.arg1 = result.getRssi();
                    mServiceHandler.sendMessage(message);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    public void disConnect() {
        if (bleGatt != null) {
            bleGatt.disconnect();
            bleGatt.close();
            bleGatt = null;
            mServiceHandler.sendEmptyMessage(ConstantUtils.WM_DISCONNECT);
        }
    }

    BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == 2) {
                Log.d(LOG_TAG, "连接成功");
                Message message = new Message();
                message.what = ConstantUtils.WM_BAND_CONNECTED_STATE_CHANGE;
                Bundle bundle = new Bundle();
                bundle.putString("address", deviceAddress);
                message.obj = bundle;
                mServiceHandler.sendMessage(message);
                gatt.discoverServices();
                return;
            }
            if (newState == 0) {
                Log.d(LOG_TAG, "断开连接");
                mServiceHandler.sendEmptyMessage(ConstantUtils.WM_BAND_STOP_CONNECT);
                connectDevice(deviceAddress);
            }
            gatt.disconnect();
            gatt.close();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            findService(gatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] arrayOfbyte = characteristic.getValue();
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private void findService(List<BluetoothGattService> paramList) {
        Iterator iterator1 = paramList.iterator();
        while (iterator1.hasNext()) {
            BluetoothGattService localBluetoothGattService = (BluetoothGattService) iterator1
                    .next();
            if (localBluetoothGattService.getUuid().toString().equalsIgnoreCase(ConstantUtils.UUID_SERVER)) {
                List localList = localBluetoothGattService.getCharacteristics();
                Iterator iterator2 = localList.iterator();
                while (iterator2.hasNext()) {
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = (BluetoothGattCharacteristic) iterator2.next();
                    if (bluetoothGattCharacteristic.getUuid().toString().equalsIgnoreCase(ConstantUtils.UUID_NOTIFY)) {
                        bleGattCharacteristic = bluetoothGattCharacteristic;
                        break;
                    }
                }
                break;
            }
        }
        boolean isEnableNotification = bleGatt.setCharacteristicNotification(bleGattCharacteristic, true);
        if (isEnableNotification) {
            List<BluetoothGattDescriptor> descriptorList = bleGattCharacteristic.getDescriptors();
            if (descriptorList != null && descriptorList.size() > 0) {
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bleGatt.writeDescriptor(descriptor);
                    mServiceHandler.sendEmptyMessageDelayed(ConstantUtils.WM_FIND_SERVICE, 100);
                }
            }
        }
    }

}
