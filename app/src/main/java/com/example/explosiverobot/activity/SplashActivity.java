package com.example.explosiverobot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.explosiverobot.MainActivity;
import com.example.explosiverobot.R;
import com.example.explosiverobot.config.AppConstants;
import com.example.explosiverobot.util.JumpItent;
import com.example.explosiverobot.util.PermissionsChecker;
import com.example.explosiverobot.util.SpUtils;
import com.ocean.mvp.library.view.BaseActivity;

public class SplashActivity extends BaseActivity {

    //请求权限
    private PermissionsChecker mChecker;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //权限
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onViewCreateBefore() {
        // 判断是否是第一次开启应用
        boolean isFirstOpen = SpUtils.getBoolean(this, AppConstants.FIRST_OPEN);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            enterHomeActivity();
            return;
        }
        // 如果不是第一次启动app，则正常显示启动屏
        super.onViewCreateBefore();
    }

    @Override
    protected void onViewCreated() {
        super.onViewInit();
        mChecker = new PermissionsChecker(this);

        if (mChecker.lacksPermissions(PERMISSIONS)) {
            //请求权限
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            isSplash();
        }
    }

    private void isSplash() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                enterHomeActivity();
            }
        }, 2000);
    }

    private void enterHomeActivity() {
        JumpItent.jump(SplashActivity.this, GuideActivity.class, true);
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isSplash();
        } else {
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
