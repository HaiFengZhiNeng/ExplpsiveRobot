package com.example.explosiverobot.udp;

import com.example.explosiverobot.listener.UdpServerListener;
import com.seabreeze.log.Print;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class UDPReceiveRunnable implements Runnable {

    private DatagramSocket mServer;
    private UdpServerListener mUdpServerListener;

    private boolean udpLife = true;     //udp生命线程

    private DatagramPacket dpRcv;
    private byte[] msgRcv = new byte[1024];

    public UDPReceiveRunnable(DatagramSocket datagramSocket, UdpServerListener udpServerListener) {
        this.mServer = datagramSocket;
        this.mUdpServerListener = udpServerListener;
    }

    @Override
    public void run() {
        try {

            dpRcv = new DatagramPacket(msgRcv, msgRcv.length);

            while (udpLife) {
                mServer.receive(dpRcv);

                if (mUdpServerListener != null) {
                    mUdpServerListener.onReceive(dpRcv);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (mUdpServerListener != null) {
                mUdpServerListener.onFail(e);
            }
            mServer.close();
        }
        Print.e("Thread.interrupted");
    }
}
