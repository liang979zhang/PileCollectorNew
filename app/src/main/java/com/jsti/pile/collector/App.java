package com.jsti.pile.collector;

import android.app.Application;

import com.jsti.pile.collector.utils.SPUtils;
import com.lzy.okgo.OkGo;

public class App extends Application {


    public static SPUtils SP_Data;
    public static SPUtils SP_System;
    @Override
    public void onCreate() {
        super.onCreate();
        SP_Data = new SPUtils(this, "data");
        SP_System = new SPUtils(this, "system_sp");
        OkGo.getInstance().init(this);
    }
}
