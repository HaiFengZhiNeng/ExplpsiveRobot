package com.example.explosiverobot.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.explosiverobot.ExplpsiveApplication;
import com.example.explosiverobot.activity.MainActivity;
import com.example.explosiverobot.config.AppConstants;
import com.example.explosiverobot.udp.UdpControl;
import com.example.explosiverobot.udp.UdpReceiver;
import com.example.explosiverobot.udp.net.NetClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public class UdpService extends Service {

    private UdpControl udpControl;
    private NetClient client;

    private UdpSendReceiver mUdpSendReceiver;

    byte[] controlBytes = new byte[7];

    private Thread mUDPReceiveRunnable;
    private boolean isCanSend = false;
    private long delayedTime = 200;

    private LocalBroadcastManager mLbmManager;

    int counter = 0;

    private StringBuffer stringBuffer = new StringBuffer();

    private byte[] interfaceBytes = null;
    private boolean isInterface = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showToast("获取到ip");
                    break;
                case 4:
                    parseJsonConter(stringBuffer.toString());
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLbmManager = LocalBroadcastManager.getInstance(this);

        udpControl = UdpControl.getInstance();
        client = NetClient.getInstance(this);

        //获取UDP的ip
        udpControl.sendUdpSocketToByIp(ExplpsiveApplication.getInstance(), client);

        startThread();

        registerUdp();

        mUdpSendReceiver = new UdpSendReceiver();
        IntentFilter filter = new IntentFilter(AppConstants.UDP_SEND_ACTION);
        mLbmManager.registerReceiver(mUdpSendReceiver, filter);
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


    private void startThread() {
        mUDPReceiveRunnable = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCanSend) {
                    try {
                        byte[] bytes = null;

                        if (isInterface) {
                            isInterface = false;
                            bytes = interfaceBytes;
                        }

                        if (bytes != null) {
                            udpControl.sendUdpByteMessage(bytes, client);
                        }
                        controlBytes[3] &= (byte) 0x07;//清空语音DIY的数据
                        Thread.sleep(delayedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void registerUdp() {
        client.registerUdpServer(new UdpReceiver(new OnListenerUDPServer() {
            @Override
            public void receiver(String receiver) {
                Log.e("registerUdpServer", "接收到UDP返回的数据--->" + receiver);
                if (receiver.contains("{")) {
                    mHandler.removeMessages(4);
                    mHandler.sendEmptyMessageDelayed(4, 300);
                }
            }

            @Override
            public void acquireIp(boolean isAcquire) {
                mHandler.sendEmptyMessage(0);
                if (isAcquire) {
                    isCanSend = true;
                    mUDPReceiveRunnable.start();
                }

            }
        }));
    }

    public void setInterfaceBytes(byte[] interfaceBytes) {
        isInterface = true;
        this.interfaceBytes = interfaceBytes;
    }

    private class UdpSendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showToast("activity发过来的数据");
            byte[] interfaceBytes = intent.getByteArrayExtra("bytes");
            if(interfaceBytes != null){
                setInterfaceBytes(interfaceBytes);
            }
        }
    }

    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示toast
     *
     * @param resStr
     * @return Toast对象，便于控制toast的显示与关闭
     */
    public void showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(UdpService.this, resStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    /**
     * 解析界面控制数据
     *
     * @param result json
     */
    private void parseJsonConter(String result) {
        if (TextUtils.isEmpty(result))
            return;

        counter = 0;
        int count = countStr(result, "}");

        if (count > 0) {
            if (count == 1) {
                pasreJson(result);
            } else {
                String[] arr = result.split("\\u007B");// { 的转义
                int size = arr.length;
                for (int i = 1; i < size; i++) {
                    pasreJson("{" + arr[i]);
                }
            }
        }
    }

    /**
     * 判断str1中包含str2的个数
     *
     * @param str1
     * @param str2
     * @return counter
     */
    private int countStr(String str1, String str2) {
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else if (str1.indexOf(str2) != -1) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) +
                    str2.length()), str2);
            return counter;
        }
        return 0;
    }

    /**
     * 解析json
     *
     * @param result
     */
    private void pasreJson(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String content = obj.optString("key_word");
            int id = obj.optInt("id");
            int count = obj.optInt("count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendLocal(String content){
        Intent intent = new Intent(AppConstants.UDP_ACCEPT_ACTION);
        intent.putExtra("content", content);
        mLbmManager.sendBroadcast(intent);
    }



    public interface OnListenerUDPServer {
        void receiver(String receiver);

        void acquireIp(boolean isAcquire);
    }
}
