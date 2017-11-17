package com.example.superEod.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.example.superEod.R;
import com.example.superEod.base.activity.BaseActivity;

/**
 * Created by zhangyuanyuan on 2017/9/25.
 */

public class SplashActivity extends BaseActivity {


    private ImageView ivSplash;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//            finish();
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ivSplash = (ImageView) findViewById(R.id.iv_splash);
        Glide.with(this)
                .load(R.mipmap.ic_splash)
                .skipMemoryCache(true)
                .animate(animationObject)
                .into(ivSplash);
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
            view.setAlpha(0f);

            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeAnim.setDuration(2000);
            fadeAnim.start();
        }
    };
}
