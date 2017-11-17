package com.example.explosiverobot.listener;

import java.net.Socket;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class UdpRegisterRequestListener {

    public void onSucess(Socket mClient){};
    public void onFail(Exception e){};
    public void onResult(String result){};
    public void onReceive(String ip, int port, String result){};

}
