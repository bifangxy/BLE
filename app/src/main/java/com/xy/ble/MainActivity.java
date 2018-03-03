package com.xy.ble;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.xy.ble.utils.PixelUtil;
import com.xy.ble.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private LoadingView mLoadingView;

    private RelativeLayout rl;

    private ListView lv_devices;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    performAnim();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();

    }

    private void initView() {
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        rl = (RelativeLayout) findViewById(R.id.rl);
        lv_devices = (ListView) findViewById(R.id.lv_devices);
    }

    private void initData() {
        mLoadingView.update();
        mHandler.sendEmptyMessageDelayed(1, 5000);

        List<String> dataList = new ArrayList<>();
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        dataList.add("Pooai-08");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lv_devices.setAdapter(adapter);
    }


    private void performAnim() {
        ValueAnimator va_one = ValueAnimator.ofInt(rl.getHeight(), PixelUtil.dp2px(mContext, 250));
        va_one.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int h = (Integer) valueAnimator.getAnimatedValue();
                rl.getLayoutParams().height = h;
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
        animSet.setDuration(1000);
        animSet.start();

    }


}
