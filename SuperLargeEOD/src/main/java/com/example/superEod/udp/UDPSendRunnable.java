package com.example.superEod.udp;

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
                if(mMsg.length() > 3) {
                    sendBuf = HexToByteArr(mMsg);
                }else{
                    sendBuf = mMsg.getBytes();
                }
            }
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, mAddress, mPort);
            mServer.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace();
            mServer.close();
        }
    }

    private byte[] HexToByteArr(String inHex) {//hex字符串转字节数组
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    private int isOdd(int num) {
        return num & 0x1;
    }

    private byte HexToByte(String inHex) {//Hex字符串转byte
        return (byte) Integer.parseInt(inHex, 16);
    }
}
