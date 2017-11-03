package com.example.explosiverobot.udp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.seabreeze.log.Print;

import java.net.DatagramPacket;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class NetClient {

    private Context mContext;

    private SocketManager mSocketManager;

    private Handler mHandler = null;

    private static NetClient client;

    public static final String GET_IP = "FF03030000000000";

    public static NetClient getInstance(Context mContext) {
        if (client == null) {
            synchronized (NetClient.class) {
                if (client == null)
                    client = new NetClient(mContext.getApplicationContext());
            }
        }
        return client;
    }

    private NetClient(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 接受
     */
    public void registerUdpServer(final UdpRegisterRequestListener listener) {
        if (mSocketManager == null)
            mSocketManager = SocketManager.getInstance();

        mSocketManager.registerUdpReceive(new UdpServerListener() {
            @Override
            public void onReceive(DatagramPacket recvPacket) {
                String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                if (listener != null)
                    listener.onReceive(recvPacket.getAddress().getHostAddress(), recvPacket.getPort(), recvStr);
            }

            @Override
            public void onFail(Exception e) {
                if (listener != null)
                    listener.onFail(e);
            }
        });

    }

    /**
     * 获取ip
     */
    public void sendUdpSocketToByIp() {

        if (mHandler == null)
            initHandler();
        if (!SocketManager.getInstance().isGetTcpIp) {
            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
        if (mSocketManager == null) {
            mSocketManager = SocketManager.getInstance();
        }

        mHandler.sendEmptyMessageDelayed(1, 1000);
        mSocketManager.sendTextMessageByUdp(GET_IP);
    }


    public void sendUdpTextMessage(String msg) {
        if (mHandler == null)
            initHandler();

        if (!mSocketManager.isGetTcpIp) {
            mHandler.sendEmptyMessage(2);
            return;
        }
        mSocketManager.sendTextMessageByUdp(msg);
    }

    private void initHandler() {
        mHandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if (!SocketManager.getInstance().isGetTcpIp) {
                            if (mSocketManager == null) {
                                mSocketManager = SocketManager.getInstance();
                            }

                            mHandler.sendEmptyMessageDelayed(1, 1000);
                            mSocketManager.sendTextMessageByUdp(GET_IP);
                        }
                        break;
                    case 2:
                        Print.e("请等待连接机器人");
                        break;
                }
            }
        };
    }

}
