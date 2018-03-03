package com.xy.ble.utils;

public class ConstantUtils {
    //消息类型
    public final static int WM_STOP_SCAN_BLE = 1;
    public final static int WM_UPDATE_BLE_LIST = 2;
    //蓝牙连接状态改变
    public final static int WM_BLE_CONNECTED_STATE_CHANGE = 3;
    //接受到蓝牙发来的消息
    public final static int WM_RECEIVE_MSG_FROM_BLE = 4;
    //断开连接或未连接成功
    public final static int WM_STOP_CONNECT = 5;
    //主动断开蓝牙连接
    public final static int WM_DISCONNECT = 6;
    //手环断开连接
    public final static int WM_BAND_STOP_CONNECT = 7;
    //手环连接状态改变
    public final static int WM_BAND_CONNECTED_STATE_CHANGE = 8;

    public final static int WM_FIND_SERVICE = 9;

    public final static int WM_FIND_BAND_SERVICE = 10;

    public final static int WM_TOILET_UPDATE_SUCCESS = 11;
    public final static int WM_TOILET_IC_ERASE_SUCCESS = 12;
    public final static int WM_TOILET_IC_ERASE_Fail = 13;
    public final static int WM_TOILET_IC_WRITE_SUCCESS = 14;
    public final static int WM_TOILET_IC_10_WRITE_FAIL = 15;
    public final static int WM_TOILET_IC_06_WRITE_FAIL = 16;
    public final static int WM_TOILET_IC_10_CHECK_FAIL = 17;
    public final static int WM_TOILET_IC_06_CHECK_FAIL = 18;


    //intent的action们
    public final static String ACTION_UPDATE_DEVICE_LIST = "action.update.device.list";//更新设备列表
    public final static String ACTION_CONNECTED_ONE_DEVICE = "action.connected.one.device";//连接上某个设备时发送的广播
    public final static String ACTION_RECEIVE_MESSAGE_FROM_DEVICE = "action.receive.message";
    public final static String ACTION_STOP_CONNECT = "action.stop.connect";
    public final static String ACTION_STOP_DISCOVERY = "action.stop.discovery";
    public final static String ACTION_BAND_CONNECTED_ONE_DEVICE = "action.band.connected.one.device";
    public final static String ACTION_BAND_STOP_CONNECT = "action.band.stop.connect";
    public final static String ACTION_RECEIVE_MESSAGE_FROM_BAND = "action.receive.band.message";


    //UUID
    public final static String UUID_SERVER = "0000ffe0-0000-1000-8000-00805f9b34fb";
//    public final static String UUID_SERVER = "00001800-0000-1000-8000-00805f9b34fb";

    public final static String UUID_NOTIFY = "0000ffe1-0000-1000-8000-00805f9b34fb";
//    public final static String UUID_NOTIFY = "00002a04-0000-1000-8000-00805f9b34fb";

    public final static String UUID_WRISTBAND_SERVER = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public final static String UUID_WRISTBAND_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    public final static String UUID_WRISTBAND_NOTIFY = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    public final static String UUID_WRISTBAND_NOTIFY1 = "00001530-1212-efde-1523-785feabcd123";

    public final static String UUID_WRISTBAND_NOTIFY2 = "00001534-1212-efde-1523-785feabcd123";


    //手环
    //步数详细信息
    public final static String BAND_STEP_DATA_DETAIL = "band_step_data_detail";
    //睡眠详细信息
    public final static String BAND_SLEEP_DATA_DETAIL = "band_sleep_data_detail";
    //心率详细信息
    public final static String BAND_HEART_RATE_DATA_DETAIL = "band_heart_rate_data_detail";
    //血压详细信息
    public final static String BAND_BLOOD_PRESSURE_DATA_DETAIL = "band_blood_pressure_data_detail";
    //血氧详细信息
    public final static String BAND_BLOOD_OXYGEN_DATA_DETAIL = "band_blood_oxygen_data_detail";
    //最近一次心率测量数据
    public final static String BAND_RECENT_HEART_RATE = "recent_heart_rate";
    //最近一次血压测量数据
    public final static String BAND_RECENT_BLOOD_PRESSURE = "recent_blood_pressure";
    //最近一次血氧测量数据
    public final static String BAND_RECENT_BLOOD_OXYGEN = "recent_blood_oxygen";
    //目前步数信息
    public final static String BAND_CURRENT_STEP = "band_current_step";
    //目前睡眠信息
    public final static String BAND_CURRENT_SLEEP = "band_current_sleep";
    //目标步数
    public final static String BAND_TARGET_STEP = "band_target_step";


    public final static String TOILET_UPDATE_SUCCESS = "action.toilet.update.success";
    public final static String TOILET_IC_ERASE_SUCCESS = "action.toilet.ic.erase.success";
    public final static String TOILET_IC_ERASE_Fail = "action.toilet.ic.erase.fail";
    public final static String TOILET_IC_WRITE_SUCCESS = "action.toilet.ic.write.success";
    public final static String TOILET_IC_10_WRITE_FAIL = "action.toilet.ic.10.write.fail";
    public final static String TOILET_IC_06_WRITE_FAIL = "action.toilet.ic.06.write.fail";
    public final static String TOILET_IC_10_CHECK_FAIL = "action.toilet.ic.10.check.fail";
    public final static String TOILET_IC_06_CHECK_FAIL = "action.toilet.ic.06.check.fail";

}
