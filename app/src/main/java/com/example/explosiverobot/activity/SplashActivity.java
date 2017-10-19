package com.example.explosiverobot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.base.config.BaseHandler;
import com.example.explosiverobot.service.BridgeService;
import com.example.explosiverobot.util.JumpItent;
import com.example.explosiverobot.util.PermissionsChecker;
import com.example.explosiverobot.util.PreferencesUtils;

import butterknife.BindView;
import vstc2.nativecaller.NativeCaller;

public class SplashActivity extends BaseActivity implements BaseHandler.HandleMessage {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.READ_SMS,
//            Manifest.permission.READ_CONTACTS,

    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final int REFRESH_COMPLETE = 0X153;

    private Handler handler = new BaseHandler<>(SplashActivity.this);

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        mPermissionsChecker = new PermissionsChecker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {

            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                //请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

            } else {
                startAnim(); // 全部权限都已获取
            }
        } else {
            startAnim();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            startAnim();
        } else {
            handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
        }
    }

    /**
     * 启动动画
     */
    private void startAnim() {
        AlphaAnimation alpha = getAlphaAnimation();

        startThread();
        ivSplash.startAnimation(alpha);
    }

    @NonNull
    private AlphaAnimation getAlphaAnimation() {
        AnimationSet set = new AnimationSet(false); //设置动画集合；
        //缩放动画；
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(2000);
        scale.setFillAfter(true);

        //淡入淡出动画；
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);

        set.addAnimation(scale);
        set.addAnimation(alpha);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                jumpNextPage();
            }
        });
        return alpha;
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

    private void jumpNextPage() {
        boolean userGuide = PreferencesUtils.getBoolean(this, AppConstants.FIRST_OPEN, false);
        if (!userGuide) {
            JumpItent.jump(SplashActivity.this, GuideActivity.class, true);
        } else {
            JumpItent.jump(SplashActivity.this, MainActivity.class, true);
        }
        finish();
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


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                finish();
                break;
        }
    }
}
