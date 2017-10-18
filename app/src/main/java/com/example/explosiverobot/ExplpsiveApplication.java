package com.example.explosiverobot;

import android.app.Application;

import com.example.explosiverobot.udp.net.NetClient;
import com.example.explosiverobot.udp.net.NetMessage;
import com.example.explosiverobot.udp.net.SendRequestListener;

/**
 * Created by dell on 2017/10/16.
 */

public class ExplpsiveApplication extends Application implements SendRequestListener {

    private static ExplpsiveApplication instance;

    public static ExplpsiveApplication getInstance() {
        return instance;
    }

    private NetClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        setNetClient();
    }

    public NetClient getNetClient() {
        return client;
    }

    public void setNetClient() {
        client = NetClient.getInstance(this);
        client.setSendRequestListener(this);
    }

    @Override
    public void onSending(NetMessage message, long total, long current) {

    }

    @Override
    public void onSuccess(NetMessage message, String result) {

    }

    @Override
    public void onFail(NetMessage message, int errorCode, String errorMessage) {

    }

}
