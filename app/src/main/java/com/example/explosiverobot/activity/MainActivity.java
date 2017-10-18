package com.example.explosiverobot.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;

import com.example.explosiverobot.ExplpsiveApplication;
import com.example.explosiverobot.R;
import com.example.explosiverobot.actionOrder.ActionCommonFragment;
import com.example.explosiverobot.adapter.ActionViewPagerAdapter;
import com.example.explosiverobot.control.InterfaceBean;
import com.example.explosiverobot.modle.ActionTab;
import com.example.explosiverobot.udp.UdpControl;
import com.example.explosiverobot.udp.UdpReceiver;
import com.example.explosiverobot.udp.net.NetClient;
import com.example.explosiverobot.weidget.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    public String TAG = this.getClass().getSimpleName();

    @BindView(R.id.pager_sliding_tabstrip)
    PagerSlidingTabStrip pagerSlidingTabstrip;
    @BindView(R.id.action_viewPager)
    ViewPager actionViewPager;

    private UdpControl udpControl;
    private NetClient client;
    byte[] controlBytes = new byte[7];

    //头部Tab
    private List<ActionTab> mActionTabsList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ActionViewPagerAdapter mActionViewPagerAdapter;

    private Thread mUDPReceiveRunnable;
    private boolean isCanSend = false;
    byte[] sendMsg = null;
    //设置重复监听所需的状态：true：可交互；false：不可交互
    private boolean isSendMsg = false;

    byte[] interfaceBytes = null;
    private boolean isInterface = false;

    byte[] getDataBytes = null;
    private boolean isGetData = false;

    private int textSendCount = 0;

    private long delayedTime = 200;

    private StringBuffer stringBuffer = new StringBuffer();

    int counter = 0;

    private ArrayList<InterfaceBean> been = null;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        udpControl = UdpControl.getInstance();
        client = ExplpsiveApplication.getInstance().getNetClient();

        initHandler();
        mHandler = getHandler();

        initTopTab();

        controlBytes[0] = (byte) 0xAA;
        controlBytes[1] = (byte) 0x01;
//        controlBytes[3] &= (byte) ~(1);
        controlBytes[3] &= (byte) 0xfe;
        controlBytes[6] = (byte) 0xBB;

        //获取UDP的ip
        udpControl.sendUdpSocketToByIp(ExplpsiveApplication.getInstance(), client);

        mUDPReceiveRunnable = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCanSend) {
                    try {
                        Log.i("control", "send control bytes ");
                        byte[] bytes = null;
                        if (isSendMsg) {
                            textSendCount++;
                            isSendMsg = false;
                            bytes = sendMsg;
                            if (textSendCount <= 5)
                                mHandler.sendEmptyMessageDelayed(3, 100);
                        } else if (isInterface) {
                            isInterface = false;
                            bytes = interfaceBytes;
                        } else if (isGetData) {
                            isGetData = false;
                            bytes = getDataBytes;
                        } else {
                            controlBytes[5] = (byte) (controlBytes[1] ^ controlBytes[2] ^ controlBytes[3] ^ controlBytes[4]);
                            bytes = controlBytes;
                        }
                        if (bytes != null)
                            udpControl.sendUdpByteMessage(bytes, ExplpsiveApplication.getInstance(), client);

                        controlBytes[3] &= (byte) 0x07;//清空语音DIY的数据
                        Thread.sleep(delayedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        client.registerUdpServer(new UdpReceiver(new OnListenerUDPServer() {
            @Override
            public void receiver(String receiver) {
                Log.e(TAG, "接收到UDP返回的数据--->" + receiver);
                if (receiver.contains("{")) {
                    mHandler.removeMessages(4);
                    stringBuffer.append(receiver);
                    mHandler.sendEmptyMessageDelayed(4, 300);
                }
            }

            @Override
            public void acquireIp(boolean isAcquire) {
                //已获取到ip，开始循环发送指令
                mHandler.sendEmptyMessage(0);
                Log.e(TAG, "已获取到ip，开始循环发送指令--->" + isAcquire);
                if (isAcquire) {
                    isCanSend = true;
                    mUDPReceiveRunnable.start();
                }

            }
        }));



    }


    /**
     * 顶部Tab
     */
    public void initTopTab() {
        addActionTab();
        addTitle();
        addFragmentList();

        setPageTitle();
    }


    /**
     * 添加数据
     */
    public void addActionTab() {
        mActionTabsList.add(new ActionTab("0", "全部"));
        mActionTabsList.add(new ActionTab("1", "前进"));
        mActionTabsList.add(new ActionTab("2", "后退"));
        mActionTabsList.add(new ActionTab("3", "左移"));
        mActionTabsList.add(new ActionTab("4", "右移"));
    }

    private void addTitle() {
        for (int i = 0; i < mActionTabsList.size(); i++) {
            mTitleList.add(mActionTabsList.get(i).getName());
        }
    }

    private void addFragmentList() {
//        if (mFragmentList != null && mFragmentList.size() > 0) {
//            return;
//        }
        for (int i = 0; i < mTitleList.size(); i++) {
            ActionCommonFragment actionCommonFragment_new = new ActionCommonFragment();
            Bundle bundle = new Bundle();
            bundle.putString("theme_id", mActionTabsList.get(i).getId());
            actionCommonFragment_new.setArguments(bundle);
            mFragmentList.add(actionCommonFragment_new);
        }
    }

    /*
     *设置Title样式
     */
    private void setPageTitle() {
//        viewPager 记载adapter
        mActionViewPagerAdapter = new ActionViewPagerAdapter(getSupportFragmentManager(), mTitleList, mFragmentList);
        actionViewPager.setAdapter(mActionViewPagerAdapter);
        actionViewPager.setCurrentItem(0, true);
//        live_viewPager.setOffscreenPageLimit(10);//保存viewpage数据不被回收
        pagerSlidingTabstrip.setViewPager(actionViewPager);
        pagerSlidingTabstrip.setIndicatorHeight(5);//设置下划线宽度
        pagerSlidingTabstrip.setUnderlineHeight(2);//设置底部边框宽度
        pagerSlidingTabstrip.setDividerColor(getResources().getColor(R.color.color_tab_unselect)); //中间竖线颜色
        pagerSlidingTabstrip.setIndicatorColor(getResources().getColor(R.color.color_white));// 下划线颜色
        pagerSlidingTabstrip.setSelectedTextColor(getResources().getColor(R.color.color_white));//文字选中颜色
        pagerSlidingTabstrip.setTextColor(getResources().getColor(R.color.color_tab_unselect));//没有选中时文字颜色
        pagerSlidingTabstrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_20));//字体大小
        pagerSlidingTabstrip.setShouldExpand(true);
        pagerSlidingTabstrip.setTabBackground(R.drawable.background_tab);

    }

    /**
     * 重新连接机器
     */
    void reLink() {
        if (UdpControl.getInstance().isGetTcpIp) {
            isCanSend = false;
            //重置数据
//            resetData();
            udpControl.sendUdpSocketToByIp(ExplpsiveApplication.getInstance(), client);
        } else {
            showToast("当前未连接机器人");
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
//                mView.setTextView("已连接" + UdpControl.getInstance().mUdpIP);
//                mView.setLinkVisiable(true);
                showToast("获取到ip");
                break;
            case 3:
                isSendMsg = true;
                break;
            case 4:
                parseJsonConter(stringBuffer.toString());
                break;
        }
    }

    /**
     * 解析界面控制数据
     *
     * @param result json
     */
    private void parseJsonConter(String result) {
        if (TextUtils.isEmpty(result))
            return;

        counter = 0;
        int count = countStr(result, "}");

        if (count > 0) {
            if (count == 1) {
                pasreJson(result);
            } else {
                String[] arr = result.split("\\u007B");// { 的转义
                int size = arr.length;
                for (int i = 1; i < size; i++) {
                    Log.e("json", "result->{" + arr[i]);
                    pasreJson("{" + arr[i]);
                }
            }
        }
    }

    /**
     * 判断str1中包含str2的个数
     *
     * @param str1
     * @param str2
     * @return counter
     */
    private int countStr(String str1, String str2) {
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else if (str1.indexOf(str2) != -1) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) +
                    str2.length()), str2);
            return counter;
        }
        return 0;
    }

    /**
     * 解析json
     *
     * @param result
     */
    private void pasreJson(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String content = obj.optString("key_word");
            int id = obj.optInt("id");
            int count = obj.optInt("count");
            if (been != null) {
                been.add(new InterfaceBean(content, id));
                Log.e("json", "been--" + been.size() + "    count--" + count);
                if (count == been.size()) {

//                    DataBaseDao dao = new DataBaseDao(getContext());
//                    dao.clear();
//                    dao.insert(been);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public interface OnListenerUDPServer {
        void receiver(String receiver);

        void acquireIp(boolean isAcquire);
    }

}
