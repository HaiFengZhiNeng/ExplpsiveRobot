package com.example.explosiverobot.udp.net;

import java.net.Socket;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public class UdpRegisterRequestListener {

    protected void onSucess(Socket mClient){};
    protected void onFail(Exception e){};
    protected void onResult(String result){};
    protected void onReceive(String ip, int port, String result){};

}
