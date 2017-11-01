package com.example.explosiverobot.udp;

import com.seabreeze.log.Print;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class SocketManager {

    public final static int SEND_PORT = 8808;
    public final static int RECEIVE_PORT = 8008;

    private String mAddress;

    private static SocketManager mInstance;

    public boolean isGetTcpIp = false;

    private ThreadPoolExecutor executorService;
    private DatagramSocket mDatagramSocketSend;
    private DatagramSocket mDatagramSocketReceive;


    public static SocketManager getInstance() {
        if (mInstance == null) {
            synchronized (SocketManager.class) {
                if (mInstance == null)
                    mInstance = new SocketManager();
            }
        }
        return mInstance;
    }

    private SocketManager() {
    }

    public void setUdpIp(String address) {
        isGetTcpIp = true;
        mAddress = address;
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }

    public void registerUdpReceive(UdpServerListener udpServerListener) {
        if (mDatagramSocketReceive == null) {
            try {
                mDatagramSocketReceive = new DatagramSocket(RECEIVE_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        if (mDatagramSocketReceive == null)
            throw new RuntimeException("DatagramSocket is null");

        getExecutorService().execute(new UDPReceiveRunnable(mDatagramSocketReceive, udpServerListener));

    }


    public void sendTextMessageByUdp(String msg) {
        if (mDatagramSocketSend == null) {
            try {
                mDatagramSocketSend = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        if (mDatagramSocketSend == null) {
            throw new RuntimeException("DatagramSocket is null");
        }
        try {

            InetAddress host = InetAddress.getByName(isGetTcpIp ? mAddress : "255.255.255.255");
            int port = isGetTcpIp ? RECEIVE_PORT: SEND_PORT;


            Print.e("发送数据 IP ： " + host.toString() + " port : " + port);
            getExecutorService().execute(new UDPSendRunnable(mDatagramSocketSend, host, port, msg));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
