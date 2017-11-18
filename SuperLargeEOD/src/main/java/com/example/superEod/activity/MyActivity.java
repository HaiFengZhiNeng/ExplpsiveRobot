package com.example.superEod.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.superEod.R;
import com.example.superEod.base.activity.BaseActivity;
import com.example.superEod.base.config.AppConstants;
import com.example.superEod.base.config.ContentCommon;
import com.example.superEod.ipcamera.MyRender;
import com.example.superEod.modle.Tele;
import com.example.superEod.receiver.UDPAcceptReceiver;
import com.example.superEod.service.BridgeService;
import com.example.superEod.service.UdpService;
import com.example.superEod.util.GpsUtils;
import com.example.superEod.view.weiget.CircleViewByImage;
import com.seabreeze.log.Print;

import butterknife.BindView;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class MyActivity extends BaseActivity implements
        BridgeService.AddCameraInterface,
        BridgeService.CallBackMessageInterface,
        BridgeService.IpcamClientInterface,
        BridgeService.PlayInterface,
        UDPAcceptReceiver.UDPAcceptInterface {

    private static final String STR_MSG_PARAM = "msgparam";
    private static final String STR_DID = "did";

    @BindView(R.id.gl_surface_view)
    GLSurfaceView glSurfaceView;
    @BindView(R.id.tv_model)
    TextView tvModel;
    @BindView(R.id.re_model)
    RelativeLayout reModel;
    @BindView(R.id.tv_drive)
    TextView tvDrive;
    @BindView(R.id.tv_control)
    TextView tvControl;
    @BindView(R.id.rb_tele1)
    RadioButton rbTele1;
    @BindView(R.id.rb_tele2)
    RadioButton rbTele2;
    @BindView(R.id.rb_tele3)
    RadioButton rbTele3;
    @BindView(R.id.rb_tele4)
    RadioButton rbTele4;
    @BindView(R.id.rg_tele)
    RadioGroup rgTele;
    @BindView(R.id.ib_hori_tour)
    ImageButton ibHoriTour;
    @BindView(R.id.ib_vert_tour)
    ImageButton ibVertTour;
    @BindView(R.id.rl_ip_control)
    RelativeLayout rlIpControl;
    @BindView(R.id.control_circle_view)
    CircleViewByImage controlCircleView;
    @BindView(R.id.tv_camera_state)
    TextView tvCameraState;
    @BindView(R.id.re_drive)
    RelativeLayout reDrive;
    @BindView(R.id.re_control)
    RelativeLayout reControl;
    @BindView(R.id.y1)
    TextView y1;
    @BindView(R.id.y7)
    TextView y7;
    @BindView(R.id.y3)
    TextView y3;
    @BindView(R.id.y9)
    TextView y9;
    @BindView(R.id.ff)
    ToggleButton ff;
    @BindView(R.id.rr)
    ToggleButton rr;
    @BindView(R.id.zz)
    ToggleButton zz;
    @BindView(R.id.xx)
    ToggleButton xx;
    @BindView(R.id.cc)
    ToggleButton cc;
    @BindView(R.id.l)
    TextView l;
    @BindView(R.id.j)
    TextView j;
    @BindView(R.id.w)
    TextView w;
    @BindView(R.id.s)
    TextView s;
    @BindView(R.id.a)
    TextView a;
    @BindView(R.id.i)
    TextView i;
    @BindView(R.id.m)
    TextView m;
    @BindView(R.id.k)
    TextView k;
    @BindView(R.id.b)
    TextView b;
    @BindView(R.id.n)
    TextView n;
    @BindView(R.id.p)
    TextView p;
    @BindView(R.id.o)
    TextView o;
    @BindView(R.id.g)
    TextView g;
    @BindView(R.id.h)
    TextView h;

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

    private boolean isLeftRight;
    private boolean isUpDown;


    private Handler mDrawHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                NativeCaller.PPPPGetSystemParams(Tele.getInstance().getDid(), ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
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

    private int tag = 0;

    private Handler PPPPMsgHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            int msgParam = bd.getInt(STR_MSG_PARAM);
            int msgType = msg.what;
            String did = bd.getString(STR_DID);
            switch (msgType) {
                case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
                    switch (msgParam) {
                        case ContentCommon.PPPP_STATUS_CONNECTING://0
                            Print.e(getString(R.string.pppp_status_connecting));
                            showToast(getString(R.string.pppp_status_connecting));
                            tvCameraState.setText(getString(R.string.pppp_status_connecting));
                            tag = 2;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
                            Print.e(getString(R.string.pppp_status_connect_failed));
                            showToast(getString(R.string.pppp_status_connect_failed));
                            tvCameraState.setText(getString(R.string.pppp_status_connect_failed));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_DISCONNECT://4
                            Print.e(getString(R.string.pppp_status_disconnect));
                            showToast(getString(R.string.pppp_status_disconnect));
                            tvCameraState.setText(getString(R.string.pppp_status_disconnect));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_INITIALING://1
                            Print.e(getString(R.string.pppp_status_initialing));
                            showToast(getString(R.string.pppp_status_initialing));
                            tvCameraState.setText(getString(R.string.pppp_status_initialing));
                            tag = 2;
                            break;
                        case ContentCommon.PPPP_STATUS_INVALID_ID://5
                            Print.e(getString(R.string.pppp_status_invalid_id));
                            showToast(getString(R.string.pppp_status_invalid_id));
                            tvCameraState.setText(getString(R.string.pppp_status_invalid_id));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
                            //摄像机在线之后读取摄像机类型
                            String cmd = "get_status.cgi?loginuse=admin&loginpas=" + Tele.getInstance().getPwd() + "&user=admin&pwd=" + Tele.getInstance().getPwd();
                            NativeCaller.TransferMessage(did, cmd, 1);
                            Print.e(getString(R.string.pppp_status_online));
                            showToast(getString(R.string.pppp_status_online));
                            tvCameraState.setText(getString(R.string.pppp_status_online));
                            priviewIpcamera();
                            tag = 1;
                            break;
                        case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
                            Print.e(getString(R.string.device_not_on_line));
                            showToast(getString(R.string.device_not_on_line));
                            tvCameraState.setText(getString(R.string.device_not_on_line));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
                            Print.e(getString(R.string.pppp_status_connect_timeout));
                            showToast(getString(R.string.pppp_status_connect_timeout));
                            tvCameraState.setText(getString(R.string.pppp_status_connect_timeout));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
                            Print.e(getString(R.string.pppp_status_pwd_error));
                            showToast(getString(R.string.pppp_status_pwd_error));
                            tvCameraState.setText(getString(R.string.pppp_status_pwd_error));
                            tag = 0;
                            break;
                        default:
                            Print.e(getString(R.string.pppp_status_unknown));
                            showToast(getString(R.string.pppp_status_unknown));
                            tvCameraState.setText(getString(R.string.pppp_status_unknown));
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
                            || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did);
                    }
                    break;
                case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
                    break;

            }

        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mLbmManager = LocalBroadcastManager.getInstance(this);
        Intent startIntent = new Intent(this, UdpService.class);
        startService(startIntent);
        initCamera();

        myRender = new MyRender(glSurfaceView);
        glSurfaceView.setRenderer(myRender);

        if (!GpsUtils.isOPen(this)) {
            GpsUtils.openGPS(this);
        }
    }


    @Override
    protected void initData() {
        tvModel.setTextColor(getResources().getColor(R.color.color_black));
        tvModel.setBackgroundColor(getResources().getColor(R.color.task_while));

        Tele.getInstance().setTele0();
    }

    @Override
    protected void setListener() {
        rgTele.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.rb_tele1:
                        stopIpcamera();
                        Tele.getInstance().setTele1();
                        connectIpcamera();
                        rlIpControl.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_tele2:
                        stopIpcamera();
                        Tele.getInstance().setTele2();
                        connectIpcamera();
                        rlIpControl.setVisibility(View.GONE);
                        break;
                    case R.id.rb_tele3:
                        stopIpcamera();
                        Tele.getInstance().setTele3();
                        connectIpcamera();
                        rlIpControl.setVisibility(View.GONE);
                        break;
                    case R.id.rb_tele4:
                        stopIpcamera();
                        Tele.getInstance().setTele4();
                        connectIpcamera();
                        rlIpControl.setVisibility(View.GONE);
                        break;
                }
            }
        });

        controlCircleView.setCallback(new CircleViewByImage.ActionCallback() {
            @Override
            public void forwardMove() {//上
                sendLocal("2");
            }

            @Override
            public void backMove() {//下
                sendLocal("8");
            }

            @Override
            public void leftMove() {//左
                sendLocal("4");
            }

            @Override
            public void rightMove() {
                sendLocal("6");
            }

            @Override
            public void actionUp() {//**
                sendLocal("5");
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

        connectIpcamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAccept = false;
        NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(), ContentCommon.CMD_PTZ_LEFT_RIGHT_STOP);
        NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(), ContentCommon.CMD_PTZ_UP_DOWN_STOP);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UdpService.class));
        stopIpcamera();
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
        sendBroadcast(new Intent(AppConstants.NET_LOONGGG_EXITAPP));
    }

    /**
     * 初始化camera
     */
    private void initCamera() {
        BridgeService.setAddCameraInterface(this);
        BridgeService.setCallBackMessage(this);
        BridgeService.setIpcamClientInterface(this);
        NativeCaller.Init();
    }

    private void connectIpcamera() {
        Print.e("连接 " + Tele.getInstance().getPwd());
        new Thread(new StartPPPPThread(Tele.getInstance().getUser(), Tele.getInstance().getPwd(), Tele.getInstance().getDid())).start();
    }

    private void priviewIpcamera() {
        Print.e("预览相机");
        BridgeService.setPlayInterface(this);
        NativeCaller.StartPPPPLivestream(Tele.getInstance().getDid(), 10, 1);//确保不能重复start
        NativeCaller.PPPPGetSystemParams(Tele.getInstance().getDid(), ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
    }

    private void stopIpcamera() {
        Print.e("stop相机");
        NativeCaller.StopPPPPLivestream(Tele.getInstance().getDid());
        NativeCaller.StopPPPP(Tele.getInstance().getDid());
    }


    @OnClick({R.id.tv_drive, R.id.tv_control, R.id.ib_hori_tour, R.id.ib_vert_tour,
            R.id.y1,
            R.id.y7,
            R.id.y3,
            R.id.y9,
            R.id.ff,
            R.id.rr,
            R.id.zz,
            R.id.xx,
            R.id.cc,
            R.id.l,
            R.id.j,
            R.id.w,
            R.id.s,
            R.id.a,
            R.id.i,
            R.id.m,
            R.id.k,
            R.id.b,
            R.id.n,
            R.id.p,
            R.id.o,
            R.id.g,
            R.id.h
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_drive:
                tvDrive.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                tvControl.setBackgroundColor(getResources().getColor(R.color.task_deep));

                reDrive.setVisibility(View.VISIBLE);
                reControl.setVisibility(View.GONE);
                break;
            case R.id.tv_control:
                tvControl.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                tvDrive.setBackgroundColor(getResources().getColor(R.color.task_deep));

                reDrive.setVisibility(View.GONE);
                reControl.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_hori_tour:
                if (isLeftRight) {
                    ibHoriTour.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                    isLeftRight = false;
                    NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(), ContentCommon.CMD_PTZ_LEFT_RIGHT_STOP);
                } else {
                    ibHoriTour.setBackgroundColor(getResources().getColor(R.color.navajowhite));
                    isLeftRight = true;
                    NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(), ContentCommon.CMD_PTZ_LEFT_RIGHT);
                }
                break;
            case R.id.ib_vert_tour:
                if (isUpDown) {
                    ibVertTour.setBackgroundColor(getResources().getColor(R.color.task_deepblue));
                    isUpDown = false;
                    NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(), ContentCommon.CMD_PTZ_UP_DOWN_STOP);
                } else {
                    ibVertTour.setBackgroundColor(getResources().getColor(R.color.navajowhite));
                    isUpDown = true;
                    NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(), ContentCommon.CMD_PTZ_UP_DOWN);

                }
                break;
            case R.id.y1:
                sendLocal("1");
                break;
            case R.id.y7:
                sendLocal("7");
                break;
            case R.id.y3:
                sendLocal("3");
                break;
            case R.id.y9:
                sendLocal("9");
                break;
            case R.id.ff:
                if (ff.isChecked()) {
                    sendLocal("F");
                } else {
                    sendLocal("f");
                }
                break;
            case R.id.rr:
                if (rr.isChecked()) {
                    sendLocal("R");
                } else {
                    sendLocal("r");
                }
                break;
            case R.id.zz:
                if (zz.isChecked()) {
                    sendLocal("Z");
                } else {
                    sendLocal("z");
                }
                break;
            case R.id.xx:
                if (xx.isChecked()) {
                    sendLocal("X");
                } else {
                    sendLocal("x");
                }
                break;
            case R.id.cc:
                if (cc.isChecked()) {
                    sendLocal("C");
                } else {
                    sendLocal("c");
                }
                break;
            case R.id.l:
                sendLocal("l");
                break;
            case R.id.j:
                sendLocal("j");
                break;
            case R.id.w:
                sendLocal("w");
                break;
            case R.id.s:
                sendLocal("s");
                break;
            case R.id.a:
                sendLocal("a");
                break;
            case R.id.i:
                sendLocal("i");
                break;
            case R.id.m:
                sendLocal("m");
                break;
            case R.id.k:
                sendLocal("k");
                break;
            case R.id.b:
                sendLocal("b");
                break;
            case R.id.n:
                sendLocal("n");
                break;
            case R.id.p:
                sendLocal("p");
                break;
            case R.id.o:
                sendLocal("o");
                break;
            case R.id.g:
                sendLocal("g");
                break;
            case R.id.h:
                sendLocal("h");
                break;
        }
    }

    private void sendLocal(byte[] bytes) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("bytes", bytes);
        mLbmManager.sendBroadcast(intent);
    }

    private void sendLocal(String order) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("order", order);
        mLbmManager.sendBroadcast(intent);
    }


    //******************************相机连接后回调*******************
    @Override
    public void callBackCameraParamNotify(String did, int resolution, int brightness, int contrast, int hue, int saturation, int flip, int mode) {
//        Print.e("设备返回的参数" + resolution + "," + brightness + "," + contrast + "," + hue + "," + saturation + "," + flip + "," + mode);

    }


    @Override
    public void callBackVideoData(byte[] videobuf, int h264Data, int len, int width, int height) {
//        Print.e("callBackVideoData 视频数据流回调");
//        Print.e("底层返回数据" + "videobuf:" + videobuf + "--" + "h264Data" + h264Data + "len" + len + "width" + width + "height" + height);
        if (!bDisplayFinished)
            return;
        bDisplayFinished = false;

//        mVideodata = RotateUtils.rotateYUV420Degree90(videobuf, width, height);
        mVideodata = videobuf;

        Message msg = new Message();
        if (h264Data == 1) { // H264

            nVideoWidths = width;//1920
            nVideoHeights = height;//1080
//            nVideoWidths = height;//1920
//            nVideoHeights = width;//1080
            isH264 = true;
            msg.what = 1;
        }
        mDrawHandler.sendMessage(msg);
    }

    @Override
    public void callBackMessageNotify(String did, int msgType, int param) {

        Print.e("连接通知 did: " + did + " msgType: " + msgType + " param: " + param);
    }

    @Override
    public void callBackAudioData(byte[] pcm, int len) {
        Print.e("callBackAudioData");
        Print.e("AudioData: len :+ " + len);
    }

    @Override
    public void callBackH264Data(byte[] h264, int type, int size) {
        Print.e("callBackH264Data");
        Print.e("CallBack_H264Data" + " type:" + type + " size:" + size);
    }

    @Override
    public void UDPAcceptMessage(String content) {
        if (isAccept) {
            showToast(content);
        }
    }

    //**************************连接*********************
    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Print.e("连接状态回调... type : " + type);
        Bundle bd = new Bundle();
        Message msg = PPPPMsgHandler.obtainMessage();
        msg.what = type;
        bd.putInt(STR_MSG_PARAM, param);
        bd.putString(STR_DID, did);
        msg.setData(bd);
        PPPPMsgHandler.sendMessage(msg);
    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {
        Print.e("相机连接后 did : " + did);
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {
        Print.e("did : " + did + " ,user1 : " + user1 + " ,pwd1 : " + pwd1 + " ,user2 : " + user2 + " ,pwd : " + pwd2 +
                " ,user3 : " + user3 + " ,pwd3 : " + pwd3);
    }

    @Override
    public void CameraStatus(String did, int status) {
        Print.e("相机连接状态 did ： " + did + " status : " + status);
    }

    @Override
    public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
        Print.e("搜索出来的 strDeviceID : " + strDeviceID);
    }

    //*********************************setCallBackMessage***************
    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {
        Print.e("setCallBackMessage的回调 did : " + did + " 连接相机的信息参数 ");
    }


    class StartPPPPThread implements Runnable {

        private String mStrUser, mStrPwd, mStrDID;

        public StartPPPPThread(String strUser, String strPwd, String strDID) {
            this.mStrUser = strUser;
            this.mStrPwd = strPwd;
            this.mStrDID = strDID;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);

                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mStrDID.toLowerCase().startsWith("vsta")) {
                    NativeCaller.StartPPPPExt(mStrDID, mStrUser, mStrPwd, 1, "",
                            "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK");
                } else {
                    NativeCaller.StartPPPP(mStrDID, mStrUser, mStrPwd, 1, "");
                }

            } catch (Exception e) {

            }
        }
    }

    private class SearchThread implements Runnable {
        @Override
        public void run() {
            NativeCaller.StartSearch();
        }
    }


}
