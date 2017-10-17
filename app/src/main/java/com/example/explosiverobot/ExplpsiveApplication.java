package com.example.explosiverobot;

import android.app.Application;
import android.content.Context;

import com.example.explosiverobot.dbhelper.DBHelper;
import com.ocean.mvp.library.net.NetClient;
import com.ocean.mvp.library.net.NetMessage;
import com.ocean.mvp.library.net.SendRequestListener;

/**
 * Created by dell on 2017/10/16.
 */

public class ExplpsiveApplication extends Application implements SendRequestListener {

    private NetClient client;
    private DBHelper dbHelper;


    @Override
    public void onCreate() {
        super.onCreate();
        client = NetClient.getInstance(this);
        client.setSendRequestListener(this);
    }

    /**
     * 获取的app的Application对象
     *
     * @param context 上下文
     * @return Application对象
     */
    public static ExplpsiveApplication from(Context context) {
        return (ExplpsiveApplication) context.getApplicationContext();
    }


    /**
     * 获取访问网络对象
     *
     * @return 访问网络对象
     */
    public NetClient getNetClient() {
        return client;
    }

    public void setNetClient() {
        client = NetClient.getInstance(getApplicationContext());
        client.setSendRequestListener(this);
    }

    /**
     * 获取数据库操作类
     *
     * @return 数据库操作类
     */

    public synchronized DBHelper getDataBase() {
        if (dbHelper == null)
            dbHelper = new DBHelper(getApplicationContext());
        return dbHelper;
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
