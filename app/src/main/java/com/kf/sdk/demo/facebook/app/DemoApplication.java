package com.kf.sdk.demo.facebook.app;

import android.app.Application;

import com.facebook.FacebookSdk;

import mdl.sinlov.android.log.ALog;

/**
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by sinlov on 16/8/17.
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ALog.initTag();
        FacebookSdk.sdkInitialize(this);
        ALog.i("FacebookSdk init");
    }
}
