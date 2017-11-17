package com.linkcard.media;

import com.seabreeze.log.Print;

/**
 * Created by zhangyuanyuan on 2017/11/13.
 */

public class LinkVideoCore {

    public static final int E_SYS_BADFRAME = -4;
    public static final int E_SYS_ERRBITMAP = -7;
    public static final int E_SYS_NOERR = 0;
    public static final int E_SYS_NOFRAME = -5;
    public static final int E_SYS_NOFRMDATA = -3;
    public static final int E_SYS_NOTINIT = -2;
    public static final int E_SYS_UNKOWNERR = -6;
    public static final int SCALE_ASPECT_BALANCED = 2;
    public static final int SCALE_ASPECT_FILL = 1;
    public static final int SCALE_ASPECT_FIT = 0;
    private static final String TAG = "DBG";

    static
    {
        System.loadLibrary("linkcardplayer");
        Print.d("liblinkcardplayer.so");
    }

    public static native int nativeAspect(int paramInt);

    public static native int nativeChangeView(int paramInt1, int paramInt2);

    public static native int nativeDecodeFrame();

    public static native int nativeDraw(int paramInt);

    public static native int nativeInitRenderer();

    public native int getVideoHeight();

    public native int getVideoWidth();

    public native int snapshot(String paramString);

    public native int startPlayback();

    public native int startRecord(String paramString);

    public native int stopPlayback();

    public native int stopRecord();

    public native int sysinit(String paramString);

    public native int sysuninit();


}
