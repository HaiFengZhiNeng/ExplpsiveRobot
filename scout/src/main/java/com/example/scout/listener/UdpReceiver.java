package com.example.scout.listener;


import com.example.scout.common.Constants;
import com.example.scout.udp.SocketManager;
import com.seabreeze.log.Print;

import static android.content.ContentValues.TAG;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UdpReceiver extends UdpRegisterRequestListener {

    private OnListenerUDPServer onListenerUDPServer;

    public UdpReceiver(OnListenerUDPServer onListenerUDPServer) {
        this.onListenerUDPServer = onListenerUDPServer;
    }


    @Override
    public void onFail(Exception e) {
        super.onFail(e);
        e.printStackTrace();
    }

    @Override
    public void onReceive(String ip, int port, String result) {
        super.onReceive(ip, port, result);
        if (!SocketManager.getInstance().isGetTcpIp) {
                Print.e(TAG, "通过UDP获取到的ip--->" + ip + "   port-->" + port);
            if(Constants.CONNECT_IP == null){
                Constants.CONNECT_IP = ip;
            }
            if(Constants.CONNECT_PORT == 0){
                Constants.CONNECT_PORT = port;
            }
                SocketManager.getInstance().setUdpIp();
                if (onListenerUDPServer != null)
                    onListenerUDPServer.acquireIp(true);
        } else {
            if (onListenerUDPServer != null)
                onListenerUDPServer.receiver(result);
        }
    }

    @Override
    public void onStart(String myIp, int myPort) {
        super.onStart(myIp, myPort);
        if (onListenerUDPServer != null)
            onListenerUDPServer.onStart(myIp, myPort);
    }
}
