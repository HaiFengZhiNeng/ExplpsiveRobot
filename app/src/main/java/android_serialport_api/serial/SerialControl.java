package android_serialport_api.serial;

import android_serialport_api.model.ComBean;
import android_serialport_api.service.DispQueueThread;

/**
 * Created by zhangyuanyuan on 2017/9/25.
 */

public class SerialControl extends SerialHelper {

    private DispQueueThread mDispQueueThread;

    public SerialControl(DispQueueThread dispQueueThread) {
        mDispQueueThread = dispQueueThread;
    }

    @Override
    protected void onDataReceived(ComBean ComRecData) {
        mDispQueueThread.AddQueue(ComRecData);
    }
}
