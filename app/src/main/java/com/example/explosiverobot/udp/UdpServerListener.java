package com.example.explosiverobot.udp;

import java.net.DatagramPacket;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public interface UdpServerListener {

    void onReceive(DatagramPacket receivePacket);

    void onFail(Exception e);

}
