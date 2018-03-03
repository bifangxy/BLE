package com.xy.ble;

import android.app.Application;

/**
 * Created by Administrator on 2018/3/2.
 */

public class MyApplication extends Application {
    private static final String LOG_TAG = MyApplication.class.getSimpleName();

    private static MyApplication mApplication;

    public static MyApplication getInstance() {
        if (mApplication == null) {
            synchronized (MyApplication.class) {
                if (mApplication == null) {
                    mApplication = new MyApplication();
                }
            }
        }
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
