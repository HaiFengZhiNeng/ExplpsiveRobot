package com.example.explosiverobot.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.explosiverobot.R;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.base.config.AppConstants;
import com.example.explosiverobot.base.config.ContentCommon;
import com.example.explosiverobot.ipcamera.MyRender;
import com.example.explosiverobot.listener.DrawInterface;
import com.example.explosiverobot.modle.Tele;
import com.example.explosiverobot.receiver.UDPAcceptReceiver;
import com.example.explosiverobot.service.BridgeService;
import com.example.explosiverobot.util.GpsUtils;
import com.example.explosiverobot.view.surface.DrawSurfaceView;
import com.seabreeze.log.Print;

import butterknife.BindView;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class TaskActivity extends BaseActivity implements AMapLocationListener,
        BridgeService.AddCameraInterface,
        BridgeService.CallBackMessageInterface,
        BridgeService.IpcamClientInterface,
        LocationSource,
        BridgeService.PlayInterface,
        UDPAcceptReceiver.UDPAcceptInterface,
        DrawInterface {

    private static final String STR_MSG_PARAM = "msgparam";
    private static final String STR_DID = "did";

    @BindView(R.id.gl_surface_view)
    GLSurfaceView glSurfaceView;
    @BindView(R.id.tv_model)
    TextView tvModel;
    @BindView(R.id.tv_map)
    TextView tvMap;
    @BindView(R.id.re_model)
    RelativeLayout reModel;
    @BindView(R.id.map_view)
    MapView mapView;
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
    @BindView(R.id.draw_suface_view)
    DrawSurfaceView mDrawSurfaceView;
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
    @BindView(R.id.drive_sole)
    LinearLayout driveSole;
    @BindView(R.id.ib_hori_tour)
    ImageButton ibHoriTour;
    @BindView(R.id.ib_vert_tour)
    ImageButton ibVertTour;
    @BindView(R.id.rl_ip_control)
    RelativeLayout rlIpControl;

    private MyRender myRender;

    private LocalBroadcastManager mLbmManager;
    private boolean isAccept;
    //map
    private AMap aMap;
    public static final int ZOOM = 15;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private boolean isFirstIn = true;

    private LatLng lastLatLng;
    //udp
    private UDPAcceptReceiver mUdpAcceptReceiver;
    //数据视频流回掉
    private boolean bDisplayFinished = true;
    private byte[] mVideodata = null;
    public int nVideoWidths = 0;
    public int nVideoHeights = 0;
    public boolean isH264 = false;//是否是H264格式标志

    private boolean isDodge;//自动避让

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

    //连接状态
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
                            tag = 2;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
                            Print.e(getString(R.string.pppp_status_connect_failed));
                            showToast(getString(R.string.pppp_status_connect_failed));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_DISCONNECT://4
                            Print.e(getString(R.string.pppp_status_disconnect));
                            showToast(getString(R.string.pppp_status_disconnect));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_INITIALING://1
                            Print.e(getString(R.string.pppp_status_initialing));
                            showToast(getString(R.string.pppp_status_initialing));
                            tag = 2;
                            break;
                        case ContentCommon.PPPP_STATUS_INVALID_ID://5
                            Print.e(getString(R.string.pppp_status_invalid_id));
                            showToast(getString(R.string.pppp_status_invalid_id));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
                            //摄像机在线之后读取摄像机类型
                            String cmd = "get_status.cgi?loginuse=admin&loginpas=" + Tele.getInstance().getPwd() + "&user=admin&pwd=" + Tele.getInstance().getPwd();
                            NativeCaller.TransferMessage(did, cmd, 1);
                            Print.e(getString(R.string.pppp_status_online));
                            showToast(getString(R.string.pppp_status_online));
                            tag = 1;
                            priviewIpcamera();
                            break;
                        case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
                            Print.e(getString(R.string.device_not_on_line));
                            showToast(getString(R.string.device_not_on_line));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
                            Print.e(getString(R.string.pppp_status_connect_timeout));
                            showToast(getString(R.string.pppp_status_connect_timeout));
                            tag = 0;
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
                            Print.e(getString(R.string.pppp_status_pwd_error));
                            showToast(getString(R.string.pppp_status_pwd_error));
                            tag = 0;
                            break;
                        default:
                            Print.e(getString(R.string.pppp_status_unknown));
                            showToast(getString(R.string.pppp_status_unknown));
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

        initCamera();

        myRender = new MyRender(glSurfaceView);
        glSurfaceView.setRenderer(myRender);

        if (!GpsUtils.isOPen(this)) {
            GpsUtils.openGPS(this);
        }
        initMap(savedInstanceState);
        mDrawSurfaceView.setDrawInterface(this);
    }


    @Override
    protected void initData() {
        Tele.getInstance().setTele1();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        isAccept = true;
        mUdpAcceptReceiver = new UDPAcceptReceiver(this);
        IntentFilter intentFilter = new IntentFilter(AppConstants.UDP_ACCEPT_ACTION);
        mLbmManager.registerReceiver(mUdpAcceptReceiver, intentFilter);

        connectIpcamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onPause();
        isAccept = false;
    }

    @Override
    protected void onDestroy() {
        stopIpcamera();
        super.onDestroy();
        mapView.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setLocationSource(this);
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
            myLocationStyle.strokeColor(Color.BLUE);
            myLocationStyle.strokeWidth(2);
            aMap.setMyLocationStyle(myLocationStyle);
            UiSettings settings = aMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);
            settings.setCompassEnabled(false);

            aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
            aMap.setMyLocationEnabled(true);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));

            aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
        }
    }

    @OnClick({R.id.tv_model, R.id.tv_map, R.id.tv_drive, R.id.tv_control, R.id.tv_inspect,
            R.id.ic_front_upper, R.id.ic_front_lower, R.id.ic_after_upper, R.id.ic_after_lower,
            R.id.tv_speed_high, R.id.tv_speed_medium, R.id.tv_speed_low,
            R.id.ib_hori_tour, R.id.ib_vert_tour})
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
                    NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(),ContentCommon.CMD_PTZ_UP_DOWN_STOP);
                } else {
                    ibVertTour.setBackgroundColor(getResources().getColor(R.color.navajowhite));
                    isUpDown = true;
                    NativeCaller.PPPPPTZControl(Tele.getInstance().getDid(),ContentCommon.CMD_PTZ_UP_DOWN);
                }
                break;
        }
    }

    private void choiceTop(boolean b) {
        tvModel.setTextColor(b ? getResources().getColor(R.color.color_black) : getResources().getColor(R.color.color_white));
        tvModel.setBackgroundColor(b ? getResources().getColor(R.color.task_while) : getResources().getColor(R.color.task_deep));
        reModel.setVisibility(b ? View.VISIBLE : View.GONE);
        tvMap.setTextColor(!b ? getResources().getColor(R.color.color_black) : getResources().getColor(R.color.color_white));
        tvMap.setBackgroundColor(!b ? getResources().getColor(R.color.task_while) : getResources().getColor(R.color.task_deep));
        mapView.setVisibility(!b ? View.VISIBLE : View.GONE);
    }

    private void sendLocal(byte[] bytes) {
        Intent intent = new Intent(AppConstants.UDP_SEND_ACTION);
        intent.putExtra("bytes", bytes);
        mLbmManager.sendBroadcast(intent);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                String address = aMapLocation.getAddress();

                if (isFirstIn) {
                    lastLatLng = new LatLng(latitude, longitude);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(lastLatLng));
                    mListener.onLocationChanged(aMapLocation);
                    isFirstIn = false;

                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Print.e(errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            setLocationClient();
        }
    }

    private void setLocationClient() {
        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
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
        Print.e("callBackSearchResultData strDeviceID : " + strDeviceID);
    }

    //*********************************setCallBackMessage***************
    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {
        Print.e("setCallBackMessage的回调 did : " + did + " 连接相机的信息参数 ");
    }

    @Override
    public void rotatioCallbackn(double changeCistance, double rotation2, double rotation3) {
//        Print.e("(" + rotation1 + ", " + rotation2 + ", " + rotation3 + ")");

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

}
