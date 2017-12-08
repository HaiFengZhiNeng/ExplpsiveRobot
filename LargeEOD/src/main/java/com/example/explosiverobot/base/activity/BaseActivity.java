package com.example.explosiverobot.base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.service.BridgeService;
import com.example.explosiverobot.service.UdpService;
import com.example.explosiverobot.util.AndroidBug54971Workaround;

import butterknife.ButterKnife;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getContentViewId());
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        ButterKnife.bind(this);
        init(savedInstanceState);
        initData();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.NET_LOONGGG_EXITAPP);
        this.registerReceiver(this.finishAppReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.finishAppReceiver);
    }

    /**
     * 设置全屏
     */
    public void fullScreen() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    protected abstract int getContentViewId();
    /**
     * 此方法描述的是： 初始化所有
     */
    protected abstract void init(Bundle savedInstanceState);
    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    protected abstract void initData();

    /**
     * 此方法描述的是： 设置所有事件监听
     */
    protected abstract void setListener();

    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示toast
     *
     * @param resStr
     * @return Toast对象，便于控制toast的显示与关闭
     */
    public void showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(BaseActivity.this, resStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * 关闭Activity的广播，放在自定义的基类中，让其他的Activity继承这个Activity就行
     */
    protected BroadcastReceiver finishAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentBridge = new Intent(BaseActivity.this, BridgeService.class);
            stopService(intentBridge);
            Intent intentUdp = new Intent(BaseActivity.this, UdpService.class);
            stopService(intentUdp);
            finish();
        }
    };

    public double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }
    public double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }
}
