package com.example.scout.socket;

import android.app.Activity;
import android.content.Intent;

import com.example.scout.ScoutApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyuanyuan on 2017/11/1.
 */

public class TcpSocketManager implements TCPTextSendListener {

    public final static int SEND_PORT = 2001;
    public final static String SEND_ADDRESS = "192.168.11.123";

    private Activity mActivity;

    private static TcpSocketManager mInstance;

    private ThreadPoolExecutor executorService;

    private static TcpClient tcpClient = null;

    private TcpCallback mTcpCallback;

    public static TcpSocketManager getInstance() {
        if (mInstance == null) {
            synchronized (TcpSocketManager.class) {
                if (mInstance == null)
                    mInstance = new TcpSocketManager();
            }
        }
        return mInstance;
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }


    public void startTcp(Activity activity) {
        mActivity = activity;
        tcpClient = new TcpClient(SEND_ADDRESS, SEND_PORT, this);
        getExecutorService().execute(tcpClient);
    }


    public void endTcp() {
        tcpClient.closeSelf();
    }

    public void sendTextMessageByTcp(final String msg, TcpCallback tcpCallback) {
        mTcpCallback = tcpCallback;
        getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                tcpClient.send(msg);
            }
        });
    }


    @Override
    public void onReadFail(final Exception e) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTcpCallback != null) {
                    mTcpCallback.onFail(e.getMessage());
                }
                mTcpCallback = null;
            }
        });

    }

    @Override
    public void onReadSuccess(final String read) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTcpCallback != null) {
                    if (read.equals("OK")) {
                        mTcpCallback.onSuccess(read);
                    } else {
                        mTcpCallback.onFail("error");
                    }
                    mTcpCallback = null;
                } else {
                    Intent intent = new Intent();
                    intent.setAction("tcpRead");
                    intent.putExtra("read", read);
                    ScoutApp.getInstance().sendBroadcast(intent);//将消息发送给主界面
                }
            }
        });

    }

    @Override
    public void onOtherFail(Exception e) {
        if (mTcpCallback != null) {
            mTcpCallback.onError(e);
        }
    }
}
