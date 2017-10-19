package com.example.explosiverobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zhangyuanyuan on 2017/10/19.
 */

public class UDPAcceptReceiver extends BroadcastReceiver {

    private static final String TAG = "UDPAcceptReceiver";

    private UDPAcceptInterface mUdpAcceptInterface;

    public UDPAcceptReceiver(UDPAcceptInterface udpAcceptInterface) {
        this.mUdpAcceptInterface = udpAcceptInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");
        if (content != null) {
            Log.e(TAG, "服务发过来的数据 :" + content);
            if(content != null){
                mUdpAcceptInterface.UDPAcceptMessage(content);
            }
        }
    }


    public interface UDPAcceptInterface {
        void UDPAcceptMessage(String content);
    }

}
