package android_serialport_api.presenter.ipresenter;

import android.content.Context;

import android_serialport_api.serial.SerialHelper;

/**
 * Created by zhangyuanyuan on 2017/9/25.
 */

public abstract class ISerialPresenter {

    private ISerialView mBaseView;

    public ISerialPresenter(ISerialView baseView) {
        mBaseView = baseView;
    }

    public abstract boolean isHasDevices();

    public abstract void openComPort(SerialHelper ComPort);

    public abstract void closeComPort();

    public abstract void sendPortData(String sOut);

    public abstract void receiveMotion(String motion);

    public interface ISerialView {

        Context getContext();
    }
}
