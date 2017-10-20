package com.example.explosiverobot.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.adapter.ActionViewPagerAdapter;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.base.config.ContentCommon;
import com.example.explosiverobot.fragment.ActionCommonFragment;
import com.example.explosiverobot.modle.ActionTab;
import com.example.explosiverobot.receiver.UDPAcceptReceiver;
import com.example.explosiverobot.service.BridgeService;
import com.example.explosiverobot.service.UdpService;
import com.example.explosiverobot.util.JumpItent;
import com.example.explosiverobot.view.weiget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class MainActivity extends BaseActivity implements BridgeService.AddCameraInterface, BridgeService.CallBackMessageInterface,
        BridgeService.IpcamClientInterface, UDPAcceptReceiver.UDPAcceptInterface {

    public String TAG = this.getClass().getSimpleName();

    private static final String STR_MSG_PARAM = "msgparam";
    private static final String STR_DID = "did";

    @BindView(R.id.pager_sliding_tabstrip)
    PagerSlidingTabStrip pagerSlidingTabstrip;
    @BindView(R.id.action_viewPager)
    ViewPager actionViewPager;
    @BindView(R.id.tv_add_group)
    TextView tvAddGroup;
    @BindView(R.id.iv_robot_bg)
    ImageView ivRobotBg;

    private LocalBroadcastManager mLbmManager;
    private boolean isAccept;
    //头部Tab
    private List<ActionTab> mActionTabsList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ActionViewPagerAdapter mActionViewPagerAdapter;
    //udp
    private UDPAcceptReceiver mUdpAcceptReceiver;
    //ipc
    //连接状态
    private int tag = 0;
    private int option = ContentCommon.INVALID_OPTION;
    private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
    private Intent intentbrod = null;

    private Handler PPPPMsgHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            int msgParam = bd.getInt(STR_MSG_PARAM);
            int msgType = msg.what;
            String did = bd.getString(STR_DID);
            switch (msgType) {
                case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
                    switch (msgParam) {
                        case ContentCommon.PPPP_STATUS_CONNECTING://0
                            promptUser(getString(R.string.pppp_status_connecting));
                            tag = 2;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
                            promptUser(getString(R.string.pppp_status_connect_failed));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_DISCONNECT://4
                            promptUser(getString(R.string.pppp_status_disconnect));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_INITIALING://1
                            promptUser(getString(R.string.pppp_status_initialing));
                            tag = 2;
                            break;
                        case ContentCommon.PPPP_STATUS_INVALID_ID://5
                            promptUser(getString(R.string.pppp_status_invalid_id));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
                            //摄像机在线之后读取摄像机类型
                            String cmd = "get_status.cgi?loginuse=admin&loginpas=" + AppConstants.devicePass
                                    + "&user=admin&pwd=" + AppConstants.devicePass;
                            NativeCaller.TransferMessage(did, cmd, 1);
                            promptUser(getString(R.string.pppp_status_online));
                            tag = 1;
                            break;
                        case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
                            promptUser(getString(R.string.device_not_on_line));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
                            promptUser(getString(R.string.pppp_status_connect_timeout));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
                            promptUser(getString(R.string.pppp_status_pwd_error));
                            tag = 0;
                            break;
                        default:
                            promptUser(getString(R.string.pppp_status_unknown));
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
                            || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did);
                    }
                    break;
                case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
                    break;

            }

        }
    };

    private void promptUser(String string) {
        Log.e(TAG, string);
        showToast(string);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mLbmManager = LocalBroadcastManager.getInstance(this);
        Intent startIntent = new Intent(this, UdpService.class);
        startService(startIntent);

        initCamera();

        initTopTab();

        connectIpcamera("admin", "haifeng567", "VSTA347062EGDGD");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        isAccept = true;
        mUdpAcceptReceiver = new UDPAcceptReceiver(this);
        IntentFilter intentFilter = new IntentFilter(AppConstants.UDP_ACCEPT_ACTION);
        mLbmManager.registerReceiver(mUdpAcceptReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAccept = false;
        NativeCaller.StopSearch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
        NativeCaller.Free();
        sendBroadcast(new Intent(AppConstants.NET_LOONGGG_EXITAPP));
    }

    @OnClick({R.id.tv_add_group, R.id.iv_robot_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_group:
//                byte[] bytes = new byte[3];
//                bytes[0] = (byte) 0xAA;
//                bytes[1] = (byte) 0x03;
//                bytes[2] = (byte) 0xBB;
//                sendLocal(bytes);

//                connectIpcamera("admin", "haifeng567", "VSTA347062EGDGD");
                break;
            case R.id.iv_robot_bg:
                if(tag == 1) {
                    startTasgAvtivity();
                }else{
                    Log.e(TAG, "tag : " + tag);
                }
                break;
        }
    }

    /**
     * 顶部Tab
     */
    private void initTopTab() {
        addActionTab();
        addTitle();
        addFragmentList();

        setPageTitle();
    }

    /**
     * 初始化camera
     */
    private void initCamera() {
        BridgeService.setAddCameraInterface(this);
        BridgeService.setCallBackMessage(this);
        intentbrod = new Intent("drop");
    }

    /**
     * 连接carmera
     *
     * @param strUser
     * @param strPwd
     * @param strDID
     */
    private void connectIpcamera(String strUser, String strPwd, String strDID) {
        if (strDID.length() == 0) {
            showToast(R.string.input_camera_id);
            return;
        }

        if (strUser.length() == 0) {
            showToast(R.string.input_camera_user);
            return;
        }

        Intent in = new Intent();
        if (option == ContentCommon.INVALID_OPTION) {
            option = ContentCommon.ADD_CAMERA;
        }
        in.putExtra(ContentCommon.CAMERA_OPTION, option);
        in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
        in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
        in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
        in.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);
        AppConstants.deviceName = strUser;
        AppConstants.deviceId = strDID;
        AppConstants.devicePass = strPwd;
        BridgeService.setIpcamClientInterface(this);
        NativeCaller.Init();
        new Thread(new StartPPPPThread()).start();
    }


    /**
     * 添加数据
     */
    private void addActionTab() {
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

    //*****************AddCameraInterface*******************************************
    @Override
    public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
        Log.e(TAG, "callBackSearchResultData");
    }

    //*****************CallBackMessageInterface*******************************************
    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {
        Log.e(TAG, "CallBackGetStatus");
        if (cmd == ContentCommon.CGI_IEGET_STATUS) {
            String cameraType = spitValue(resultPbuf, "upnp_status=");
            int intType = Integer.parseInt(cameraType);
            int type14 = (int) (intType >> 16) & 1;// 14位 来判断是否报警联动摄像机
            if (intType == 2147483647) {// 特殊值
                type14 = 0;
            }

            if (type14 == 1) {
                Log.e(TAG, "CallBackGetStatus   type14 == 1");
            }
        }
    }

    private String spitValue(String name, String tag) {
        String[] strs = name.split(";");
        for (int i = 0; i < strs.length; i++) {
            String str1 = strs[i].trim();
            if (str1.startsWith("var")) {
                str1 = str1.substring(4, str1.length());
            }
            if (str1.startsWith(tag)) {
                String result = str1.substring(str1.indexOf("=") + 1);
                return result;
            }
        }
        return -1 + "";
    }

    //*****************setIpcamClientInterface*******************************************
    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.e(TAG, "BSMsgNotifyData");
        Bundle bd = new Bundle();
        Message msg = PPPPMsgHandler.obtainMessage();
        msg.what = type;
        bd.putInt(STR_MSG_PARAM, param);
        bd.putString(STR_DID, did);
        msg.setData(bd);
        PPPPMsgHandler.sendMessage(msg);
        if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            intentbrod.putExtra("ifdrop", param);
            sendBroadcast(intentbrod);
        }
    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {
        Log.e(TAG, "BSSnapshotNotify");
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {
        Log.e(TAG, "callBackUserParams");
    }

    @Override
    public void CameraStatus(String did, int status) {
        Log.e(TAG, "CameraStatus");
    }

    @Override
    public void UDPAcceptMessage(String content) {
        if(isAccept){
            showToast(content);
        }
    }


    class StartPPPPThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (AppConstants.deviceId.toLowerCase().startsWith("vsta")) {
                    NativeCaller.StartPPPPExt(AppConstants.deviceId, AppConstants.deviceName, AppConstants.devicePass, 1, "",
                            "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK");
                } else {
                    NativeCaller.StartPPPP(AppConstants.deviceId, AppConstants.deviceName, AppConstants.devicePass, 1, "");
                }

            } catch (Exception e) {

            }
        }
    }

    private void sendLocal(byte[] bytes) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("bytes", bytes);
        mLbmManager.sendBroadcast(intent);
    }

    private void startTasgAvtivity() {
        JumpItent.jump(MainActivity.this, TaskActivity.class);
    }


}
