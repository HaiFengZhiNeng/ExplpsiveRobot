package com.example.scout.common;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static int displayWidth;
    public static int displayHeight;

    //exit
    public static final String EXIT_APP = "EXIT_APP";
    public static final String NET_LOONGGG_EXITAPP = "net.loonggg.exitapp";

    //udp service
    public static final String UDP_ACCEPT_ACTION = "android.receiver.udpAcceptReceiver";
    public static final String UDP_SEND_ACTION = "android.receiver.udpSendReceiver";

    //udp
    public static String IP;
    public static int PORT;
    public static String CONNECT_IP = null;
    public static int CONNECT_PORT = 0;

    private static String mSdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String projectPath = mSdRootPath + File.separator + "fangfangBig" + File.separator;

    public static class API {


    }

}
