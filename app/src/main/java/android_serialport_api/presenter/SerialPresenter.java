package android_serialport_api.presenter;

import android.app.Activity;

import com.seabreeze.log.Print;

import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPortFinder;
import android_serialport_api.listener.DispListener;
import android_serialport_api.model.ComBean;
import android_serialport_api.presenter.ipresenter.ISerialPresenter;
import android_serialport_api.serial.SerialControl;
import android_serialport_api.serial.SerialHelper;
import android_serialport_api.service.DispQueueThread;

/**
 * Created by zhangyuanyuan on 2017/9/25.
 */

public class SerialPresenter extends ISerialPresenter implements DispListener {

    private ISerialView mSerialView;

    private SerialControl comA;

    public String devName = "ttyUSB1";

    private DispQueueThread dispQueue;


    public SerialPresenter(ISerialView baseView) {
        super(baseView);
        this.mSerialView = baseView;

        comA = new SerialControl(null);
        comA.setPort("/dev/" + devName);
        comA.setBaudRate("9600");
        openComPort(comA);

        dispQueue = new DispQueueThread((Activity) mSerialView.getContext());
        dispQueue.setDispListener(this);

    }

    @Override
    public boolean isHasDevices() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] devices = serialPortFinder.getAllDevices();
        if (devices != null && devices.length > 0) {
            for (int i = 0; i < devices.length; i++) {
                if (devName.equals(devices[i])) {
                    return true;
                }
            }
        }
        Print.i("此机器没有 Devices");
        return false;
    }

    @Override
    public void openComPort(SerialHelper comPort) {
        if (!isHasDevices()) {
            return;
        }
        try {
            comPort.open();
        } catch (SecurityException e) {
            Print.i("打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            Print.i("打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            Print.i("打开串口失败:参数错误!");
        }
    }

    @Override
    public void closeComPort() {
        if (!isHasDevices()) {
            return;
        }
        if (comA != null) {
            comA.stopSend();
            comA.close();
        }

    }


    @Override
    public void sendPortData(String sOut) {

        if (!isHasDevices()) {
            return;
        }
        if (comA != null && comA.isOpen()) {

            comA.sendHex(sOut);

        }
    }

    @Override
    public void receiveMotion(String motion) {
        sendPortData(motion);
    }

    @Override
    public void dispRecData(ComBean ComRecData) {
    }
}
