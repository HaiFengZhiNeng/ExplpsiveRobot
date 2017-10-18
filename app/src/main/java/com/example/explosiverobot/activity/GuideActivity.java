package com.example.explosiverobot.activity;

import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.explosiverobot.R;
import com.example.explosiverobot.adapter.GuideViewPagerAdapter;
import com.example.explosiverobot.config.AppConstants;
import com.example.explosiverobot.util.JumpItent;
import com.example.explosiverobot.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GuideActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    @BindView(R.id.btn_enter)
    Button btnEnter;
    @BindView(R.id.ll)
    LinearLayout ll;

    private GuideViewPagerAdapter adapter;
    private List<View> views = new ArrayList<>();

    // 引导页图片资源
    private static final int[] pics = {R.layout.guid_view1,
            R.layout.guid_view2, R.layout.guid_view3, R.layout.guid_view4};

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void init() {
        views = new ArrayList<View>();

        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);

            if (i == pics.length - 1) {
                btnEnter = (Button) view.findViewById(R.id.btn_login);
                btnEnter.setTag("enter");
                btnEnter.setOnClickListener(this);
            }

            views.add(view);
        }

        // 初始化adapter
        adapter = new GuideViewPagerAdapter(views);
        vpGuide.setAdapter(adapter);
        vpGuide.setOnPageChangeListener(new PageChangeListener());

        initDots();
    }


    private void initDots() {
        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
            dots[i].setOnClickListener(this);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态

    }

    /**
     * 设置当前view
     *
     * @param position
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vpGuide.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().equals("enter")) {
            PreferencesUtils.putBoolean(GuideActivity.this, AppConstants.FIRST_OPEN, true);
            JumpItent.jump(GuideActivity.this, MainActivity.class, true);
            finish();
            return;
        }

        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    @Override
    public void handleMessage(Message msg) {

    }


    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int position) {
            // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。

        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            // arg0 :当前页面，及你点击滑动的页面
            // arg1:当前页面偏移的百分比
            // arg2:当前页面偏移的像素位置

        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            setCurDot(position);
        }

    }


}
