package com.example.scout.listener;

import java.net.DatagramPacket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public interface UdpServerListener {

    void onStart(String myIp, int myPort);

    void onReceive(DatagramPacket receivePacket);

    void onFail(Exception e);
}
