package com.example.explosiverobot.view.surface;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.explosiverobot.listener.DrawInterface;
import com.example.explosiverobot.modle.Spot;
import com.example.explosiverobot.service.DrawingThread;


public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder mHolder; // 用于控制SurfaceView

    private DrawingThread mDrawingThread;

    private float cx = 50;      //圆点默认X坐标
    private float cy = 50;      //圆点默认Y坐标

    private int screenW;        //屏幕宽度
    private int screenH;        //屏幕高度

    private DrawInterface mDrawInterface;

    public DrawSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHolder = getHolder(); // 获得SurfaceHolder对象
        mHolder.addCallback(this); // 为SurfaceView添加状态监听

        mHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.mDrawInterface = drawInterface;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        screenW = getWidth();
        screenH = getHeight();

        mDrawingThread = new DrawingThread(this, mHolder, screenW, screenH);
        mDrawingThread.start(); // 启动线程
        mDrawingThread.setDrawInterface(mDrawInterface);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDrawingThread.quit();
        mDrawingThread = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                cx = (int) event.getX();
//                cy = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                cx = (int) event.getX();
                cy = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                cx = (int) event.getX();
//                cy = (int) event.getY();
                break;
        }

        mDrawingThread.setSpot(new Spot(cx, cy));
        return true;
    }


    public void clear() {
        if (mDrawingThread != null) {
            mDrawingThread.clearDraw();
        }
    }


}
