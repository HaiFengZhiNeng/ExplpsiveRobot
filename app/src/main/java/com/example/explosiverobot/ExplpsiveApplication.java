package com.example.explosiverobot;

import android.app.Application;

import com.example.explosiverobot.udp.net.NetClient;

/**
 * Created by dell on 2017/10/16.
 */

public class ExplpsiveApplication extends Application {

    private static ExplpsiveApplication instance;

    public static ExplpsiveApplication getInstance() {
        return instance;
    }

    private NetClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }

}
