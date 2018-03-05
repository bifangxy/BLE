package com.xy.ble;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.xy.ble.adapter.DeviceAdapter;
import com.xy.ble.data.BleDevice;
import com.xy.ble.utils.BluetoothController;
import com.xy.ble.utils.ConstantUtils;
import com.xy.ble.utils.PixelUtil;
import com.xy.ble.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private LoadingView mLoadingView;

    private RelativeLayout rl;

    private RecyclerView rcv_devices;

    private BluetoothController bluetoothController;

    private DeviceAdapter device_adapter;

    private List<BleDevice> device_list;

    private MyReceiver my_receiver;

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
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();

    }

    private void initView() {
        mLoadingView = findViewById(R.id.loadingView);
        rl = findViewById(R.id.rl);
        rcv_devices = findViewById(R.id.rcv_devices);
    }

    private void initData() {
        device_list = new ArrayList<>();
        BleDevice bleDevice = new BleDevice();
        bleDevice.setDevice_address("14:25:56:78");
        bleDevice.setDevice_state(0);
        bleDevice.setDevice_name("Pooai08");
        device_list.add(bleDevice);
        mLoadingView.update();
        my_handler.sendEmptyMessageDelayed(1, 5000);
        initRecycleView();
        initBroadcast();
    }

    private void initBroadcast() {
        IntentFilter my_intentFilter = new IntentFilter();
        my_receiver = new MyReceiver();

        my_intentFilter.addAction(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE);
        my_intentFilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND);
        my_intentFilter.addAction(ConstantUtils.ACTION_STOP_DISCOVERY);
        my_intentFilter.addAction(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);

        registerReceiver(my_receiver, my_intentFilter);
    }

    private void initRecycleView() {
        rcv_devices.setLayoutManager(new LinearLayoutManager(this));
        device_adapter = new DeviceAdapter(R.layout.device_item, device_list);
        rcv_devices.setAdapter(device_adapter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(my_receiver);
        super.onDestroy();
    }

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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ConstantUtils.ACTION_UPDATE_DEVICE_LIST)) {
                    String name = intent.getStringExtra("name");
                    String address = intent.getStringExtra("address");
                    String rssi = intent.getStringExtra("rssi");
                    BleDevice bleDevice = new BleDevice();
                    bleDevice.setDevice_state(0);
                    bleDevice.setDevice_name(name);
                    bleDevice.setDevice_address(address);
                    bleDevice.setDevice_rssi(rssi);
                    device_list.add(bleDevice);
                    my_handler.sendEmptyMessage(2);
                }
            }
        }
    }


}
