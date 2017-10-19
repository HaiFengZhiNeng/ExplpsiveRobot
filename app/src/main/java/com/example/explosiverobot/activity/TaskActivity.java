package com.example.explosiverobot.activity;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.base.config.ContentCommon;
import com.example.explosiverobot.ipcamera.MyRender;
import com.example.explosiverobot.service.BridgeService;

import butterknife.BindView;
import vstc2.nativecaller.NativeCaller;

public class TaskActivity extends BaseActivity implements BridgeService.PlayInterface {

    private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;

    @BindView(R.id.gl_surface_view)
    GLSurfaceView glSurfaceView;

    private String strDID;

    private MyRender myRender;

    //数据视频流回掉
    private boolean bDisplayFinished = true;
    private byte[] mVideodata = null;
    public int nVideoWidths = 0;
    public int nVideoHeights = 0;
    public boolean isH264 = false;//是否是H264格式标志

    private Handler mDrawHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
//                setViewVisible();
                NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
            }

            switch (msg.what) {
                case 1: // h264
//                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AppConstants.displayWidth, AppConstants.displayHeight * 3 / 4);
//                        lp.gravity = Gravity.CENTER;
//                        glSurfaceView.setLayoutParams(lp);
//                    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AppConstants.displayWidth, AppConstants.displayHeight);
//                        lp.gravity = Gravity.CENTER;
//                        glSurfaceView.setLayoutParams(lp);
//                    }
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

        BridgeService.setPlayInterface(this);
        NativeCaller.StartPPPPLivestream(strDID, 10, 1);//确保不能重复start
        NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);

        myRender = new MyRender(glSurfaceView);
        glSurfaceView.setRenderer(myRender);

    }


    @Override
    public void callBackCameraParamNotify(String did, int resolution, int brightness, int contrast, int hue, int saturation, int flip, int mode) {
        Log.e(TAG, "callBackCameraParamNotify");
        Log.e(TAG, "设备返回的参数" + resolution + "," + brightness + "," + contrast + "," + hue + "," + saturation + "," + flip + "," + mode);
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

}
