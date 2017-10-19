package com.example.explosiverobot.udp;

import android.util.Log;

import com.example.explosiverobot.activity.MainActivity;
import com.example.explosiverobot.service.UdpService;
import com.example.explosiverobot.udp.net.UdpRegisterRequestListener;

import static android.content.ContentValues.TAG;

/**
 * UDP控制类
 * Created by 管罗苍 on 2017/10/16.
 */

public class UdpReceiver extends UdpRegisterRequestListener {

    UdpService.OnListenerUDPServer onListenerUDPServer;

    public UdpReceiver(UdpService.OnListenerUDPServer onListenerUDPServer) {
        this.onListenerUDPServer = onListenerUDPServer;
    }

    @Override
    protected void onFail(Exception e) {
        super.onFail(e);
        e.printStackTrace();
    }

    @Override
    protected void onReceive(String ip, int port, String result) {
        super.onReceive(ip, port, result);
        //host:192.168.0.158,port:8891
        if (!UdpControl.getInstance().isGetTcpIp) {
            if (result.contains(",")) {
                String[] split = result.split(",");
                String mUdpIP = split[0].substring(5, split[0].length());
                int mUdpPort = Integer.parseInt(split[1].substring(5, split[1].length()));
                Log.e(TAG, "通过UDP获取到的ip--->" + mUdpIP + "   port-->" + mUdpPort);
                UdpControl.getInstance().setUdpIp(mUdpIP, mUdpPort);
//                isConnectLastIp = false;
                if (onListenerUDPServer != null)
                    onListenerUDPServer.acquireIp(true);
            }
        } else {
            if (onListenerUDPServer != null)
                onListenerUDPServer.receiver(result);
        }
    }

}
