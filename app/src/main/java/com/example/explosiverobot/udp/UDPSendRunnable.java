package com.example.explosiverobot.udp;

import android.text.TextUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class UDPSendRunnable implements Runnable {

    private DatagramSocket mServer;

    private InetAddress mAddress;
    private int mPort;
    private String mMsg;

    public UDPSendRunnable(DatagramSocket datagramSocket, InetAddress address, int port, String msg) {
        this.mServer = datagramSocket;
        this.mAddress = address;
        this.mPort = port;
        this.mMsg = msg;
    }

    @Override
    public void run() {
        try {

            byte[] sendBuf = new byte[1024];
            if (!TextUtils.isEmpty(mMsg)) {
                sendBuf = mMsg.getBytes();
            }
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, mAddress, mPort);
            mServer.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace();
            mServer.close();
        }
    }
}
