package com.example.explosiverobot.view.weiget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/11/12.
 */

public class MyImageView extends ImageView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private Context context;

    private GestureDetector detector;

    private float downX;
    private float downY;
    private float offsetX;
    private float offsetY;
    private float offsetsByX;
    private float offsetsByY;

    private List<File> fileList;
    private int total;
    private int index;

    private static final int MSG_R = 0;
    private static final int MSG_L = 1;
    private static final int MSG_T = 2;
    private static final int MSG_B = 3;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_R:
                    if(index < total){
                        index ++;
                        load();
                    }
                    break;
                case MSG_L:
                    if(index > 0){
                        index --;
                        load();
                    }
                    break;
                case MSG_T:
                    Print.e("t");
                    break;
                case MSG_B:
                    Print.e("b");
                    break;
            }
        }
    };

    public MyImageView(Context context) {
        super(context);
        init(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        detector = new GestureDetector(context, this);
        detector.setOnDoubleTapListener(this);

    }

    public void load(File url) {
        fileList = new ArrayList<>();

        if(url.exists() && !url.isFile()){

            File[] files = url.listFiles();
            for(int i =0; i < files.length;i++){
                File file = files[i];
                if(file.getAbsolutePath().endsWith(".jpg")){
                 fileList.add(file);
                }
            }
        }
        if(fileList != null && fileList.size() > 0) {
            index = 0;
            total = fileList.size();
            load();
        }
    }

    private void load() {
        Glide.with(context).load(fileList.get(index)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().centerCrop().override(300, 300).into(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = downX - event.getRawX();
                offsetY = downY - event.getRawY();

                offsetsByX += offsetX;
                offsetsByY += offsetY;

                if (offsetsByX > 200 || offsetsByY > 200) {

                    int orientation = getOrientation(offsetsByX, offsetsByY);
                    switch (orientation) {
                        case 'r'://左
                            offsetsByX = 0;
                            offsetsByY = 0;
                            downX = event.getRawX();
                            downY = event.getRawY();

                            mHandler.sendEmptyMessage(MSG_R);
                            break;
                        case 'l'://右
                            offsetsByX = 0;
                            offsetsByY = 0;
                            downX = event.getRawX();
                            downY = event.getRawY();
                            mHandler.sendEmptyMessage(MSG_L);
                            break;
                        case 't':
                            offsetsByX = 0;
                            offsetsByY = 0;
                            downX = event.getRawX();
                            downY = event.getRawY();
                            mHandler.sendEmptyMessage(MSG_T);
                            break;
                        case 'b':
                            offsetsByX = 0;
                            offsetsByY = 0;
                            downX = event.getRawX();
                            downY = event.getRawY();
                            mHandler.sendEmptyMessage(MSG_B);
                            break;
                    }
                }
        }
        return true;
    }

    private int getOrientation(double dx, double dy) {
        if (Math.abs(dx) < Math.abs(dy)) {
            return dy > 0 ? 'b' : 't';
        } else {
            return dx > 0 ? 'r' : 'l';
        }
    }


    // 获取屏幕的宽度
    @SuppressWarnings("deprecation")
    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    // 获取屏幕的高度
    @SuppressWarnings("deprecation")
    public int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Print.e("onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Print.e("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Print.e("onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Print.e("onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Print.e("onLongPress");

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Print.e("onFling");
        return false;
    }

    /*************************************/
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        Print.e("onSingleTapConfirmed");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Print.e("onDoubleTap");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        Print.e("onDoubleTapEvent");
        return false;
    }
}
