//package com.example.explosiverobot.control;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//
//import com.example.explosiverobot.R;
//import com.example.explosiverobot.actionOrder.ActionCommonFragment;
//import com.example.explosiverobot.base.BaseActivity;
//import com.example.explosiverobot.modle.ActionTab;
//import com.example.explosiverobot.udp.UdpControl;
//import com.example.explosiverobot.weidget.PagerSlidingTabStrip;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class ControlView extends BaseActivity<ControlPresenter> implements IControlView, android.view.View.OnClickListener {
//
//    private UdpControl mUdpControl;
//
//    @BindView(R.id.tabStrip)
//    PagerSlidingTabStrip mPagerSlidingTabStrip;
//    @BindView(R.id.action_viewPager)
//    ViewPager mActionViewPager;
//
//    //头部Tab
//    private List<ActionTab> mActionTabsList = new ArrayList<>();
//    private List<String> mTitle = new ArrayList<>();
//    private List<Fragment> mFragmentList = new ArrayList<>();
//
//    @Override
//    protected int getContentViewResource() {
//        return R.layout.activity_control_view;
//    }
//
//
//    @Override
//    public Context getContext() {
//        return this;
//    }
//
//    @Override
//    public ControlPresenter createPresenter() {
//        return new ControlPresenter(this);
//    }
//
//    @Override
//    protected void onViewInit() {
//        ButterKnife.bind(this);
//        initTopTab();
//        super.onViewInit();
//    }
//
//    /**
//     * 顶部Tab
//     */
//    public void initTopTab() {
//        addActionTop();
//        if (mFragmentList != null && mFragmentList.size() > 0) {
//            return;
//        }
//        for (int i = 0; i < mActionTabsList.size(); i++) {
//            mTitle.add(mActionTabsList.get(i).getName());
//        }
//        for (int i = 0; i < mTitle.size(); i++) {
//            ActionCommonFragment actionCommonFragment_new = new ActionCommonFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("theme_id", mActionTabsList.get(i).getId());
//            actionCommonFragment_new.setArguments(bundle);
//            mFragmentList.add(actionCommonFragment_new);
//        }
//        setPageTitle();
//    }
//
//    /**
//     * 添加数据
//     */
//    public void addActionTop() {
//        mActionTabsList.add(new ActionTab("0", "全部"));
//        mActionTabsList.add(new ActionTab("1", "前进"));
//        mActionTabsList.add(new ActionTab("2", "后退"));
//        mActionTabsList.add(new ActionTab("3", "左移"));
//        mActionTabsList.add(new ActionTab("4", "右移"));
//    }
//
//    /*设置Title样式*/
//    private void setPageTitle() {
//        //viewPager 记载adapter
//        mActionViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
//        mActionViewPager.setCurrentItem(0, true);
////        live_viewPager.setOffscreenPageLimit(10);//保存viewpage数据不被回收
//        mPagerSlidingTabStrip.setViewPager(mActionViewPager);
//        mPagerSlidingTabStrip.setIndicatorHeight(5);//设置下划线宽度
//        mPagerSlidingTabStrip.setUnderlineHeight(2);//设置底部边框宽度
//        mPagerSlidingTabStrip.setDividerColor(getResources().getColor(R.color.color_tab_unselect)); //中间竖线颜色
//        mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.color_white));// 下划线颜色
//        mPagerSlidingTabStrip.setSelectedTextColor(getResources().getColor(R.color.color_white));//文字选中颜色
//        mPagerSlidingTabStrip.setTextColor(getResources().getColor(R.color.color_tab_unselect));//没有选中时文字颜色
//        mPagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_20));//字体大小
//        mPagerSlidingTabStrip.setShouldExpand(true);
//        mPagerSlidingTabStrip.setTabBackground(R.drawable.background_tab);
//
//    }
//
//    @Override
//    public void onClick(android.view.View v) {
//
//    }
//
//    class MyAdapter extends FragmentPagerAdapter {
//
//        public MyAdapter(FragmentManager pagerAdapter) {
//            super(pagerAdapter);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTitle.get(position);
//        }
//    }
//
//}
