//package com.example.explosiverobot.control;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//
//import com.example.explosiverobot.ExplpsiveApplication;
//import com.example.explosiverobot.dao.DataBaseDao;
//import com.example.explosiverobot.udp.UdpControl;
//import com.example.explosiverobot.udp.UdpReceiver;
//import com.ocean.mvp.library.net.NetClient;
//import com.ocean.mvp.library.presenter.BasePresenter;
//import com.ocean.mvp.library.utils.L;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by dell on 2017/10/16.
// */
//
//public class ControlPresenter extends BasePresenter<IControlView> {
//
//
//    public ControlPresenter(IControlView mView) {
//        super(mView);
//    }
//
//    private UdpControl udpControl;
//    private ExplpsiveApplication application;
//
//    private NetClient client;
//
//    private Thread mUDPReceiveRunnable;
//
//    private boolean isCanSend = false;
//    //设置重复监听所需的状态：true：可交互；false：不可交互
//    private boolean isSendMsg = false;
//
//    private int textSendCount = 0;
//    byte[] sendMsg = null;
//
//    private Handler mHandler;
//
//    private boolean isInterface = false;
//    byte[] interfaceBytes = null;
//
//    byte[] getDataBytes = null;
//    private boolean isGetData = false;
//
//    byte[] controlBytes = new byte[7];
//
//    private long delayedTime = 200;
//
//    private StringBuffer stringBuffer = new StringBuffer();
//
//    private ArrayList<InterfaceBean> been = null;
//
//    @Override
//    public void onCreate(Bundle saveInstanceState) {
//        super.onCreate(saveInstanceState);
//        udpControl = UdpControl.getInstance();
//        application = ExplpsiveApplication.from(getContext());
//        client = application.getNetClient();
//
//        initHandler();
//        mHandler = getHandler();
//
//        //获取UDP的ip
//        udpControl.sendUdpSocketToByIp(application, client);
//
//        mUDPReceiveRunnable = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isCanSend) {
//                    try {
//                        L.i("control", "send control bytes ");
//                        byte[] bytes = null;
//                        if (isSendMsg) {
//                            textSendCount++;
//                            isSendMsg = false;
//                            bytes = sendMsg;
//                            if (textSendCount <= 5)
//                                mHandler.sendEmptyMessageDelayed(3, 100);
//                        } else if (isInterface) {
//                            isInterface = false;
//                            bytes = interfaceBytes;
//                        } else if (isGetData) {
//                            isGetData = false;
//                            bytes = getDataBytes;
//                        } else {
//                            controlBytes[5] = (byte) (controlBytes[1] ^ controlBytes[2] ^ controlBytes[3] ^ controlBytes[4]);
//                            bytes = controlBytes;
//                        }
//                        if (bytes != null)
//                            udpControl.sendUdpByteMessage(bytes, application, client);
//
//                        controlBytes[3] &= (byte) 0x07;//清空语音DIY的数据
//                        Thread.sleep(delayedTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//
//        client.registerUdpServer(new UdpReceiver(new OnListenerUDPServer() {
//            @Override
//            public void receiver(String receiver) {
//                L.e(TAG, "接收到UDP返回的数据--->" + receiver);
//                if (receiver.contains("{")) {
//                    mHandler.removeMessages(4);
//                    stringBuffer.append(receiver);
//                    mHandler.sendEmptyMessageDelayed(4, 300);
//                }
//            }
//
//            @Override
//            public void acquireIp(boolean isAcquire) {
//                //已获取到ip，开始循环发送指令
//                mHandler.sendEmptyMessage(0);
//                L.e(TAG, "已获取到ip，开始循环发送指令--->" + isAcquire);
//                if (isAcquire) {
//                    isCanSend = true;
//                    mUDPReceiveRunnable.start();
//                }
//
//            }
//        }));
//    }
//
//    @Override
//    protected void handleMessage(Message msg) {
//        super.handleMessage(msg);
//        switch (msg.what) {
//            case 0:
////                mView.setTextView("已连接" + UdpControl.getInstance().mUdpIP);
////                mView.setLinkVisiable(true);
//                showToast("获取到ip");
//                break;
//            case 3:
//                isSendMsg = true;
//                break;
//            case 4:
//                parseJsonConter(stringBuffer.toString());
//                break;
//        }
//    }
//
//    //UDP
//    public interface OnListenerUDPServer {
//        void receiver(String receiver);
//
//        void acquireIp(boolean isAcquire);
//    }
//
//
//    /**
//     * 重新连接机器
//     */
//    void reLink() {
//        if (UdpControl.getInstance().isGetTcpIp) {
//            isCanSend = false;
//            //重置数据
////            resetData();
//            udpControl.sendUdpSocketToByIp(application, client);
//        } else {
//            showToast("当前未连接机器人");
//        }
//    }
//
//    int counter = 0;
//
//    /**
//     * 判断str1中包含str2的个数
//     *
//     * @param str1
//     * @param str2
//     * @return counter
//     */
//    private int countStr(String str1, String str2) {
//        if (str1.indexOf(str2) == -1) {
//            return 0;
//        } else if (str1.indexOf(str2) != -1) {
//            counter++;
//            countStr(str1.substring(str1.indexOf(str2) +
//                    str2.length()), str2);
//            return counter;
//        }
//        return 0;
//    }
//
//    /**
//     * 解析界面控制数据
//     *
//     * @param result json
//     */
//    private void parseJsonConter(String result) {
//        if (TextUtils.isEmpty(result))
//            return;
//
//        counter = 0;
//        int count = countStr(result, "}");
//
//        if (count > 0) {
//            if (count == 1) {
//                pasreJson(result);
//            } else {
//                String[] arr = result.split("\\u007B");// { 的转义
//                int size = arr.length;
//                for (int i = 1; i < size; i++) {
//                    L.e("json", "result->{" + arr[i]);
//                    pasreJson("{" + arr[i]);
//                }
//            }
//        }
//    }
//
//    /**
//     * 解析json
//     *
//     * @param result
//     */
//    private void pasreJson(String result) {
//        try {
//            JSONObject obj = new JSONObject(result);
//            String content = obj.optString("key_word");
//            int id = obj.optInt("id");
//            int count = obj.optInt("count");
//            if (been != null) {
//                been.add(new InterfaceBean(content, id));
//                L.e("json", "been--" + been.size() + "    count--" + count);
//                if (count == been.size()) {
//
//                    DataBaseDao dao = new DataBaseDao(getContext());
//                    dao.clear();
//                    dao.insert(been);
////                    if (interfaceDialog != null && interfaceDialog.isShowing())
////                        interfaceDialog.setData(been);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
