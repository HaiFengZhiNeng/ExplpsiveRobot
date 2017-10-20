package com.example.explosiverobot.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.base.config.ContentCommon;
import com.example.explosiverobot.ipcamera.MyRender;
import com.example.explosiverobot.receiver.UDPAcceptReceiver;
import com.example.explosiverobot.service.BridgeService;

import butterknife.BindView;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class TaskActivity extends BaseActivity implements BridgeService.PlayInterface, UDPAcceptReceiver.UDPAcceptInterface {

    @BindView(R.id.gl_surface_view)
    GLSurfaceView glSurfaceView;
    @BindView(R.id.tv_model)
    TextView tvModel;
    @BindView(R.id.tv_map)
    TextView tvMap;
    @BindView(R.id.iv_model)
    ImageView ivModel;
    @BindView(R.id.iv_map)
    ImageView ivMap;
    @BindView(R.id.tv_drive)
    TextView tvDrive;
    @BindView(R.id.tv_control)
    TextView tvControl;
    @BindView(R.id.tv_inspect)
    TextView tvInspect;
    @BindView(R.id.re_drive)
    RelativeLayout reDrive;
    @BindView(R.id.re_control)
    RelativeLayout reControl;
    @BindView(R.id.re_inspect)
    RelativeLayout reInspect;
    @BindView(R.id.ic_front_upper)
    ImageView icFrontUpper;
    @BindView(R.id.ic_front_lower)
    ImageView icFrontLower;
    @BindView(R.id.ic_after_upper)
    ImageView icAfterUpper;
    @BindView(R.id.ic_after_lower)
    ImageView icAfterLower;
    @BindView(R.id.tv_speed_high_front)
    TextView tvSpeedHighFront;
    @BindView(R.id.tv_speed_high)
    TextView tvSpeedHigh;
    @BindView(R.id.tv_speed_medium_front)
    TextView tvSpeedMediumFront;
    @BindView(R.id.tv_speed_medium)
    TextView tvSpeedMedium;
    @BindView(R.id.tv_speed_low_front)
    TextView tvSpeedLowFront;
    @BindView(R.id.tv_speed_low)
    TextView tvSpeedLow;
    @BindView(R.id.iv_arm)
    ImageView ivArm;
    @BindView(R.id.sc_dodge)
    SwitchCompat scDodge;

    private String strDID;

    private MyRender myRender;

    private LocalBroadcastManager mLbmManager;
    private boolean isAccept;
    //udp
    private UDPAcceptReceiver mUdpAcceptReceiver;
    //数据视频流回掉
    private boolean bDisplayFinished = true;
    private byte[] mVideodata = null;
    public int nVideoWidths = 0;
    public int nVideoHeights = 0;
    public boolean isH264 = false;//是否是H264格式标志

    private boolean isDodge;//自动避让

    private Handler mDrawHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
            }

            switch (msg.what) {
                case 1: // h264
                    myRender.writeSample(mVideodata, nVideoWidths, nVideoHeights);
                    break;
            }
            if (msg.what == 1) {
                bDisplayFinished = true;
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task;
    }

    @Override
    protected void init() {

        strDID = AppConstants.deviceId;

        mLbmManager = LocalBroadcastManager.getInstance(this);

        BridgeService.setPlayInterface(this);
        NativeCaller.StartPPPPLivestream(strDID, 10, 1);//确保不能重复start
        NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);

        myRender = new MyRender(glSurfaceView);
        glSurfaceView.setRenderer(myRender);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        scDodge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isDodge) {
                    isDodge = true;
                } else {
                    isDodge = false;
                }
            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
    }

    @OnClick({R.id.tv_model, R.id.tv_map, R.id.tv_drive, R.id.tv_control, R.id.tv_inspect,
            R.id.ic_front_upper, R.id.ic_front_lower, R.id.ic_after_upper, R.id.ic_after_lower,
            R.id.tv_speed_high, R.id.tv_speed_medium, R.id.tv_speed_low})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_model:
                choiceTop(true);
                break;
            case R.id.tv_map:
                choiceTop(false);
                break;
            case R.id.tv_drive:
                tvDrive.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                reDrive.setVisibility(View.VISIBLE);
                tvControl.setBackgroundColor(getResources().getColor(R.color.task_deep));
                reControl.setVisibility(View.GONE);
                tvInspect.setBackgroundColor(getResources().getColor(R.color.task_deep));
                reInspect.setVisibility(View.GONE);
                break;
            case R.id.tv_control:
                tvControl.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                reControl.setVisibility(View.VISIBLE);
                tvDrive.setBackgroundColor(getResources().getColor(R.color.task_deep));
                reDrive.setVisibility(View.GONE);
                tvInspect.setBackgroundColor(getResources().getColor(R.color.task_deep));
                reInspect.setVisibility(View.GONE);
                break;
            case R.id.tv_inspect:
                tvInspect.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                reInspect.setVisibility(View.VISIBLE);
                tvDrive.setBackgroundColor(getResources().getColor(R.color.task_deep));
                reDrive.setVisibility(View.GONE);
                tvControl.setBackgroundColor(getResources().getColor(R.color.task_deep));
                reControl.setVisibility(View.GONE);
                break;
            case R.id.ic_front_upper://前上
                showToast("前上");
                break;
            case R.id.ic_front_lower://前下
                showToast("前下");
                break;
            case R.id.ic_after_upper://后上
                showToast("后上");
                break;
            case R.id.ic_after_lower://后下
                showToast("后下");
                break;
            case R.id.tv_speed_high:
                tvSpeedHighFront.setVisibility(View.VISIBLE);
                tvSpeedMediumFront.setVisibility(View.GONE);
                tvSpeedLowFront.setVisibility(View.GONE);
                showToast("高速");
                break;
            case R.id.tv_speed_medium:
                tvSpeedHighFront.setVisibility(View.GONE);
                tvSpeedMediumFront.setVisibility(View.VISIBLE);
                tvSpeedLowFront.setVisibility(View.GONE);
                showToast("中速");
                break;
            case R.id.tv_speed_low:
                tvSpeedHighFront.setVisibility(View.GONE);
                tvSpeedMediumFront.setVisibility(View.GONE);
                tvSpeedLowFront.setVisibility(View.VISIBLE);
                showToast("低速");
                break;
        }
    }

    private void choiceTop(boolean b) {
        tvModel.setTextColor(b ? getResources().getColor(R.color.color_black) : getResources().getColor(R.color.color_white));
        tvModel.setBackgroundColor(b ? getResources().getColor(R.color.task_while) : getResources().getColor(R.color.task_deep));
        ivModel.setVisibility(b ? View.VISIBLE : View.GONE);
        tvMap.setTextColor(!b ? getResources().getColor(R.color.color_black) : getResources().getColor(R.color.color_white));
        tvMap.setBackgroundColor(!b ? getResources().getColor(R.color.task_while) : getResources().getColor(R.color.task_deep));
        ivMap.setVisibility(!b ? View.VISIBLE : View.GONE);
    }

    private void sendLocal(byte[] bytes) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("bytes", bytes);
        mLbmManager.sendBroadcast(intent);
    }

    @Override
    public void callBackCameraParamNotify(String did, int resolution, int brightness, int contrast, int hue, int saturation, int flip, int mode) {
//        Log.e(TAG, "callBackCameraParamNotify");
//        Log.e(TAG, "设备返回的参数" + resolution + "," + brightness + "," + contrast + "," + hue + "," + saturation + "," + flip + "," + mode);
    }


    @Override
    public void callBackVideoData(byte[] videobuf, int h264Data, int len, int width, int height) {
//        Log.e(TAG, "callBackVideoData 视频数据流回调");
//        Log.e(TAG, "底层返回数据" + "videobuf:" + videobuf + "--" + "h264Data" + h264Data + "len" + len + "width" + width + "height" + height);
        if (!bDisplayFinished)
            return;
        bDisplayFinished = false;

        mVideodata = videobuf;
        Message msg = new Message();
        if (h264Data == 1) { // H264

            nVideoWidths = width;//1920
            nVideoHeights = height;//1080
            isH264 = true;
            msg.what = 1;
        }
        mDrawHandler.sendMessage(msg);
    }

    @Override
    public void callBackMessageNotify(String did, int msgType, int param) {
        Log.e(TAG, "callBackMessageNotify");
        Log.e(TAG, "MessageNotify did: " + did + " msgType: " + msgType + " param: " + param);
    }

    @Override
    public void callBackAudioData(byte[] pcm, int len) {
        Log.e(TAG, "callBackAudioData");
        Log.e(TAG, "AudioData: len :+ " + len);
    }

    @Override
    public void callBackH264Data(byte[] h264, int type, int size) {
        Log.e(TAG, "callBackH264Data");
        Log.e(TAG, "CallBack_H264Data" + " type:" + type + " size:" + size);
    }

    @Override
    public void UDPAcceptMessage(String content) {
        if(isAccept){
            showToast(content);
        }
    }

}
