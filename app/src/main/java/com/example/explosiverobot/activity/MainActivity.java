package com.example.explosiverobot.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.explosiverobot.view.weiget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class MainActivity extends BaseActivity implements UDPAcceptReceiver.UDPAcceptInterface {

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
        sendBroadcast(new Intent(AppConstants.NET_LOONGGG_EXITAPP));
    }

    @OnClick({R.id.tv_add_group, R.id.iv_robot_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            //添加分组
            case R.id.tv_add_group:
                addGroup();
                break;
            case R.id.iv_robot_bg:
                startTasgAvtivity();
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
            mActionTabsList.add(new ActionTab(mGroupNum + "", mTabName, actionId));

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
        etInputGroupName.setText("");

    }


    @Override
    public void UDPAcceptMessage(String content) {
        if (isAccept) {
            showToast(content);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
