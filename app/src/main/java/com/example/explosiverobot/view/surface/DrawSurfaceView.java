package com.example.explosiverobot.view.surface;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import com.example.explosiverobot.listener.DrawInterface;
import com.example.explosiverobot.modle.Spot;
import com.example.explosiverobot.service.DrawingThread;
import com.seabreeze.log.Print;


public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder mHolder; // 用于控制SurfaceView

    private DrawingThread mDrawingThread;

    private float cx = 50;      //圆点默认X坐标
    private float cy = 50;      //圆点默认Y坐标

    private int screenW;        //屏幕宽度
    private int screenH;        //屏幕高度

    private DrawInterface mDrawInterface;

    private VelocityTracker mTracker;
    private boolean isSlow;

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

        mDrawingThread = new DrawingThread(mContext, this, mHolder, screenW, screenH);
        mDrawingThread.start(); // 启动线程
        mDrawingThread.setDrawInterface(mDrawInterface);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Print.e("");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDrawingThread.save();
        mDrawingThread.quit();
        mDrawingThread = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                cx = (int) event.getX();
//                cy = (int) event.getY();
//                mDrawingThread.setSpot(new Spot(cx, cy));
                if (mTracker == null) {
                    mTracker = VelocityTracker.obtain();
                } else {
                    mTracker.clear();
                }
                isSlow = false;
                mTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.addMovement(event);
                mTracker.computeCurrentVelocity(1000);
                int xSpeed = (int) Math.abs(mTracker.getXVelocity());
                int ySpeed = (int) Math.abs(mTracker.getYVelocity());
                if(xSpeed < 200 && ySpeed < 200) {
                    isSlow = true;
                }else{
                    isSlow = false;
                }
                if(isSlow){
                    cx = (int) event.getX();
                    cy = (int) event.getY();
                    mDrawingThread.setSpot(new Spot(cx, cy));
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isSlow){
                    cx = (int) event.getX();
                    cy = (int) event.getY();
                    mDrawingThread.setSpotUp(new Spot(cx, cy));
                }
                if (mTracker != null) {
                    mTracker.recycle();
                    mTracker = null;
                    isSlow = false;
                }

                break;
        }

        return true;
    }

    public void clear() {
        if (mDrawingThread != null) {
            mDrawingThread.clearDraw();
        }
    }


    public void onReset() {
        mDrawingThread.reset();
    }
}
