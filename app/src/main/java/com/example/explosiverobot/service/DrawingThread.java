package com.example.explosiverobot.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.explosiverobot.R;
import com.example.explosiverobot.listener.DrawInterface;
import com.example.explosiverobot.modle.Spot;

import java.math.BigDecimal;

/**
 * Created by zhangyuanyuan on 2017/9/28.
 */

public class DrawingThread extends HandlerThread implements Handler.Callback {

    private static final int MSG_DRAW = 101;
    private int leftBase;
    private int topBase;
    private int rightBase;
    private int bottomBase;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mDrawingSurface;
    private Paint mPaint;
    private Handler mReceiver;

    // 定义一个记录图像是否开始渲染的旗帜
    private boolean mRunning;

    private int mScreenW;        //屏幕宽度
    private int mScreenH;        //屏幕高度

    private int basePosX;
    private int basePosY;

    private int groundY;

    private double shortProportion;
    private double longProportion;

    private int carHeight;
    private int carWidth;
    private Bitmap armBaseBitmap;
    private Rect mArmBaseDestRect;

    private Bitmap armFirstBitmap;
    private Matrix mFirstMatrix;
    private float mRotation1;
    private double firstDegreeMax;
    private double firstDegreeMin;

    private Bitmap armSecondBitmap;
    private Matrix mSecondMatrix;
    private float mRotation2;
    private double secondDegreeMax;
    private double secondDegreeMin;

    private Bitmap armEndBitmap;
    private Matrix mEndMatrix;
    private float mRotation3;
    private double endDegreeMax;
    private double endDegreeMin;

    private Spot braceSpot;
    private Spot triangleSpot;
    private double originalBraceDistance;

    private double myRotation;
    /**
     * 第一条线的长度
     */
    private int r0;
    /**
     * 第二条线的长度
     */
    private int r1;
    /**
     * 第三条线的长度
     */
    private int r2;
    /**
     * 定义触摸的范围
     */
    private int rtouch = 70;
    /**
     * 固定点坐标
     */
    private Spot originSpot;
    private Spot levelSpot;
    /**
     * 第一条线末端位置，第二条线的圆心
     */
    private Spot firstSpot;
    private Spot firstSpotTail;
    /**
     * 第二条线的圆心
     */
    private Spot secondSpot;
    /**
     * 第二条线末端位置，第三条线的圆心
     */
    private Spot endingSpot;
    /**
     * 第三条线的末端位置
     */
    private Spot touchSpot;
    private Spot mySpot;

    /**
     * 回调接口
     */
    private DrawInterface mDrawInterface;

    private boolean isDrawing;

    private double mCallRotation1;
    private double mCallRotation2;
    private double mCallRotation3;


    public DrawingThread(SurfaceView surfaceView, SurfaceHolder holder, int screenW, int screenH) {
        super("DrawingThread");
        mSurfaceView = surfaceView;
        mDrawingSurface = holder;
        mScreenW = screenW;
        mScreenH = screenH;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        mPaint.setTextSize(30);
        mPaint.setColor(Color.rgb(255, 203, 15));
        mPaint.setTextAlign(Paint.Align.LEFT);

        carWidth = 400;
        carHeight = 100;
        basePosX = (int) (screenW * 0.3);
        basePosY = (int) (screenH * 0.7);

        originSpot = new Spot(basePosX, basePosY);
        levelSpot = new Spot(originSpot.getX() + carWidth, originSpot.getY());
        //基座
        armBaseBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_base);

        groundY = (int) (originSpot.getY() + armBaseBitmap.getHeight() * 5 / 6 + carHeight);

        leftBase = (int) (originSpot.getX() - armBaseBitmap.getWidth() * 2 / 3);
        topBase = (int) (originSpot.getY() - armBaseBitmap.getHeight() / 6);
        rightBase = (int) (originSpot.getX() + armBaseBitmap.getWidth() / 3);
        bottomBase = (int) (originSpot.getY() + armBaseBitmap.getHeight() * 5 / 6);
        mArmBaseDestRect = new Rect(leftBase, topBase, rightBase, bottomBase);

        //第一条机械臂
        armFirstBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_first);
        //第二条机械臂
        armSecondBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_second);
        //第三条机械臂
        armEndBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_end);

        firstDegreeMax = 60;
        firstDegreeMin = 15;
        secondDegreeMax = 180;
        secondDegreeMin = 100;
        endDegreeMax = 180;
        endDegreeMin = 45;

        mFirstMatrix = new Matrix();
        mSecondMatrix = new Matrix();
        mEndMatrix = new Matrix();

        r0 = armFirstBitmap.getHeight() * 4 / 5 - armSecondBitmap.getWidth() / 2;
        r1 = armSecondBitmap.getHeight() * 8 / 9 - armEndBitmap.getWidth() / 2;
        r2 = armEndBitmap.getHeight() - armEndBitmap.getWidth();


        firstSpot = new Spot(basePosX + r0 * Math.cos(degreeToRadian(firstDegreeMax)),
                basePosY - r0 * Math.sin(degreeToRadian(firstDegreeMax)));

        secondSpot = firstSpot;

        endingSpot = new Spot(firstSpot.getX() + r1 * Math.sin(degreeToRadian(270 - secondDegreeMax - firstDegreeMax)),
                firstSpot.getY() - r1 * Math.cos(degreeToRadian(270 - secondDegreeMax - firstDegreeMax)));

        mySpot = new Spot(0, 0);

        touchSpot = new Spot(endingSpot.getX() + r2, endingSpot.getY());

        shortProportion = 12.5 / 48.5;
        longProportion = 46.5 / 48.5;

        double d1 = r0 / 48.5 * 16.5;
        double d2 = r0 / 48.5 * 14;

        triangleSpot = belowLinePoint(originSpot, firstSpot, r0 * longProportion, r0 * shortProportion);

        braceSpot = new Spot((int) (originSpot.getX() + d1), (int) (originSpot.getY() + Math.sqrt(d1 * d1 - d2 * d2)));
        originalBraceDistance = getDistance(braceSpot, triangleSpot);

        firstSpotTail = angleAcquisitionPoint(originSpot, firstSpot, degreeToRadian(firstDegreeMax - firstDegreeMin));
    }

    private Spot belowLinePoint(Spot spot1, Spot spot2, double r1, double r2) {
        double[] intersectionTriangle = circleCircleIntersection(spot1, spot2, r1, r2);
        float[] intersectionFloatTriangle = new float[4];
        for (int i = 0; i < intersectionTriangle.length; i++) {
            BigDecimal b = new BigDecimal(Double.toString(intersectionTriangle[i]));
            intersectionFloatTriangle[i] = b.floatValue();
        }
        return  new Spot(intersectionFloatTriangle[0] > intersectionFloatTriangle[2] ? intersectionFloatTriangle[0] : intersectionFloatTriangle[2],
                intersectionFloatTriangle[1] > intersectionFloatTriangle[3] ? intersectionFloatTriangle[1] : intersectionFloatTriangle[3]);
    }

    /**
     * 绘制图形
     *
     * @param canvas
     */
    private void drawSpot(Canvas canvas, double rotation1, double rotation2, double rotation3, boolean isCorrect) {

        if (isCorrect) {

            mRotation1 = (float) (mRotation1 + radianToDegree(rotation1));
            mRotation2 = (float) (mRotation2 + radianToDegree(rotation2));
            mRotation3 = (float) (mRotation3 + radianToDegree(rotation3));
            myRotation = myRotation + rotation3;
        }


        mPaint.setColor(Color.MAGENTA);
        canvas.drawRect((float) leftBase, (float) topBase + armBaseBitmap.getHeight(),
                (float) rightBase + carWidth, (float) bottomBase + carHeight, mPaint);

        canvas.drawLine((float) braceSpot.getX(), (float) braceSpot.getY(), (float) triangleSpot.getX(), (float) triangleSpot.getY(), mPaint);
        //画基座
        mPaint.setColor(Color.WHITE);
        canvas.drawBitmap(armBaseBitmap, null, mArmBaseDestRect, mPaint);
        canvas.drawCircle(leftBase, topBase, rtouch, mPaint);

        //画第一条
        mPaint.setColor(Color.BLUE);
        mFirstMatrix.reset();
        float dxFirst = (float) (originSpot.getX() - armFirstBitmap.getWidth() / 2);
        float dyFirst = (float) (originSpot.getY() - armFirstBitmap.getHeight() * 4 / 5);
        mFirstMatrix.postTranslate(dxFirst, dyFirst);
        float pxFirst = (float) originSpot.getX();
        float pyFirst = (float) originSpot.getY();
        mFirstMatrix.postRotate(mRotation1, pxFirst, pyFirst);
        canvas.drawBitmap(armFirstBitmap, mFirstMatrix, mPaint);
        canvas.drawCircle(pxFirst, pyFirst, r0, mPaint);
        canvas.drawLine(pxFirst, pyFirst, (float) firstSpot.getX(), (float) firstSpot.getY(), mPaint);

        //画第二条
        mPaint.setColor(Color.RED);
        mSecondMatrix.reset();
        float dxSecond = (float) (secondSpot.getX() - armSecondBitmap.getWidth() / 2);
        float dySecond = (float) (secondSpot.getY() - armSecondBitmap.getHeight() * 8 / 9);
        mSecondMatrix.postTranslate(dxSecond, dySecond);
        float pxSecond = (float) secondSpot.getX();
        float pySecond = (float) secondSpot.getY();
        mSecondMatrix.postRotate(mRotation2, pxSecond, pySecond);
        canvas.drawBitmap(armSecondBitmap, mSecondMatrix, mPaint);
        canvas.drawCircle(pxSecond, pySecond, r1, mPaint);
        canvas.drawCircle((float) basePosX, (float) basePosY, rtouch, mPaint);
        canvas.drawLine(pxSecond, pySecond, (float) endingSpot.getX(), (float) endingSpot.getY(), mPaint);

        //画第三条
        mPaint.setColor(Color.GREEN);
        mEndMatrix.reset();
        float dxEnd = (float) (endingSpot.getX() - armEndBitmap.getWidth() / 2);
        float dyEnd = (float) (endingSpot.getY() - armEndBitmap.getHeight() * 14 / 16);
        mEndMatrix.postTranslate(dxEnd, dyEnd);
        float pxEnd = (float) endingSpot.getX();
        float pyEnd = (float) endingSpot.getY();
        mEndMatrix.postRotate(mRotation3, pxEnd, pyEnd);
        canvas.drawBitmap(armEndBitmap, mEndMatrix, mPaint);
        canvas.drawCircle(pxEnd, pyEnd, r2, mPaint);
        canvas.drawLine(pxEnd, pyEnd, (float) touchSpot.getX(), (float) touchSpot.getY(), mPaint);

        mPaint.setColor(Color.YELLOW);
        mySpot.setX(endingSpot.getX());
        mySpot.setY(endingSpot.getY() - r2);
        mySpot = angleAcquisitionPoint(endingSpot, mySpot, myRotation);
        canvas.drawCircle((float) mySpot.getX(), (float) mySpot.getY(), 50, mPaint);

        mPaint.setColor(Color.BLACK);
        canvas.drawCircle((float) touchSpot.getX(), (float) touchSpot.getY(), rtouch, mPaint);

        isDrawing = false;
    }

    private void drawSpot(Canvas canvas, Spot spot) {
        if (spot == null) {
            drawSpot(canvas, degreeToRadian(90 - firstDegreeMax), degreeToRadian(90 - firstDegreeMax), Math.PI / 2, true);
            return;
        }
        mCallRotation1 = 0.0;
        mCallRotation2 = 0.0;
        mCallRotation3 = 0.0;

        Spot temporaryFirstSpot = firstSpot;
        Spot temporarySecondSpot = secondSpot;
        Spot temporaryendingSpot = endingSpot;
        Spot temporaryTouchSpot = touchSpot;
        Spot temporaryMySpot = mySpot;
        double temporaryRotation = myRotation;

        double rotation1 = 0.0;
        double rotation2 = 0.0;
        double rotation3 = 0.0;
        //点击的点到第三条线末端的位置
        double distanceTouch = getDistance(spot, touchSpot);
        //点击的点到第二个圆中心点的距离
        double distanceMax = getDistance(spot, secondSpot);

        //触摸点再第三条线的触摸范围之内，且再最大半径以内
        if (distanceTouch < rtouch && distanceMax < r1 + r2 && distanceMax > rtouch) {
            //计算以第二圆和第三圆的交点
            double[] intersection = circleCircleIntersection(secondSpot, spot, r1, r2);
            if (intersection == null) {
                System.err.println("intersection null");
                return;
            }
            //double转换成float
            float[] intersectionFloat = new float[4];
            for (int i = 0; i < intersection.length; i++) {
                BigDecimal b = new BigDecimal(Double.toString(intersection[i]));
                intersectionFloat[i] = b.floatValue();
            }
            //得到两个点的坐标值
            Spot spot1 = new Spot(intersectionFloat[0], intersectionFloat[1]);
            Spot spot2 = new Spot(intersectionFloat[2], intersectionFloat[3]);
            //得到两点到上一次交点的距离
            double distance1 = getDistance(spot1, endingSpot);
            double distance2 = getDistance(spot2, endingSpot);
            //判断距离，取最小值
            if (distance1 > distance2) {
                endingSpot = spot2;
            } else {
                endingSpot = spot1;
            }

            rotation2 = pointAcquisitionAngle(secondSpot, temporaryendingSpot, endingSpot);
            rotation2 = positiveNegative(secondSpot, temporaryendingSpot, endingSpot) ? rotation2 : -rotation2;

            rotation3 = pointCrossAngle(temporaryendingSpot, temporaryTouchSpot, endingSpot, spot);

            mySpot.setX(endingSpot.getX());
            mySpot.setY(endingSpot.getY() - r2);
            mySpot = angleAcquisitionPoint(endingSpot, mySpot, rotation3 + myRotation);
            double distanceCheck1 = getDistance(spot, mySpot);

            myRotation = temporaryRotation;
            mySpot.setX(endingSpot.getX());
            mySpot.setY(endingSpot.getY() - r2);
            mySpot = angleAcquisitionPoint(endingSpot, mySpot, -rotation3 + myRotation);

            double distanceCheck2 = getDistance(spot, mySpot);
            if (distanceCheck1 < distanceCheck2) {
                rotation3 = rotation3;
            } else {
                rotation3 = -rotation3;
            }
            touchSpot = spot;

            mCallRotation1 = rotation1;
            mCallRotation2 = rotation2;
            mCallRotation3 = rotation3;

        } else if (distanceMax > r1 + r2 && distanceMax < r1 + r2 + rtouch) {//触摸点再最大半径之外再可控范围之内


            //交点 同心圆坐标求解
            double distance = getDistance(spot, secondSpot);
            endingSpot = getOuterCircle(secondSpot, distance, r1, spot);
            //尾点
            touchSpot = getOuterCircle(secondSpot, r1, r2 + r1, endingSpot);

            rotation2 = pointAcquisitionAngle(secondSpot, temporaryendingSpot, endingSpot);
            rotation2 = positiveNegative(secondSpot, temporaryendingSpot, endingSpot) ? rotation2 : -rotation2;

            rotation3 = pointCrossAngle(temporaryendingSpot, temporaryTouchSpot, endingSpot, touchSpot);

            mySpot.setX(endingSpot.getX());
            mySpot.setY(endingSpot.getY() - r2);
            mySpot = angleAcquisitionPoint(endingSpot, mySpot, rotation3 + myRotation);
            double distanceCheck1 = getDistance(touchSpot, mySpot);

            myRotation = temporaryRotation;
            mySpot.setX(endingSpot.getX());
            mySpot.setY(endingSpot.getY() - r2);
            mySpot = angleAcquisitionPoint(endingSpot, mySpot, -rotation3 + myRotation);

            double distanceCheck2 = getDistance(touchSpot, mySpot);
            if (distanceCheck1 < distanceCheck2) {
                myRotation = temporaryRotation;
                rotation3 = rotation3;
            } else {
                myRotation = temporaryRotation;
                rotation3 = -rotation3;
            }

            mCallRotation1 = rotation1;
            mCallRotation2 = rotation2;
            mCallRotation3 = rotation3;

        } else if (distanceMax < rtouch) {//接近第二个圆  no

            if(firstSpotTail.getY() < spot.getY()){
                spot = firstSpotTail;
            }else {

                //触摸点到中心点的距离
                double distanceb = getDistance(originSpot, spot);
                //由触摸点得到在中心圆上实际点的坐标
                Spot temporarySpot = getOuterCircle(originSpot, distanceb, r0, spot);
                //由中心圆实际点的坐标计算出应该旋转的角度
                double rotation = pointAcquisitionAngle(originSpot, firstSpot, temporarySpot);
                //触摸点与原点的差值
                double dx = spot.getX() - firstSpot.getX();
                double dy = spot.getY() - firstSpot.getY();
                //差值大于1再进行判断
                if (Math.abs(dx) > 1 && Math.abs(dy) > 1) {
                    //判断触摸的方向
                    int orientation = getOrientation(dx, dy);
                    //判断旋转方向
                    rotation = rotationDirection(spot, originSpot, firstSpot, rotation, orientation);

                    //由角度计算出个点位置
//                Spot testSpor = angleAcquisitionPoint(originSpot, firstSpot, degreeToRadian(5));
                    firstSpot = angleAcquisitionPoint(originSpot, firstSpot, rotation);
                    secondSpot = angleAcquisitionPoint(originSpot, secondSpot, rotation);
                    endingSpot = angleAcquisitionPoint(originSpot, endingSpot, rotation);
                    touchSpot = angleAcquisitionPoint(originSpot, touchSpot, rotation);

                    rotation1 = rotation;
                    rotation2 = rotation;
                    rotation3 = rotation;

                    mCallRotation1 = rotation1;
                    mCallRotation2 = 0.0;
                    mCallRotation3 = 0.0;
                }
            }
        }
        myRotation = temporaryRotation;
        mySpot = temporaryMySpot;

        if (pointToYDistance(touchSpot.getY(), groundY)) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        if (pointToYDistance(endingSpot.getY(), groundY)) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        if (pointToYDistance(secondSpot.getY(), groundY)) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        if (pointToYDistance(firstSpot.getY(), groundY)) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        double firstDegree = radianToDegree(pointAcquisitionAngle(originSpot, firstSpot, levelSpot));
        if (firstDegree < firstDegreeMin) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }
        if (firstDegree > firstDegreeMax + 1) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        //originSpot连接firstSpot的直线在圆心为secondSpot上的交点
        Spot secondNeedleSpotRes = lineCircleIntersection(secondSpot,
                new Spot(originSpot.getX(), 2 * secondSpot.getY() - originSpot.getY()), r1);
        //android上准确点的位置
        Spot secondNeedleSpotRes0 = new Spot(secondNeedleSpotRes.getX(),
                secondSpot.getY() - (secondNeedleSpotRes.getY() - secondSpot.getY()));
        //求出圆心secondSpot圆上点homeopathicNeedleSpotRes0顺时针旋转40度的点
        Spot secondNeedleSpot40 = angleAcquisitionPoint(secondSpot, secondNeedleSpotRes0, degreeToRadian((secondDegreeMax - secondDegreeMin) / 2));
        double secondDegree = radianToDegree(pointAcquisitionAngle(secondSpot, endingSpot, secondNeedleSpot40));
        if (secondDegree > (secondDegreeMax - secondDegreeMin) / 2 + 1) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        //secondSpot连接endingSpot的直线在圆心为endingSpot上的交点
        Spot endNeedleSpotRes = lineCircleIntersection(endingSpot,
                new Spot(secondSpot.getX(), 2 * endingSpot.getY() - secondSpot.getY()), r2);
        Spot endNeedleSpotRes0 = new Spot(endNeedleSpotRes.getX(),
                endingSpot.getY() - (endNeedleSpotRes.getY() - endingSpot.getY()));
        Spot endNeedleSpotRes65 = angleAcquisitionPoint(endingSpot, endNeedleSpotRes0, degreeToRadian((endDegreeMax - endDegreeMin) / 2));
        double endDegree = radianToDegree(pointAcquisitionAngle(endingSpot, touchSpot, endNeedleSpotRes65));
        if (endDegree > (endDegreeMax - endDegreeMin) / 2) {
            firstSpot = temporaryFirstSpot;
            secondSpot = temporarySecondSpot;
            endingSpot = temporaryendingSpot;
            touchSpot = temporaryTouchSpot;
            drawSpot(canvas, 0, 0, 0, false);
            return;
        }

        triangleSpot = belowLinePoint(originSpot, firstSpot, r0 * longProportion, r0 * shortProportion);;
        double braceDistance = getDistance(braceSpot, triangleSpot);
        double changeCistance = originalBraceDistance - braceDistance;
        originalBraceDistance = braceDistance;


        if (mDrawInterface != null) {
            mDrawInterface.rotatioCallbackn(radianToDegree(mCallRotation1), mCallRotation2, mCallRotation3, changeCistance);
        }
        drawSpot(canvas, rotation1, rotation2, rotation3, true);
    }

    @Override
    protected void onLooperPrepared() {
        mReceiver = new Handler(getLooper(), this);
        // 开始渲染
        mRunning = true;
        mReceiver.sendEmptyMessage(MSG_DRAW);

    }

    @Override
    public boolean quit() {
        // 退出之前清除所有的消息
        mRunning = false;
        mReceiver.removeCallbacksAndMessages(null);

        return super.quit();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DRAW:
                if (!mRunning) return true;
                Spot spot = (Spot) msg.obj;
                if (spot != null) {
                    // 锁定 SurfaceView，并返回到要绘图的 Canvas
                    Canvas canvas = mDrawingSurface.lockCanvas();
                    if (canvas == null) {
                        break;
                    }
                    try {
                        synchronized (mDrawingSurface) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            drawSpot(canvas, spot);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // 解锁 Canvas，并渲染当前的图像
                        mDrawingSurface.unlockCanvasAndPost(canvas);
                        isDrawing = false;
                    }
                } else {
                    Canvas canvas = mDrawingSurface.lockCanvas();
                    try {
                        synchronized (mDrawingSurface) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                            drawSpot(canvas, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mDrawingSurface.unlockCanvasAndPost(canvas);
                        isDrawing = false;
                    }
                }
                break;
        }
        return true;
    }


    /**
     * 清楚屏幕上轨迹
     */
    public void clearDraw() {
        Canvas canvas = null;
        try {
            canvas = mDrawingSurface.lockCanvas();
            if (canvas == null) {
                return;
            }
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mDrawingSurface.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 传入spot，绘制
     *
     * @param spot
     */
    public void setSpot(Spot spot) {
        if(!isDrawing) {
            isDrawing = true;
            // 通过 Message 参数将位置传给处理程序
            Message msg = Message.obtain(mReceiver, MSG_DRAW, spot);
            mReceiver.sendMessage(msg);
        }
    }

    public void setSpotUp(Spot spot){
        setSpot(spot);
        if(mDrawInterface != null){
            mDrawInterface.onMotionEventUp();
        }
    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.mDrawInterface = drawInterface;
    }

    /**
     * 两园相交求交点坐标
     *
     * @param spot1
     * @param spot2
     * @return
     */
    private double[] circleCircleIntersection(Spot spot1, Spot spot2, double r1, double r2) {

        double x1 = spot1.getX();
        double y1 = spot1.getY();
        double x2 = spot2.getX();
        double y2 = spot2.getY();
        // 在一元二次方程中 a*x^2+b*x+c=0
        double a, b, c;
        //x的两个根 x_1 , x_2
        //y的两个根 y_1 , y_2
        double x_1 = 0, x_2 = 0, y_1 = 0, y_2 = 0;
        //判别式的值
        double delta = -1;
        //如果 y1!=y2
        if (y1 != y2) {
            //为了方便代入
            double A = (x1 * x1 - x2 * x2 + y1 * y1 - y2 * y2 + r2 * r2 - r1 * r1) / (2 * (y1 - y2));
            double B = (x1 - x2) / (y1 - y2);
            a = 1 + B * B;
            b = -2 * (x1 + (A - y1) * B);
            c = x1 * x1 + (A - y1) * (A - y1) - r1 * r1;
            //下面使用判定式 判断是否有解
            delta = b * b - 4 * a * c;
            if (delta > 0) {
                x_1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
                x_2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
                y_1 = A - B * x_1;
                y_2 = A - B * x_2;
            } else if (delta == 0) {
                x_1 = x_2 = -b / (2 * a);
                y_1 = y_2 = A - B * x_1;
            } else {
                System.err.println("两个圆不相交");
                return null;
            }
        } else if (x1 != x2) {
            //当y1=y2时，x的两个解相等
            x_1 = x_2 = (x1 * x1 - x2 * x2 + r2 * r2 - r1 * r1) / (2 * (x1 - x2));
            a = 1;
            b = -2 * y1;
            c = y1 * y1 - r1 * r1 + (x_1 - x1) * (x_1 - x1);
            delta = b * b - 4 * a * c;
            if (delta > 0) {
                y_1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
                y_2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
            } else if (delta == 0) {
                y_1 = y_2 = -b / (2 * a);
            } else {
                System.err.println("两个圆不相交");
                return null;
            }
        } else {
            System.out.println("无解");
            return null;
        }
        return new double[]{x_1, y_1, x_2, y_2};
    }

    private Spot lineCircleIntersection(Spot spot0, Spot spot1, int r) {
        double c = spot0.getX();
        double d = spot0.getY();
        double x1 = spot1.getX();
        double y1 = spot1.getY();

        double k = (d - y1) / (c - x1);
        double b = -k * x1 + y1;
        double CK = k * k + 1;
//        double A = Math.abs((CK) * r * r - c * c * k * k + (2 * c * d + 2 * b * c) * k - d * d - 2 * b * d - b * b);
//        double B = Math.abs(k * k * r * r + r * r - c * c * k * k + 2 * c * d * k + 2 * b * c * k - d * d - 2 * b * d - b * b);
        double A = Math.abs((CK) * r * r - c * c * k * k + (2 * c * d - 2 * b * c) * k - d * d + 2 * b * d - b * b);
        double B = Math.abs(k * k * r * r + r * r - c * c * k * k + 2 * c * d * k - 2 * b * c * k - d * d + 2 * b * d - b * b);

//        double x_1 = (Math.sqrt(A) + (d + b) * k + c) / CK;
//        double y_1 = (k * (Math.sqrt(B) + c) + d * k * k - b) / CK;
//        double x_2 = (Math.sqrt(A) + (-d - b) * k - c) / CK;
//        double y_2 = (k * (c - Math.sqrt(B)) + d * k * k - b) / CK;

        double x_1 = -(Math.sqrt(A) + (-d + b) * k - c) / CK;
        double y_1 = -(k * (Math.sqrt(B) - c) - d * k * k - b) / CK;
        double x_2 = (Math.sqrt(A) + (d - b) * k + c) / CK;
        double y_2 = -(k * (-c - Math.sqrt(B)) - d * k * k - b) / CK;


        Spot spot_1 = new Spot(x_1, y_1);
        Spot spot_2 = new Spot(x_2, y_2);
        double distance_1 = getDistance(spot_1, spot1);
        double distance_2 = getDistance(spot_2, spot1);
        return distance_1 > distance_2 ? spot_1 : spot_2;
    }

    /**
     * 计算两点之间的距离
     *
     * @param nowSpot
     * @param oldSpot
     * @return
     */
    private double getDistance(Spot nowSpot, Spot oldSpot) {
        double _x = Math.abs(nowSpot.getX() - oldSpot.getX());
        double _y = Math.abs(nowSpot.getY() - oldSpot.getY());
        return Math.sqrt(_x * _x + _y * _y);
    }

    /**
     * 求穿过同心圆圆心的直线上同一侧的点的坐标，已知一点坐标
     *
     * @param origin 圆心坐标
     * @param r1     已知坐标的圆的半径
     * @param r2     未知坐标的圆的半径
     * @param spot   已知的坐标
     * @return 位置的坐标
     */
    private Spot getOuterCircle(Spot origin, double r1, double r2, Spot spot) {
        double x = spot.getX();
        double y = spot.getY();

        double x1 = x - origin.getX();
        double y1 = y - origin.getY();

        double x2 = x1 * r2 / r1;
        double y2 = y1 * r2 / r1;

        return new Spot((float) x2 + origin.getX(), (float) y2 + origin.getY());
    }

    /**
     * 任意两条直线的角度，此方法两直线交点为spot0
     *
     * @param spot0 交点，此处理解为圆心
     * @param spot1 第一条直线上的结束点
     * @param spot2 第二条直线上的结束点
     * @return
     */
    private double pointAcquisitionAngle(Spot spot0, Spot spot1, Spot spot2) {

        if (spot0 == null) {
            return 0;
        }
        double x0 = spot0.getX();
        double y0 = spot0.getY();
        double x1 = spot1.getX();
        double y1 = spot1.getY();
        double x2 = spot2.getX();
        double y2 = spot2.getY();

        double k1;
        double k2;

        if (x1 != x0) {
            k1 = (y1 - y0) / (x1 - x0);
        } else {
            k1 = Integer.MAX_VALUE;
        }
        if (x2 != x0) {

            k2 = (y2 - y0) / (x2 - x0);
        } else {
            k2 = Integer.MAX_VALUE;
        }
        return Math.abs(Math.atan(Math.abs(k1 - k2) / (1 + k1 * k2)));

    }

    /**
     * 求两直线的角度
     *
     * @param spot1
     * @param spot2
     * @param spot3
     * @param spot4
     * @return
     */
    private double pointCrossAngle(Spot spot1, Spot spot2, Spot spot3, Spot spot4) {
        double x1 = spot1.getX();
        double y1 = spot1.getY();
        double x2 = spot2.getX();
        double y2 = spot2.getY();
        double x3 = spot3.getX();
        double y3 = spot3.getY();
        double x4 = spot4.getX();
        double y4 = spot4.getY();
        double k1 = Double.MAX_VALUE;
        double k2 = Double.MAX_VALUE;
        boolean flag1 = false;
        boolean flag2 = false;

        if ((x1 - x2) == 0)
            flag1 = true;
        if ((x3 - x4) == 0)
            flag2 = true;

        if (!flag1)
            k1 = (y1 - y2) / (x1 - x2);
        if (!flag2)
            k2 = (y3 - y4) / (x3 - x4);

        if (k1 == k2)
            return 0;

        return Math.abs(Math.atan(Math.abs(k1 - k2) / (1 + k1 * k2)));
    }

    /**
     * 任意两条直线的交点
     *
     * @param spot1
     * @param spot2
     * @param spot3
     * @param spot4
     * @return
     */
    public Spot getCrossPoint(Spot spot1, Spot spot2, Spot spot3, Spot spot4) {
        double x;
        double y;
        double x1 = spot1.getX();
        double y1 = spot1.getY();
        double x2 = spot2.getX();
        double y2 = spot2.getY();
        double x3 = spot3.getX();
        double y3 = spot3.getY();
        double x4 = spot4.getX();
        double y4 = spot4.getY();
        double k1 = Double.MAX_VALUE;
        double k2 = Double.MAX_VALUE;
        boolean flag1 = false;
        boolean flag2 = false;

        if ((x1 - x2) == 0)
            flag1 = true;
        if ((x3 - x4) == 0)
            flag2 = true;

        if (!flag1)
            k1 = (y1 - y2) / (x1 - x2);
        if (!flag2)
            k2 = (y3 - y4) / (x3 - x4);

        if (k1 == k2)
            return null;

        if (flag1) {
            if (flag2)
                return null;
            x = x1;
            if (k2 == 0) {
                y = y3;
            } else {
                y = k2 * (x - x4) + y4;
            }
        } else if (flag2) {
            x = x3;
            if (k1 == 0) {
                y = y1;
            } else {
                y = k1 * (x - x2) + y2;
            }
        } else {
            if (k1 == 0) {
                y = y1;
                x = (y - y4) / k2 + x4;
            } else if (k2 == 0) {
                y = y3;
                x = (y - y2) / k1 + x2;
            } else {
                x = (k1 * x2 - k2 * x4 + y4 - y2) / (k1 - k2);
                y = k1 * (x - x2) + y2;
            }
        }
        if (between(x1, x2, x) && between(y1, y2, y) && between(x3, x4, x) && between(y3, y4, y)) {
            Spot spot = new Spot();
            spot.setX(x);
            spot.setY(y);
            if (spot.equals(spot1) || spot.equals(spot2))
                return null;
            return spot;
        } else {
            return null;
        }
    }

    public boolean between(double a, double b, double target) {
        if (target >= a - 0.01 && target <= b + 0.01 || target <= a + 0.01 && target >= b - 0.01)
            return true;
        else
            return false;
    }


    /**
     * 圆心为spot0 的圆上点spot1 旋转角度c后的点的位置
     *
     * @param spot0
     * @param spot1
     * @param c
     * @return
     */
    private Spot angleAcquisitionPoint(Spot spot0, Spot spot1, double c) {

        double x0 = spot0.getX();
        double y0 = spot0.getY();
        double x1 = spot1.getX();
        double y1 = spot1.getY();

        double x2 = (x1 - x0) * Math.cos(c) - (y1 - y0) * Math.sin(c) + x0;
        double y2 = (y1 - y0) * Math.cos(c) + (x1 - x0) * Math.sin(c) + y0;
        return new Spot((float) x2, (float) y2);
    }

    /**
     * 手指触摸的方位
     *
     * @param dx
     * @param dy
     * @return
     */
    private int getOrientation(double dx, double dy) {
        if (Math.abs(dx) < Math.abs(dy)) {
            return dy > 0 ? 'b' : 't';
        } else {
            return dx > 0 ? 'r' : 'l';
        }
    }

    /**
     * 判断旋转方向(同心圆)
     *
     * @param spot        触摸点
     * @param rotation
     * @param orientation 角度
     * @return
     */
    private double rotationDirection(Spot spot, Spot originSpot, Spot firstSpot, double rotation, int orientation) {
        //判断触摸点在第几象限
        if (spot.getX() - originSpot.getX() > 0 && spot.getY() - originSpot.getY() < 0) {//1
            switch (orientation) {
                case 'r':
                    if (firstSpot.getX() - originSpot.getX() < 0) {//实际点在第二象限
                        rotation = -rotation;
                    } else {
                        rotation = rotation;
                    }
                    break;
                case 'l':
                    rotation = -rotation;
                    break;
                case 't':
                    rotation = -rotation;
                    break;
                case 'b':
                    rotation = rotation;
                    break;
            }
        } else if (spot.getX() - originSpot.getX() < 0 && spot.getY() - originSpot.getY() < 0) {//2
            switch (orientation) {
                case 'r':
                    rotation = rotation;
                    break;
                case 'l':
                    if (firstSpot.getX() - originSpot.getX() > 0) {//实际点在还在第一象限
                        rotation = rotation;
                    } else {
                        rotation = -rotation;
                    }
                    break;
                case 't':
                    rotation = rotation;
                    break;
                case 'b':
                    rotation = -rotation;
                    break;
            }
        } else if (spot.getX() - originSpot.getX() < 0 && spot.getY() - originSpot.getY() > 0) {//3
            switch (orientation) {
                case 'r':
                    rotation = -rotation;
                    break;
                case 'l':
                    if (firstSpot.getX() - originSpot.getX() > 0) {///实际点在第四象限
                        rotation = -rotation;
                    } else {
                        rotation = rotation;
                    }
                    break;
                case 't':
                    rotation = rotation;
                    break;
                case 'b':
                    rotation = -rotation;
                    break;
            }
        } else if (spot.getX() - originSpot.getX() > 0 && spot.getY() - originSpot.getY() > 0) {//4
            switch (orientation) {
                case 'r':
                    if (firstSpot.getX() - originSpot.getX() < 0) {//实际点还在第三想象限
                        rotation = rotation;
                    } else {
                        rotation = -rotation;
                    }
                    break;
                case 'l'://不同
                    rotation = rotation;
                    break;
                case 't':
                    rotation = -rotation;
                    break;
                case 'b':
                    rotation = rotation;
                    break;
            }
        }
        return rotation;
    }

    /**
     * 小角度变化角度旋转方向
     *
     * @param spot0
     * @param spot1
     * @param spot2
     * @return true 顺时针
     */
    private boolean positiveNegative(Spot spot0, Spot spot1, Spot spot2) {
        boolean isClockwise = false;
        double x0 = spot0.getX();
        double y0 = spot0.getY();

        double x1 = spot1.getX();
        double y1 = spot1.getY();

        double x2 = spot2.getX();
        double y2 = spot2.getY();

        //特殊情况
        if (y1 < y0 && y2 < y0) {
            if (x1 < x2) {
                isClockwise = true;
            } else {
                isClockwise = false;
            }
        }
        if (y1 > y0 && y2 > y0) {
            if (x1 < x2) {
                isClockwise = false;
            } else {
                isClockwise = true;
            }
        }
        if (x1 > x0 && x2 > x0) {
            if (y1 < y2) {
                isClockwise = true;
            } else {
                isClockwise = false;
            }
        }
        if (x1 < x0 && x2 < x0) {
            if (y1 < y2) {
                isClockwise = false;
            } else {
                isClockwise = true;
            }
        }

        if (x1 > x0 && y1 < y0 && x2 > x0 && y2 < y0) {//11
            if (x1 < x2) {
                isClockwise = true;
            } else {
                isClockwise = false;
            }
        } else if (x1 < x0 && y1 < y0 && x2 < x0 && y2 < y0) {//22
            if (x1 < x2) {
                isClockwise = true;
            } else {
                isClockwise = false;
            }
        } else if (x1 < x0 && y1 > y0 && x2 < x0 && y2 > y0) {//33
            if (x1 < x2) {
                isClockwise = false;
            } else {
                isClockwise = true;
            }
        } else if (x1 > x0 && y1 > y0 && x2 > x0 && y2 > y0) {//44
            if (x1 < x2) {
                isClockwise = false;
            } else {
                isClockwise = true;
            }
        }
        return isClockwise;
    }

    private boolean pointToYDistance(double y0, double y) {
        return y < y0 ? true : false;
    }

    /**
     * 弧度换算成角度
     *
     * @return
     */
    public static double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }


    /**
     * 角度换算成弧度
     *
     * @param degree
     * @return
     */
    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }
}

