package com.xy.ble;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xy.ble.adapter.DeviceAdapter;
import com.xy.ble.data.BleDevice;
import com.xy.ble.service.BluetoothService;
import com.xy.ble.utils.BluetoothController;
import com.xy.ble.utils.ConstantUtils;
import com.xy.ble.utils.PixelUtil;
import com.xy.ble.view.LoadingView;

import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Xieying on 2018/2/27.
 * 蓝牙Activity
 */

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = BluetoothActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 0;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    private Context mContext;

    private LoadingView mLoadingView;

    private RelativeLayout rl;

    private RecyclerView rcv_devices;

    private ImageView iv_search;

    private BluetoothController bluetoothController;

    private DeviceAdapter device_adapter;

    private List<BleDevice> device_list;

    private MyReceiver my_receiver;

    private BluetoothService my_bluetoothService;

    private ObjectAnimator search_animation;

    private Handler my_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    performAnim();
                    break;
                case 2:
                    device_adapter.notifyDataSetChanged();
                    break;
                case 3:
                    mLoadingView.stopOpeningAnimation();
                    break;
            }
            return false;
        }
    });

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder localBinder = (BluetoothService.LocalBinder) service;
            my_bluetoothService = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
        initListener();

    }


    private void initView() {
        mLoadingView = findViewById(R.id.loadingView);
        rl = findViewById(R.id.rl);
        rcv_devices = findViewById(R.id.rcv_devices);
        iv_search = findViewById(R.id.iv_search);
    }

    private void initData() {
        initBluetoothService();

        bluetoothController = BluetoothController.getInstance();
        if (!bluetoothController.initBLE()) {
            Toast.makeText(mContext, "该设备不支持蓝牙BLE", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            device_list = new ArrayList<>();
            initRecycleView();
            initBroadcast();
        }
    }


    private void initListener() {
        iv_search.setOnClickListener(this);
        mLoadingView.setOnClickListener(this);
        device_adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                connect_device(position);
            }
        });
    }


    //初始化广播
    private void initBroadcast() {
        IntentFilter my_intentFilter = new IntentFilter();
        my_receiver = new MyReceiver();

        my_intentFilter.addAction(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE);
        my_intentFilter.addAction(ConstantUtils.ACTION_STOP_DISCOVERY);
        my_intentFilter.addAction(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
        my_intentFilter.addAction(ConstantUtils.ACTION_STOP_CONNECT);
        my_intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(my_receiver, my_intentFilter);

        mLoadingView.setOpen(bluetoothController.isBleOpen());
    }

    //初始化recycleView
    private void initRecycleView() {
        rcv_devices.setLayoutManager(new LinearLayoutManager(this));
        device_adapter = new DeviceAdapter(R.layout.device_item, device_list);
        rcv_devices.setAdapter(device_adapter);
    }

    //初始化BluetoothService
    private void initBluetoothService() {
        Intent intent = new Intent(mContext, BluetoothService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    //搜索按钮点击事件
    private void search() {
        //判断蓝牙是否正在搜索
        if (!bluetoothController.isDiscovering()) {
            //判断蓝牙是否已经连接
            if (bluetoothController.getConnect_state() == 1) {
                //是否断开当前连接设备重新搜索
                LemonHello.getWarningHello(getString(R.string.connected) + bluetoothController.getTargetDevice().getName(), getString(R.string.whether_disconnected_bluetooth))
                        .addAction(new LemonHelloAction(getString(R.string.cancel), new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                                lemonHelloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                                bluetoothController.disConnect();
                                checkOpen();
                                lemonHelloView.hide();
                            }
                        }))
                        .show(mContext);
            } else {
                checkOpen();
            }
        } else {
            stopSearch();
        }
    }


    //检查蓝牙是否打开
    private void checkOpen() {
        if (bluetoothController.isBleOpen()) {
            checkBluetoothPermission();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    //检查是否有定位权限
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(BluetoothActivity.this,
                        Manifest.permission.READ_CONTACTS)) {
                    LemonHello.getInformationHello(getString(R.string.tips), getString(R.string.loaction_access_describe))
                            .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                                @Override
                                public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                                    lemonHelloView.hide();
                                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                                }
                            }))
                            .show(mContext);
                    return;
                }
                //请求权限
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                startSearch();
            }
        } else {
            startSearch();
        }
    }

    //开始搜索，初始化List集合，开始搜索动画
    private void startSearch() {
        device_list.clear();
        device_adapter.notifyDataSetChanged();
        bluetoothController.bluetoothScan(true);
        mLoadingView.startSearchingAnimation();
        searchAnimation();
    }

    //停止搜索
    private void stopSearch() {
        bluetoothController.stopScan();
    }

    //连接设备
    private void connect_device(final int position) {
        //检查蓝牙是否已连接
        if (bluetoothController.getConnect_state() == 1) {
            //检查连接的蓝牙是否为当前设备
            if (device_list.get(position).getDevice_address().equals(bluetoothController.getTargetDevice().getAddress())) {
                Toast.makeText(mContext, R.string.device_already_connected, Toast.LENGTH_SHORT).show();
            } else {
                //是否断开当前蓝牙连接，连接另一个蓝牙设备
                LemonHello.getWarningHello(getString(R.string.connected) + bluetoothController.getTargetDevice().getName(), getString(R.string.whether_disconnect_this_connect_other))
                        .addAction(new LemonHelloAction(getString(R.string.cancel), new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                                lemonHelloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                                bluetoothController.disConnect();
                                connect_device(position);
                                lemonHelloView.hide();
                            }
                        }))
                        .show(mContext);

            }
        } else {
            //检查蓝牙是否在搜索的状态
            if (bluetoothController.isDiscovering()) {
                stopSearch();
            }
            BleDevice bleDevice = device_list.get(position);
            device_list.get(position).setDevice_state(2);
            device_adapter.notifyDataSetChanged();
            bluetoothController.connectDevice(bleDevice);
            mLoadingView.startConnectingAnimation();
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(my_receiver);
        unbindService(serviceConnection);
        my_handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    //recycleView显示动画
    private void performAnim() {
        ValueAnimator va_one = ValueAnimator.ofInt(rl.getHeight(), PixelUtil.dp2px(mContext, 250));
        va_one.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                rl.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                rl.requestLayout();
            }
        });

        ValueAnimator va_two = ValueAnimator.ofInt(mLoadingView.getHeight(), PixelUtil.dp2px(mContext, 150));
        va_two.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int h = (Integer) valueAnimator.getAnimatedValue();
                mLoadingView.getLayoutParams().height = h;
                mLoadingView.getLayoutParams().width = h;
                mLoadingView.requestLayout();
            }
        });
        ObjectAnimator anim = ObjectAnimator.ofFloat(mLoadingView, "rotation", 0f, 90f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(va_one).with(va_two).with(anim);
        animSet.setDuration(500);
        animSet.start();
    }

    //底部搜索图标动画
    private void searchAnimation() {
        search_animation = ObjectAnimator.ofFloat(iv_search, "rotation", 0, 360);
        search_animation.setRepeatCount(-1);
        search_animation.setDuration(1000);
        search_animation.setRepeatMode(ValueAnimator.RESTART);
        search_animation.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext, R.string.please_allow_open_bluetooth, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSearch();
            } else {
                Toast.makeText(mContext, R.string.can_not_access_location_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loadingView:
                search();
                break;
            case R.id.iv_search:
                search();
                break;
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ConstantUtils.ACTION_UPDATE_DEVICE_LIST:
                        String name = intent.getStringExtra("name");
                        String address = intent.getStringExtra("address");
                        int rssi = intent.getIntExtra("rssi", 0);
                        BleDevice bleDevice = new BleDevice();
                        bleDevice.setDevice_state(0);
                        bleDevice.setDevice_name(name);
                        bleDevice.setDevice_address(address);
                        bleDevice.setDevice_rssi(rssi);
                        if (!device_list.contains(bleDevice)) {
                            device_list.add(bleDevice);
                            my_handler.sendEmptyMessage(2);
                        }
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                        if (state == BluetoothAdapter.STATE_TURNING_ON) {
                            mLoadingView.startOpeningAnimation();
                        } else if (state == BluetoothAdapter.STATE_ON) {
                            mLoadingView.stopOpeningAnimation();
                            checkBluetoothPermission();
                        }
                        break;
                    case ConstantUtils.ACTION_CONNECTED_ONE_DEVICE:
                        mLoadingView.stopConnectingAnimation();
                        String connect_address = intent.getStringExtra("address");
                        for (int i = 0; i < device_list.size(); i++) {
                            if (device_list.get(i).getDevice_address().equals(connect_address)) {
                                device_list.get(i).setDevice_state(1);
                                device_adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case ConstantUtils.ACTION_STOP_CONNECT:
                        for (int i = 0; i < device_list.size(); i++) {
                            if (device_list.get(i).getDevice_address().equals(bluetoothController.getTargetDevice().getAddress())) {
                                device_list.get(i).setDevice_state(0);
                                device_adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case ConstantUtils.ACTION_STOP_DISCOVERY:
                        if (mLoadingView.getHeight() != PixelUtil.dp2px(mContext, 150)) {
                            performAnim();
                        }
                        mLoadingView.stopSearchingAnimation();
                        search_animation.cancel();
                        break;
                }
            }
        }
    }


}
