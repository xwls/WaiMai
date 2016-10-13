package com.oaec.waimai.app;

import android.app.Application;

import com.oaec.waimai.entity.User;

import org.xutils.x;

/**
 * Created by Kevin on 2016/10/8.
 * Description：
 */
public class WaiMaiApplication extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
