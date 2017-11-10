package com.example.explosiverobot.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.udp.NetClient;
import com.example.explosiverobot.udp.OnListenerUDPServer;
import com.example.explosiverobot.udp.SocketManager;
import com.example.explosiverobot.udp.UdpReceiver;
import com.seabreeze.log.Print;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public class UdpService extends Service implements OnListenerUDPServer {

    private LocalBroadcastManager mLbmManager;

    private UdpSendReceiver mUdpSendReceiver;

    private Handler mHandler = new Handler();

    private NetClient client;

    private LinkedBlockingQueue<String> mSendQueue;//队列

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLbmManager = LocalBroadcastManager.getInstance(this);

        mSendQueue = new LinkedBlockingQueue<>();

        client = NetClient.getInstance(this);
//        client.sendUdpSocketToByIp();

        client.registerUdpServer(new UdpReceiver(this));
        SocketManager.getInstance().setUdpIp("192.168.1.102");
        sendLocal("udp connect");

        mUdpSendReceiver = new UdpSendReceiver();
        IntentFilter filter = new IntentFilter(AppConstants.UDP_SEND_ACTION);
        mLbmManager.registerReceiver(mUdpSendReceiver, filter);

        mHandler.post(runnable);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpSendReceiver);
    }


    private void sendLocal(String content){
        Intent intent = new Intent(AppConstants.UDP_ACCEPT_ACTION);
        intent.putExtra("content", content);
        mLbmManager.sendBroadcast(intent);
    }

    @Override
    public void receiver(String receiver) {
        sendLocal(receiver);
    }

    @Override
    public void acquireIp(boolean isAcquire) {
        Print.e(isAcquire);
//        mHandler.post(runnable);
        sendLocal("udp connect");
    }


    private class UdpSendReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String information = intent.getStringExtra("order");
            if(information != null){

//                client.sendUdpTextMessage(information);
                if(mSendQueue != null) {
                    mSendQueue.add(information);
                }
            }
        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            String order = null;
            if (!mSendQueue.isEmpty()) {
                order = mSendQueue.poll();
            }
            if(order != null){
                client.sendUdpTextMessage(order);
                Print.e("runnable : " + order);
            }
            mHandler.postDelayed(runnable, 300);
        }

    };

}
