package com.xy.ble.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xy.ble.R;
import com.xy.ble.data.BleDevice;

import java.util.List;

/**
 * Created by Xieying on 2018/3/1.
 */

public class DeviceAdapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {

    public DeviceAdapter(int layoutResId, @Nullable List<BleDevice> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BleDevice item) {
        helper.setText(R.id.tv_device_name, item.getDevice_name())
                .setText(R.id.tv_device_address, item.getDevice_address());
        if (item.getDevice_state() == 1) {
            helper.setText(R.id.tv_device_state, "已连接");
            helper.setGone(R.id.tv_device_state, true);
        } else if (item.getDevice_state() == 2) {
            helper.setText(R.id.tv_device_state, "正在连接");
            helper.setGone(R.id.tv_device_state, true);
        } else {
            helper.setGone(R.id.tv_device_state, false);
        }
    }

}
