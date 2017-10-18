package com.example.explosiverobot.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.explosiverobot.config.BaseHandler;

import butterknife.ButterKnife;

/**
 * Created by zhangyuanyuan on 2017/10/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseHandler.HandleMessage{

    public String TAG = this.getClass().getSimpleName();

    private BaseHandler mBaseHandler;

    protected Context mContext;
    protected Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        init();
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
    }


    protected abstract int getContentViewId();
    protected abstract void init();

    /**
     * 初始化一个Handler，如果需要使用Handler，先调用此方法，
     * 然后可以使用postRunnable(Runnable runnable)，
     * sendMessage在handleMessage（Message msg）中接收msg
     */
    public void initHandler() {
        mBaseHandler = new BaseHandler(this);
    }

    /**
     * 返回Handler，在此之前确定已经调用initHandler（）
     * @return Handler
     */
    public Handler getHandler() {
        return mBaseHandler;
    }

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

}
