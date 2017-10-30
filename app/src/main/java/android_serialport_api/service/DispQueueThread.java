package android_serialport_api.service;

import android.app.Activity;

import java.util.LinkedList;
import java.util.Queue;

import android_serialport_api.listener.DispListener;
import android_serialport_api.model.ComBean;

/**
 * Created by zhangyuanyuan on 2017/10/26.
 */

public class DispQueueThread extends Thread {

    private Queue<ComBean> QueueList = new LinkedList<ComBean>();

    private Activity mActivity;
    private DispListener mDispListener;

    public DispQueueThread(Activity activity) {
        mActivity = activity;
    }

    public void setDispListener(DispListener dispListener) {
        this.mDispListener = dispListener;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            final ComBean ComData;
            while ((ComData = QueueList.poll()) != null) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if(mDispListener != null) {
                            mDispListener.dispRecData(ComData);
                        }
                    }
                });
                try {
                    Thread.sleep(100);//显示性能高的话，可以把此数值调小。
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public synchronized void AddQueue(ComBean ComData){
        QueueList.add(ComData);
    }


}
