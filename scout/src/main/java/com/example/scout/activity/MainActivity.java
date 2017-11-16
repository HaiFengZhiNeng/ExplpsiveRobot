package com.example.scout.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.scout.R;
import com.example.scout.common.BaseActivity;
import com.example.scout.common.Constants;
import com.example.scout.socket.TcpCallback;
import com.example.scout.socket.TcpSocketManager;
import com.example.scout.view.CircleViewByImage;
import com.example.scout.view.TouchImageView;
import com.example.scout.view.VideoSurfaceView;
import com.linkcard.media.LinkVideoCore;
import com.seabreeze.log.Print;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class MainActivity extends BaseActivity implements TouchImageView.OnImageTimeListener,
        CircleViewByImage.ActionCallback {


    @BindView(R.id.show_view)
    VideoSurfaceView showView;
    @BindView(R.id.ll_control)
    LinearLayout llControl;
    @BindView(R.id.move_view)
    CircleViewByImage moveView;
    @BindView(R.id.ll_front_lighting)
    LinearLayout llFrontLighting;
    @BindView(R.id.iv_front_lighting)
    ImageView ivFrontLighting;
    @BindView(R.id.ll_after_lighting)
    LinearLayout llAfterLighting;
    @BindView(R.id.iv_after_lighting)
    ImageView ivAfterLighting;
    @BindView(R.id.ll_front_image)
    LinearLayout llFrontImage;
    @BindView(R.id.iv_front_image)
    ImageView ivFrontImage;
    @BindView(R.id.ll_after_image)
    LinearLayout llAfterImage;
    @BindView(R.id.iv_after_image)
    ImageView ivAfterImage;
    @BindView(R.id.ll_lift_image)
    LinearLayout llLiftImage;
    @BindView(R.id.iv_lift_image)
    ImageView ivLiftImage;
    @BindView(R.id.ll_right_image)
    LinearLayout llRightImage;
    @BindView(R.id.iv_right_image)
    ImageView ivRightImage;
    @BindView(R.id.iv_arm_up)
    TouchImageView ivArmUp;
    @BindView(R.id.iv_arm_down)
    TouchImageView ivArmDown;
    @BindView(R.id.iv_speeh_up)
    ImageView ivSpeehUp;
    @BindView(R.id.iv_speeh_down)
    ImageView ivSpeehDown;
    @BindView(R.id.btn_ssid)
    Button btnSsid;
    @BindView(R.id.btn_pass)
    Button btnPass;

    private LinkVideoCore linkVideoCore;

    private boolean isShow;
    private boolean isFrontOpen;
    private boolean isAfterOpen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        linkVideoCore = new LinkVideoCore();
        linkVideoCore.sysinit("192.168.11.123");

        isShow = true;
    }

    @Override
    protected void initDb() {

    }

    @Override
    protected void initData() {
        TcpSocketManager.getInstance().startTcp(MainActivity.this);
    }

    @Override
    protected void setListener() {
        moveView.setCallback(this);
        ivArmUp.setOnTimeListener(this);
        ivArmDown.setOnTimeListener(this);
    }


    @Override
    protected void onDestroy() {
        linkVideoCore.sysuninit();
        TcpSocketManager.getInstance().endTcp();
        super.onDestroy();
    }


    @OnClick({R.id.show_view, R.id.ll_front_lighting, R.id.ll_after_lighting, R.id.ll_front_image,
            R.id.ll_after_image, R.id.ll_lift_image, R.id.ll_right_image,
            R.id.btn_ssid, R.id.btn_pass, R.id.iv_speeh_down, R.id.iv_speeh_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_view:
                if (isShow) {
                    isShow = false;
                    llControl.setVisibility(View.GONE);
                } else {
                    isShow = true;
                    llControl.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_front_lighting:
                if (isFrontOpen) {
                    TcpSocketManager.getInstance().sendTextMessageByTcp("f", new TcpCallback() {

                        @Override
                        public void onSuccess(String t) {
                            isFrontOpen = false;
                            ivFrontLighting.setBackgroundResource(R.mipmap.ic_light_close);
                            Print.e("前灯关");
                        }

                    });
                } else {

                    TcpSocketManager.getInstance().sendTextMessageByTcp("F", new TcpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            isFrontOpen = true;
                            ivFrontLighting.setBackgroundResource(R.mipmap.ic_light_open);
                            Print.e("前灯开");
                        }

                    });
                }
                break;
            case R.id.ll_after_lighting:
                if (isAfterOpen) {
                    TcpSocketManager.getInstance().sendTextMessageByTcp("g", new TcpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            isAfterOpen = false;
                            ivAfterLighting.setBackgroundResource(R.mipmap.ic_light_close);
                            Print.e("后灯关");
                        }
                    });

                } else {
                    TcpSocketManager.getInstance().sendTextMessageByTcp("G", new TcpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            isAfterOpen = true;
                            ivAfterLighting.setBackgroundResource(R.mipmap.ic_light_open);
                            Print.e("后灯开");
                        }
                    });
                }
                break;
            case R.id.ll_front_image:
                TcpSocketManager.getInstance().sendTextMessageByTcp("a", new TcpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        ivFrontImage.setBackgroundResource(R.mipmap.ic_camera_open);
                        ivAfterImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivLiftImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivRightImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        Print.e("前影像");
                    }
                });
                break;
            case R.id.ll_after_image:
                TcpSocketManager.getInstance().sendTextMessageByTcp("b", new TcpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        ivFrontImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivAfterImage.setBackgroundResource(R.mipmap.ic_camera_open);
                        ivLiftImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivRightImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        Print.e("后影像");
                    }
                });
                break;
            case R.id.ll_lift_image:
                TcpSocketManager.getInstance().sendTextMessageByTcp("c", new TcpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        ivFrontImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivAfterImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivLiftImage.setBackgroundResource(R.mipmap.ic_camera_open);
                        ivRightImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        Print.e("左影像");
                    }
                });
                break;
            case R.id.ll_right_image:
                TcpSocketManager.getInstance().sendTextMessageByTcp("d", new TcpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        ivFrontImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivAfterImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivLiftImage.setBackgroundResource(R.mipmap.ic_camera_close);
                        ivRightImage.setBackgroundResource(R.mipmap.ic_camera_open);
                        Print.e("右影像");
                    }
                });
                break;
            case R.id.iv_speeh_up:
                TcpSocketManager.getInstance().sendTextMessageByTcp("7", null);
                break;
            case R.id.iv_speeh_down:
                TcpSocketManager.getInstance().sendTextMessageByTcp("9", null);
                break;
            case R.id.btn_ssid:
                modifySsid();
                break;
            case R.id.btn_pass:
                modifyPass();
                break;
        }
    }


    @Override
    public void onImageDown() {

    }

    @Override
    public void onImageTimecount(View view, int count) {
        switch (view.getId()) {
            case R.id.iv_arm_up:
                TcpSocketManager.getInstance().sendTextMessageByTcp("1", null);
                Print.e("up");
                break;
            case R.id.iv_arm_down:
                TcpSocketManager.getInstance().sendTextMessageByTcp("3", null);
                Print.e("down");
                break;
        }
    }

    @Override
    public void onImageDownFinish(View view) {
        switch (view.getId()) {
            case R.id.iv_arm_up:
                TcpSocketManager.getInstance().sendTextMessageByTcp("0", null);
                Print.e("up finish");
                break;
            case R.id.iv_arm_down:
                TcpSocketManager.getInstance().sendTextMessageByTcp("0", null);
                Print.e("down finish");
                break;
        }
    }

    @Override
    public void forwardMove() {//上
        TcpSocketManager.getInstance().sendTextMessageByTcp("2", null);
        Print.e("上");
    }

    @Override
    public void backMove() {//下
        TcpSocketManager.getInstance().sendTextMessageByTcp("8", null);
        Print.e("下");
    }

    @Override
    public void leftMove() {//左
        TcpSocketManager.getInstance().sendTextMessageByTcp("4", null);
        Print.e("左");
    }

    @Override
    public void rightMove() {
        TcpSocketManager.getInstance().sendTextMessageByTcp("6", null);
        Print.e("右");//右
    }

    @Override
    public void actionUp() {//**
        TcpSocketManager.getInstance().sendTextMessageByTcp("5", null);
        Print.e("离开");
    }

    private void sendMessage(String instructions) {
        Print.e("");
        OkHttpUtils
                .get()
                .url(Constants.API.CONTROL_CMD_INSTRUCTIONS)
//                .addParams("instructions", instructions)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Print.e("onError");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Print.e(response);
                    }
                });
    }

    private void modifySsid() {
        OkHttpUtils
                .get()
                .url(Constants.API.CONTROL_CMD_SET_WIFI_SSID)
                .addParams("ssid", "ssid")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Print.e(call.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Print.e(response);
                    }
                });
    }

    private void modifyPass() {
        OkHttpUtils
                .get()
                .url(Constants.API.CONTROL_CMD_SET_WIFI_PWD)
                .addParams("pswd", "87654321")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Print.e(call.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Print.e(response);
                    }
                });
    }

}
