package com.example.explosiverobot.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.actionOrder.ActionCommonFragment;
import com.example.explosiverobot.adapter.ActionViewPagerAdapter;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.modle.ActionTab;
import com.example.explosiverobot.service.UdpService;
import com.example.explosiverobot.weidget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    public String TAG = this.getClass().getSimpleName();

    @BindView(R.id.pager_sliding_tabstrip)
    PagerSlidingTabStrip pagerSlidingTabstrip;
    @BindView(R.id.action_viewPager)
    ViewPager actionViewPager;
    @BindView(R.id.tv_add_group)
    TextView tvAddGroup;

    private LocalBroadcastManager mLbmManager;
    private boolean isAccept;
    //头部Tab
    private List<ActionTab> mActionTabsList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ActionViewPagerAdapter mActionViewPagerAdapter;

    private UDPAcceptReceiver mUdpAcceptReceiver;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

        mLbmManager = LocalBroadcastManager.getInstance(this);
        Intent startIntent = new Intent(this, UdpService.class);
        startService(startIntent);

        initTopTab();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isAccept = true;
        mUdpAcceptReceiver = new UDPAcceptReceiver();
        IntentFilter intentFilter = new IntentFilter(AppConstants.UDP_ACCEPT_ACTION);
        mLbmManager.registerReceiver(mUdpAcceptReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAccept = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
    }

    @OnClick({R.id.tv_add_group})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_add_group:
                byte[] bytes = new byte[3];
                bytes[0] = (byte) 0xAA;
                bytes[1] = (byte) 0x03;
                bytes[2] = (byte) 0xBB;
                sendLocal(bytes);
                break;
        }
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


    public class UDPAcceptReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isAccept){
                return;
            }
            String content = intent.getStringExtra("content");
            if (content != null) {
                Log.e(TAG, "服务发过来的数据 :" + content);
            }
        }
    }

    private void sendLocal(byte[] bytes) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("bytes", bytes);
        mLbmManager.sendBroadcast(intent);
    }


}
