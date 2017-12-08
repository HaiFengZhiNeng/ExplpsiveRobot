package com.example.superEod;

import android.app.Application;

import com.example.superEod.util.CrashHandler;
import com.seabreeze.log.Print;
import com.seabreeze.log.inner.ConsoleTree;
import com.seabreeze.log.inner.FileTree;
import com.seabreeze.log.inner.LogcatTree;

/**
 * Created by zhangyuanyuan on 2017/11/17.
 */

public class SuperApp extends Application {

    private static SuperApp instance;

    public static SuperApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

        if(BuildConfig.DEBUG){
            Print.getLogConfig().configAllowLog(true)
                    .configShowBorders(false);
            Print.plant(new FileTree(this, "Log"));
            Print.plant(new ConsoleTree());
            Print.plant(new LogcatTree());
        }

    }

}
