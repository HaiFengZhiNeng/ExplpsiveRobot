package com.example.superEod.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.example.superEod.R;
import com.example.superEod.base.activity.BaseActivity;
import com.example.superEod.base.config.AppConstants;
import com.example.superEod.base.config.BaseHandler;
import com.example.superEod.service.BridgeService;
import com.example.superEod.util.PermissionsChecker;

import butterknife.BindView;
import vstc2.nativecaller.NativeCaller;

/**
 * Created by zhangyuanyuan on 2017/9/25.
 */

public class SplashActivity extends BaseActivity implements BaseHandler.HandleMessage {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA

    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final int REFRESH_COMPLETE = 0X153;
    private static final int REFRESH_START = 0X155;

    private Handler handler = new BaseHandler<>(SplashActivity.this);

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        AppConstants.displayWidth = getWindowManager().getDefaultDisplay().getWidth();
        AppConstants.displayHeight = getWindowManager().getDefaultDisplay().getHeight();
        mPermissionsChecker = new PermissionsChecker(this);


        Glide.with(this)
                .load(R.mipmap.ic_splash)
                .skipMemoryCache(true)
                .animate(animationObject)
                .into(ivSplash);
        mHandler.sendEmptyMessageDelayed(0, 3000);

        startThread();
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
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {

            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                //请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

            } else {
                startActivity(); // 全部权限都已获取
            }
        } else {
            startActivity();
        }
    }

    private void startActivity() {
        handler.sendEmptyMessageDelayed(REFRESH_START, 2000);
    }

    private void startThread() {
        Intent intent = new Intent(SplashActivity.this, BridgeService.class);
        startService(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            startActivity();
        } else {
            handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
        }
    }


    /**
     * 含有全部的权限
     *
     * @param grantResults
     * @return
     */
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
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



    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                finish();
                break;
            case REFRESH_START:
                startActivity(new Intent(SplashActivity.this, MyActivity.class));
                finish();
                break;
        }
    }
}
