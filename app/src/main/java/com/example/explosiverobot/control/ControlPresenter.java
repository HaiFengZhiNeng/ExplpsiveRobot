package com.example.explosiverobot.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.example.explosiverobot.ExplpsiveApplication;
import com.example.explosiverobot.base.BasePresenter;
import com.example.explosiverobot.modle.InterfaceBean;
import com.example.explosiverobot.udp.UdpControl;
import com.example.explosiverobot.udp.UdpReceiver;
import com.example.explosiverobot.udp.net.NetClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dell on 2017/10/16.
 */

public class ControlPresenter extends IControPresenter {


    private UdpControl udpControl;
    private ExplpsiveApplication application;

    private NetClient client;

    private Thread mUDPReceiveRunnable;

    private boolean isCanSend = false;
    //设置重复监听所需的状态：true：可交互；false：不可交互
    private boolean isSendMsg = false;

    private int textSendCount = 0;
    byte[] sendMsg = null;

    private Handler mHandler;

    private boolean isInterface = false;
    byte[] interfaceBytes = null;

    byte[] getDataBytes = null;
    private boolean isGetData = false;

    byte[] controlBytes = new byte[7];

    private long delayedTime = 200;

    private StringBuffer stringBuffer = new StringBuffer();


    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }


}
