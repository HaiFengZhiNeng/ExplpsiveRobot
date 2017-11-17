package com.example.scout.common;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static int displayWidth;
    public static int displayHeight;

    //exit
    public static final String NET_LOONGGG_EXITAPP = "net.loonggg.exitapp";


    private static String mSdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String projectPath = mSdRootPath + File.separator + "fangfangBig" + File.separator;

    public static class API {

        public static final String CONTROL_CMD_SET_WIFI_PWD = "http://192.168.11.123/api/setpasswd";
        public static final String CONTROL_CMD_SET_WIFI_SSID = "http://192.168.11.123/api/setssid";
        public static final String CONTROL_CMD_VERSION = "http://192.168.11.123/api/version";

        public static final String CONTROL_CMD_INSTRUCTIONS = "http://192.168.11.123:2001/";
    }

}
