package com.example.explosiverobot;

import android.app.Application;

import com.example.explosiverobot.db.base.BaseManager;
import com.example.explosiverobot.util.CrashHandler;
import com.seabreeze.log.Print;
import com.seabreeze.log.inner.ConsoleTree;
import com.seabreeze.log.inner.FileTree;
import com.seabreeze.log.inner.LogcatTree;

/**
 * Created by dell on 2017/10/16.
 */

public class ExplpsiveApplication extends Application {

    private static ExplpsiveApplication instance;

    public static ExplpsiveApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        BaseManager.initOpenHelper(this);

        if(BuildConfig.DEBUG){
            Print.getLogConfig().configAllowLog(true)
                    .configShowBorders(false);
            Print.plant(new FileTree(this, "Log"));
            Print.plant(new ConsoleTree());
            Print.plant(new LogcatTree());
        }

    }


}
