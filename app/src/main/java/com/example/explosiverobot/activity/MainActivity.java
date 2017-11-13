package com.example.explosiverobot.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.explosiverobot.R;
import com.example.explosiverobot.adapter.ActionViewPagerAdapter;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.db.manager.ActionTabDbManager;
import com.example.explosiverobot.fragment.ActionCommonFragment;
import com.example.explosiverobot.modle.ActionTab;
import com.example.explosiverobot.receiver.UDPAcceptReceiver;
import com.example.explosiverobot.service.UdpService;
import com.example.explosiverobot.util.JumpItent;
import com.example.explosiverobot.util.SPManager;
import com.example.explosiverobot.view.weiget.PagerSlidingTabStrip;
import com.example.explosiverobot.view.weiget.TouchTextView;
import com.seabreeze.log.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vstc2.nativecaller.NativeCaller;

public class MainActivity extends BaseActivity implements UDPAcceptReceiver.UDPAcceptInterface, TouchTextView.OnTextTimeListener, PagerSlidingTabStrip.OnPagerSlidingTabStripChanged {

    public String TAG = this.getClass().getSimpleName();

    @BindView(R.id.pager_sliding_tabstrip)
    PagerSlidingTabStrip pagerSlidingTabstrip;
    @BindView(R.id.action_viewPager)
    ViewPager actionViewPager;
    @BindView(R.id.tv_add_group)
    TextView tvAddGroup;
    @BindView(R.id.iv_robot_bg)
    ImageView ivRobotBg;
    @BindView(R.id.et_inputGroupName)
    EditText etInputGroupName;
    //复位
    @BindView(R.id.ll_recovery)
    LinearLayout llRecovery;
    //照明灯前
    @BindView(R.id.tog_front)
    ToggleButton togFront;
    //照明灯后
    @BindView(R.id.tog_back)
    ToggleButton togBack;
    //脚掌前向上
    @BindView(R.id.tv_foot_front_top)
    TouchTextView tvFootFrontTop;
    //脚掌前向下
    @BindView(R.id.tv_foot_front_bottom)
    TouchTextView tvFootFrontBottom;
    //脚掌后向上
    @BindView(R.id.tv_foot_back_top)
    TouchTextView tvFootBackTop;
    //脚掌后向下
    @BindView(R.id.tv_foot_back_bottom)
    TouchTextView tvFootBackBottom;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_order)
    TextView tvOrder;

    private boolean isConnect;

    private boolean quit = false; //设置退出标识

    private LocalBroadcastManager mLbmManager;
    private boolean isAccept;
    //头部Tab
    private List<ActionTab> mActionTabsList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ActionViewPagerAdapter mActionViewPagerAdapter;
    //udp
    private UDPAcceptReceiver mUdpAcceptReceiver;

    private int mGroupNum = 0;//分组id
    //Tab本地数据
    private ActionTabDbManager mActionDbManager;
    private long actionId = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mActionDbManager = new ActionTabDbManager();
        mLbmManager = LocalBroadcastManager.getInstance(this);
        Intent startIntent = new Intent(this, UdpService.class);
        startService(startIntent);

        initTopTab();

        showDialog();
    }

    private int i = -1;

    @Override
    protected void initData() {
        pagerSlidingTabstrip.setOnPagerSlidingTabStripChanged(this);
        etInputGroupName.setFocusable(true);
        etInputGroupName.setFocusableInTouchMode(true);
        etInputGroupName.requestFocus();
        etInputGroupName.requestFocusFromTouch();
    }

    @Override
    protected void setListener() {
        tvFootFrontTop.setOnTimeListener(this);
        tvFootFrontBottom.setOnTimeListener(this);
        tvFootBackBottom.setOnTimeListener(this);
        tvFootBackTop.setOnTimeListener(this);
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
        stopService(new Intent(this, UdpService.class));
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
        sendBroadcast(new Intent(AppConstants.NET_LOONGGG_EXITAPP));
    }

    @Override
    public void onBackPressed() {

        if (!quit) { //询问退出程序
            showToast("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() { //启动定时任务
                @Override
                public void run() {
                    quit = false; //重置退出标识
                }
            }, 2000);
            quit = true;
        } else { //确认退出程序
            super.onBackPressed();
            finish();
            //退出时杀掉所有进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }

//        moveTaskToBack(true);
    }

    @OnClick({R.id.tv_add_group, R.id.iv_robot_bg, R.id.tog_back, R.id.tog_front, R.id.ll_recovery})
    public void onClick(View view) {
        switch (view.getId()) {
            //添加分组
            case R.id.tv_add_group:
                addGroup();
                break;
            case R.id.iv_robot_bg:
                startTasgAvtivity();
//                sendLocal(NetClient.GET_IP);
                break;
            case R.id.tog_back:
                // 当按钮第一次被点击时候响应的事件
                if (togBack.isChecked()) {
                    sendLocal(SPManager.controlLampBackOpen());
                    showToast("照明灯后开");
                }
                // 当按钮再次被点击时候响应的事件
                else {
                    sendLocal(SPManager.controlLampBackClose());
                    showToast("照明灯后关");
                }
                break;
            case R.id.tog_front:
                // 当按钮第一次被点击时候响应的事件
                if (togFront.isChecked()) {
//                    sendLocal(SPManager.controlLampFrontOpen());
                    sendLocal("z");
                    showToast("照明灯前开");
                }
                // 当按钮再次被点击时候响应的事件
                else {
//                    sendLocal(SPManager.controlLampFrontClose());
                    sendLocal("Z");
                    showToast("照明灯前关");
                }
                break;
//            case R.id.tv_foot_back_bottom:
//                sendLocal(SPManager.controlarmObstacleDown());
//                Print.e("后轮向下");
//                break;
//            case R.id.tv_foot_back_top:
//                sendLocal(SPManager.controlarmObstacleUp());
//                Print.e("后轮向上");
//                break;
            //复位
            case R.id.ll_recovery:
                sendLocal(SPManager.controlReset());
                break;
        }
    }


    /**
     * 顶部Tab
     */
    private void initTopTab() {
        addActionTab();
    }

    /**
     * 添加分组
     */
    private void addGroup() {
        String mTabName = etInputGroupName.getText().toString().trim();
        if (!"".equals(mTabName)) {
            List<ActionTab> actionTabs = mActionDbManager.queryByTabName(mTabName);
            if (!actionTabs.isEmpty()) {
                showToast("请不要添加相同的问题！");
                return;
            }
            ++mGroupNum;
            mActionTabsList.add(new ActionTab(mGroupNum + "", mTabName));
            mTitleList.add(mTabName);

            ActionCommonFragment actionCommonFragment_new = new ActionCommonFragment();
            Bundle bundle = new Bundle();
            bundle.putString("theme_name", mTabName);
            actionCommonFragment_new.setArguments(bundle);
            mFragmentList.add(actionCommonFragment_new);

            setPageTitle();
            mActionDbManager.insert(new ActionTab(mGroupNum + "", mTabName));
            showToast("添加成功");
        } else {
            showToast("输入不能为空！");
        }
    }


    /**
     * 添加数据
     */
    private void addActionTab() {
        if (mFragmentList != null && mFragmentList.size() > 0) {
            return;
        }
        mActionTabsList = mActionDbManager.loadAll();
        ++mGroupNum;

        if (mActionTabsList == null || mActionTabsList.size() == 0) {
            mActionDbManager.insert(new ActionTab(mGroupNum + "", "全部", actionId));
            mActionTabsList.add(new ActionTab(mGroupNum + "", "全部", actionId));
        }
        addTitle();
    }

    private void addTitle() {
        for (int i = 0; i < mActionTabsList.size(); i++) {
            mTitleList.add(mActionTabsList.get(i).getTab_name());
        }
        addFragmentList();
    }

    private void addFragmentList() {
        for (int i = 0; i < mActionTabsList.size(); i++) {
            ActionCommonFragment actionCommonFragment_new = new ActionCommonFragment();
            Bundle bundle = new Bundle();
            bundle.putString("theme_name", mActionTabsList.get(i).getTab_name());
            actionCommonFragment_new.setArguments(bundle);
            mFragmentList.add(actionCommonFragment_new);
        }
        setPageTitle();
    }

    /**
     * 设置Title样式
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
        etInputGroupName.setText("");

    }

    private void showDialog() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("正在连接中...");
        pDialog.show();
        pDialog.setCancelable(false);
        new CountDownTimer(500 * 7, 500) {
            public void onTick(long millisUntilFinished) {
                // you can change the progress bar color by ProgressHelper every 800 millis
                i++;
                switch (i) {
                    case 0:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                        break;
                    case 1:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                        break;
                    case 2:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                    case 3:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                        break;
                    case 4:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                        break;
                    case 5:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                        break;
                    case 6:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                }
            }

            public void onFinish() {
                i = -1;
                if (isConnect) {
                    pDialog.setTitleText("连接成功!")
                            .setConfirmText("确定")
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    tvState.setText("未连接");
                    pDialog.setTitleText("连接失败!")
                            .setConfirmText("确定")
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        }.start();
    }

    @Override
    public void UDPAcceptMessage(String content) {
        if (isAccept) {
            if (content.equals("udp connect")) {
                tvState.setText("已连接");
                isConnect = true;
            } else {
                tvOrder.setText(content);
            }
        }
    }

    private void sendLocal(byte[] bytes) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("bytes", bytes);
        mLbmManager.sendBroadcast(intent);
    }

    private void sendLocal(String order) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("order", order);
        mLbmManager.sendBroadcast(intent);
    }

    private void startTasgAvtivity() {
        JumpItent.jump(MainActivity.this, TaskActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onTextDowm() {
        sendLocal("x");
    }

    @Override
    public void onTextTimecount(View view, int count) {
        switch (view.getId()) {
            case R.id.tv_foot_front_bottom:
//                sendLocal(SPManager.controlarmObstacleStop(Constants.just + Constants.degree));
                sendLocal("1");
                Print.e("后轮 发送 1 ");
                break;
            case R.id.tv_foot_front_top:
                sendLocal("7");
                Print.e("后轮 发送 7 ");
                break;
            case R.id.tv_foot_back_bottom:
                sendLocal("3");
                Print.e("后轮 发送 3 ");
                break;
            case R.id.tv_foot_back_top:
                sendLocal("9");
                Print.e("后轮 发送 9 ");
                break;
        }
    }

    @Override
    public void onTextDownFinish(View view) {
        switch (view.getId()) {
            case R.id.tv_foot_front_bottom:
                Print.e("前轮停止");
                sendLocal(SPManager.controlarmObstacleStop());
                break;
            case R.id.tv_foot_front_top:
                Print.e("前轮停止");
                sendLocal(SPManager.controlarmObstacleStop());
                break;
            case R.id.tv_foot_back_bottom:
                showToast("后脚掌向下停止");
                break;
            case R.id.tv_foot_back_top:
                showToast("后脚掌向上停止");
                break;
        }
    }


    @Override
    public void onPageChanged(int position) {
        Log.e("GG pagetrip 当前positon", position + "");
        Log.e("GG pagetrip 当前positon", mActionTabsList.get(position).getTab_name());
    }
}
