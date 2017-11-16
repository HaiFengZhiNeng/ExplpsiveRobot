package com.example.scout.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.scout.R;

/**
 * Created by zhangyuanyuan on 2017/10/31.
 */

public class CircleViewByImage extends FrameLayout {

    private static final int BACKGROUND = 0;
    private static final int FOREGROUND = 1;

    private Context mContext;

    private int center;//大小圆心
    private int mInnerCircleRadius;//小圆半径
    private Paint innerPaint;//画内部小圆的画笔
    private float SmallRockerCircleX;//小圆圆心-动态改变
    private float SmallRockerCircleY;//小圆圆心-动态改变
    private float RockerCircleX;//大圆圆心x坐标-不可改变
    private float RockerCircleY;//大圆圆心y坐标-不可y改变
    private float RockerCircleR;//大圆半径

    private boolean isMoveToCenter;//手指是否抬起，即是否需要移动到中心点
    private OnTouchListener mBackgroundListener = new InnerCircleTouchListener();
    private ActionCallback callback;

    private float firstX;//按下去时x的坐标
    private float firstY;//按下去时y的坐标
    private boolean isShortTime;

    private long delayYime = 1000;
    
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isShortTime = false;//用于判断是否在短时间内点击了小圆两次
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (lefting) {
                        if (callback != null) {
                            callback.leftMove();
                        }
                        mHandler.sendEmptyMessageDelayed(0, delayYime);
                    }
                    break;
                case 1:
                    if (uping) {
                        if (callback != null) {
                            callback.forwardMove();;
                        }
                        mHandler.sendEmptyMessageDelayed(1, delayYime);
                    }
                    break;
                case 2:
                    if (righting) {
                        if (callback != null) {
                            callback.rightMove();
                        }
                        mHandler.sendEmptyMessageDelayed(2, delayYime);
                    }
                    break;
                case 3:
                    if (downing) {
                        if (callback != null) {
                            callback.backMove();
                        }
                        mHandler.sendEmptyMessageDelayed(3, delayYime);
                    }
                    break;
            }
        }
    };
    private final int mBackgroundId;
    private final int foregroundId;

    private boolean uping;
    private boolean downing;
    private boolean lefting;
    private boolean righting;

    public CircleViewByImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleViewByImage);
        mBackgroundId = array.getResourceId(R.styleable.CircleViewByImage_background, R.mipmap.control_background);
        foregroundId = array.getResourceId(R.styleable.CircleViewByImage_foreground, R.mipmap.control_foreground);
        array.recycle();

        initiation();

        setWillNotDraw(false);//如果onDraw不执行，运行该还代码或者重写dispatchDraw替换onDraw
    }

    private void initiation() {
        //放置大圆，如果自定义大圆，可以参考【绘制小圆】的做法，绘制只需要一个半径，和一只画笔，并在onDraw进行简单处理
        ImageView imageViewB = new ImageView(mContext);
        imageViewB.setImageResource(mBackgroundId);
        imageViewB.setOnTouchListener(mBackgroundListener);
        addView(imageViewB, BACKGROUND);

        Drawable bgDrawable = getResources().getDrawable(mBackgroundId);
        center = bgDrawable.getMinimumWidth() / 2;//得到大圆半径

        //【绘制小圆】，通过给定的图片来绘制一模一样的小圆
        Drawable drawable = getResources().getDrawable(foregroundId);//小圆png图片
        mInnerCircleRadius = drawable.getMinimumWidth() / 2;//通过png图片获取要画的小圆半径 r

        //准备小圆画笔
        innerPaint = new Paint();
        innerPaint.setColor(Color.argb(250, 216, 216, 216));//设置颜色
        innerPaint.setAntiAlias(true);//设置抗锯齿
        innerPaint.setStyle(Paint.Style.FILL);//设置绘制风格，填充模式

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        center = getWidth() / 2;//在这里得到大圆半径，有可能导致绘图时，小圆在左上角而不是在中间

        //大圆圆心坐标
        RockerCircleX = center;
        RockerCircleY = center;

        //第一次绘制小圆时坐标点
        SmallRockerCircleX = center;
        SmallRockerCircleY = center;

        //调节此处，当手指处于控件外围时，小球位置的最大区域，同时也调节灵敏度
        RockerCircleR = center - mInnerCircleRadius;
        //center - mInnerCircleRadius 小圆最大移动距离是内切大圆

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isMoveToCenter) {
            //手指抬起，小圆返回圆心
            canvas.drawCircle(center, center, mInnerCircleRadius, innerPaint);
        } else {
            //手指一动，小圆随手指一动
            canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY, mInnerCircleRadius, innerPaint);
        }
    }

    private class InnerCircleTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();

            try {
                    gestures(action, x, y);
            } catch (Exception e) {
                throw new NullPointerException("must implement : "+ActionCallback.class.getSimpleName());
            }
            return true;
        }
    }

    private void gestures(int action, float x, float y) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstX = x;
                firstY = y;
                isShortTime = true;
                handler.sendEmptyMessageDelayed(0, delayYime);//500ms后再点击则判断为此次动作为非点击动作
            case MotionEvent.ACTION_MOVE:
                // 当触屏区域不在活动范围内
                if (Math.sqrt(Math.pow((RockerCircleX - (int) x), 2) + Math.pow((RockerCircleY - (int) y), 2)) >= RockerCircleR) {
                    //得到摇杆与触屏点所形成的角度
                    double tempRad = getRad(RockerCircleX, RockerCircleY, x, y);
                    //保证内部小圆运动的长度限制
                    //获取圆周运动的X坐标
                    SmallRockerCircleX = (float) (RockerCircleR * Math.cos(tempRad)) + RockerCircleX;
                    //获取圆周运动的Y坐标
                    SmallRockerCircleY = (float) (RockerCircleR * Math.sin(tempRad)) + RockerCircleY;

                    gestureAction(MyAction.OUTSIDE_MOVE, tempRad);
                } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可
                    SmallRockerCircleX = (int) x;
                    SmallRockerCircleY = (int) y;
                    gestureAction(MyAction.INNER_MOVE, 0);
                }
                circleMoveToCenter(false);
                break;
            case MotionEvent.ACTION_UP:
                //当释放按键时摇杆要恢复摇杆的位置为初始位置
                circleMoveToCenter(true);
                gestureAction(MyAction.ACTION_UP, 0);
                if (isInnerClick(firstX, firstY, x, y) && isShortTime) {
                    gestureAction(MyAction.ACTION_CLICK, 0);
                }
                break;
        }
    }

    /**
     * 判断手指按下抬起的坐标是否在小圆内
     * @param param :firstX,firstY,endX,endY
     * @return true：在小圆按下抬起；false：不在小圆按下或抬起
     */
    private boolean isInnerClick(float... param) {
        if (param == null) return false;
        for (float v : param) {
            if ((v <= center - mInnerCircleRadius || v >= center + mInnerCircleRadius)) {
                return false;
            }
        }
        return true;
    }

    /***
     * 得到两点之间的弧度
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / xie;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }



    private void gestureAction(MyAction myAction, double rad) {
        switch (myAction) {
            case OUTSIDE_MOVE:
                double small = 0.75;
                double big = 2.35;
                if (rad >= big || rad <= -big) {//左
                    if(!lefting) {
                        callback.leftMove();
                        uping = false;
                        downing = false;
                        lefting = true;
                        righting = false;
                        mHandler.sendEmptyMessageDelayed(0, delayYime);
                    }
                } else if (rad > -big && rad < -small) {//上
                    if(!uping) {
                        callback.forwardMove();
                        uping = true;
                        downing = false;
                        lefting = false;
                        righting = false;
                        mHandler.sendEmptyMessageDelayed(1, delayYime);
                    }
                } else if (rad >= -small && rad <= small) {//右
                    if(!righting) {
                        callback.rightMove();
                        uping = false;
                        downing = false;
                        lefting = false;
                        righting = true;
                        mHandler.sendEmptyMessageDelayed(2, delayYime);
                    }
                } else {//下
                    if(!downing) {
                        callback.backMove();
                        uping = false;
                        downing = true;
                        lefting = false;
                        righting = false;
                        mHandler.sendEmptyMessageDelayed(3, delayYime);
                    }
                }
                break;
//            case INNER_MOVE:
//                callback.centerMove();
//                break;
//            case ACTION_CLICK:
//                callback.centerClick();
//                break;
            case ACTION_UP:
                mHandler.removeMessages(0);
                mHandler.removeMessages(1);
                mHandler.removeMessages(2);
                mHandler.removeMessages(3);
                callback.actionUp();
                uping = false;
                downing = false;
                lefting = false;
                righting = false;
                break;
        }
    }


    /**
     * 手指抬起后回归中心或手指的xy上
     */
    private void circleMoveToCenter(boolean isMoveToCenter) {
        this.isMoveToCenter = isMoveToCenter;
        invalidate();
    }

    public void setCallback(@NonNull ActionCallback callback) {
        this.callback = callback;
    }

    /**
     * 该接口回调用户操作，某一个方法可能会被频繁的调用，请自行做处理，比如判断时间差距
     */
    public interface ActionCallback {

        void forwardMove();//向前移动

        void backMove();//向后移动

        void leftMove();//左边移动

        void rightMove();//右边移动

//        void centerMove();//中心小范围移动

//        void centerClick();//中心点击

        void actionUp();//手指抬起
    }

    enum MyAction {
        OUTSIDE_MOVE, INNER_MOVE, ACTION_CLICK, ACTION_UP
    }
}
