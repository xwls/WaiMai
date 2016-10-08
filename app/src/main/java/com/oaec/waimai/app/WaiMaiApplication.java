package com.oaec.waimai.app;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Kevin on 2016/10/8.
 * Description：
 */
public class WaiMaiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
