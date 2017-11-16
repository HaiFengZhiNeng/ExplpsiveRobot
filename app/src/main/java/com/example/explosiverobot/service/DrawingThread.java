package com.example.explosiverobot.service;

import android.content.Context;
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
import com.example.explosiverobot.util.GsonUtil;
import com.example.explosiverobot.util.PreferencesUtils;
import com.seabreeze.log.Print;

import java.math.BigDecimal;

/**
 * Created by zhangyuanyuan on 2017/9/28.
 */

public class DrawingThread extends HandlerThread implements Handler.Callback {

    private static final int MSG_DRAW = 101;

    private Context mContext;

    private int leftBase;
    private int topBase;
    private int rightBase;
    private int bottomBase;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mDrawingSurface;
    private Paint mPaint;
    private Handler mReceiver;
    //回调
    private DrawInterface mDrawInterface;

    private boolean isDrawing;

    // 定义一个记录图像是否开始渲染的旗帜
    private boolean mRunning;

    //整个图像基础位置
    private int basePosX;
    private int basePosY;
    //定义地盘车的宽高
    private int carW;
    private int carH;
    //地盘车最低点
    private int groundY;
    //基座bitmap
    private Bitmap armBBitmap;
    //第一条
    private Bitmap armSBitmap;
    //第二条
    private Bitmap armFBitmap;
    //第三条
    private Bitmap armEBitmap;
    //基座位置
    private Rect mArmBaseDestRect;
    //重置
    private Bitmap resetBitmap;

    //定义机械臂旋转最大最小角度
    private double degreeFMax = 60;
    private double degreeFMin = 15;
    private double degreeSMax = 180;
    private double degreeSMin = 100;
    private double degreeEMax = 180;
    private double degreeEMin = 45;
    //定义触摸的范围
    private int rtouch = 70;

    private Matrix mFMatrix = new Matrix();
    private Matrix mSMatrix = new Matrix();
    private Matrix mEMatrix = new Matrix();

    //上方推点
    private Spot pushSpot;
    //下方支点
    private Spot braceSpot;
    //第一条线末点到上方推点间距离与第一条线长度比例
    private double shortProportion = 0.25;
    //第一条线起点到上方推点间距离与第一条线长度比例
    private double longProportion = 0.95;
    //第一条线起点到支撑点x轴上比例
    private double braceXProportion = 0.35;
    //第一条线起点到支撑点y轴上比例
    private double braceYProportion = 0.29;
    //第一条线的长度
    private int rF;
    //第二条线的长度
    private int rS;
    //第三条线的长度
    private int rE;
    //固定点坐标
    private Spot baseSpot;

    //固定坐标点的水平点
    private Spot levelSpot;
    //第一条线两点
    private Spot startFSpot;
    private Spot endFSpot;
    private Spot fYSpot;
    //第二条线两点
    private Spot startSSpot;
    private Spot endSSpot;
    private Spot sYSpot;
    //第二条线两点
    private Spot startESpot;
    private Spot endESpot;
    private Spot eYSpot;
    //备份点
    private Spot startFSpotTemp;
    private Spot endFSpotTemp;
    private Spot startSSpotTemp;
    private Spot endSSpotTemp;
    private Spot startESpotTemp;
    private Spot endESpotTemp;

    private double rotateF;
    private double rotateS;
    private double rotateE;

    private double rotateFTemp;
    private double rotateSTemp;
    private double rotateETemp;

    private boolean isTouchFe;
    private boolean isTouchSe;
    private boolean isTouchEe;
    private boolean isFirst;

    private Spot firstSpotTail;
    private double originalBraceDistance;

    private double myRotation;

    private double mCallRotation1;

    private double mCallRotation2;
    private double mCallRotation3;

    private float mRotation3;
    private float mRotation2;
    private float mRotation1;
    //    private Spot touchSpot;
    private Spot mySpot;

    private long curTime;

    public DrawingThread(Context context, SurfaceView surfaceView, SurfaceHolder holder, int screenW, int screenH) {
        super("DrawingThread");
        mContext = context;
        mSurfaceView = surfaceView;
        mDrawingSurface = holder;

        setPaint();
        loadBitmap();
        setBasePos(screenW, screenH);

        loadRadius();

        definitionF();
        definitionS();
        definitionE();

        mySpot = new Spot(0, 0);

        definitionPushAAndBrace();

        computeRotate();

        isFirst = true;
//        mRotation1 = PreferencesUtils.getFloat(mContext, "mRotation1", 0);
//        mRotation2 = PreferencesUtils.getFloat(mContext, "mRotation2", 0);
//        mRotation3 = PreferencesUtils.getFloat(mContext, "mRotation3", 0);
//        myRotation = PreferencesUtils.getFloat(mContext, "myRotation", 0);
    }

    /**
     * 画笔
     */
    private void setPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        mPaint.setTextSize(30);
        mPaint.setColor(Color.rgb(255, 203, 15));
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * 加载bitmap
     */
    private void loadBitmap() {
        armBBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_base);
        armFBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_first);
        armSBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_second);
        armEBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.arm_end);
        resetBitmap = BitmapFactory.decodeResource(mSurfaceView.getResources(), R.mipmap.ic_recovery);
    }

    /**
     * 定义基础位置
     */
    private void setBasePos(int screenW, int screenH) {
        basePosX = (int) (screenW * 0.3);
        basePosY = (int) (screenH * 0.7);
        baseSpot = new Spot(basePosX, basePosY);
        //地盘车
        carW = armBBitmap.getWidth() * 2;
        carH = armBBitmap.getHeight() / 3;

        levelSpot = new Spot(basePosX + carW, basePosY);

        groundY = (int) (basePosY + armBBitmap.getHeight() * 7 / 6);

        leftBase = (int) (basePosX - armBBitmap.getWidth() * 2 / 3);
        topBase = (int) (basePosY - armBBitmap.getHeight() / 6);
        rightBase = (int) (basePosX + armBBitmap.getWidth() / 3);
        bottomBase = (int) (basePosY + armBBitmap.getHeight() * 5 / 6);
        mArmBaseDestRect = new Rect(leftBase, topBase, rightBase, bottomBase);
    }

    /**
     * 获取半径
     */
    private void loadRadius() {
        rF = armFBitmap.getHeight() * 4 / 5 - armSBitmap.getWidth() / 2;
        rS = armSBitmap.getHeight() * 8 / 9 - armEBitmap.getWidth() / 2;
        rE = armEBitmap.getHeight() - armEBitmap.getWidth();
    }

    /**
     * 获取第一条线两点位置
     */
    private void definitionF() {
        startFSpot = takeForKey("startFSpot");
        endFSpot = takeForKey("endFSpot");
        if (startFSpot == null) {
            startFSpot = baseSpot;
        }
        if (endFSpot == null) {
            endFSpot = new Spot(basePosX + rF * Math.cos(degreeToRadian(degreeFMax)), basePosY - rF * Math.sin(degreeToRadian(degreeFMax)));
        }
        fYSpot = new Spot(startFSpot.getX(), startFSpot.getY() - rF);
    }

    /**
     * 获取第二条线两点位置
     */
    private void definitionS() {
        startSSpot = takeForKey("startSSpot");
        endSSpot = takeForKey("endSSpot");
        if (startSSpot == null) {
            startSSpot = new Spot(basePosX + rF * Math.cos(degreeToRadian(degreeFMax)),
                    basePosY - rF * Math.sin(degreeToRadian(degreeFMax)));
            ;
        }
        if (endSSpot == null) {
            endSSpot = new Spot(startSSpot.getX() + rS * Math.sin(degreeToRadian(270 - degreeSMax - degreeFMax)),
                    startSSpot.getY() - rS * Math.cos(degreeToRadian(270 - degreeSMax - degreeFMax)));
        }
        sYSpot = new Spot(startSSpot.getX(), startSSpot.getY() - rS);
    }

    /**
     * 获取第三条线上两点位置
     */
    private void definitionE() {
        startESpot = takeForKey("startESpot");
        if (startESpot == null) {
            startESpot = new Spot(endFSpot.getX() + rS * Math.sin(degreeToRadian(270 - degreeSMax - degreeFMax)),
                    endFSpot.getY() - rS * Math.cos(degreeToRadian(270 - degreeSMax - degreeFMax)));
        }
        endESpot = takeForKey("endESpot");
        if (endESpot == null) {
            endESpot = new Spot(endFSpot.getX() + rS * Math.sin(degreeToRadian(270 - degreeSMax - degreeFMax)) + rE,
                    endFSpot.getY() - rS * Math.cos(degreeToRadian(270 - degreeSMax - degreeFMax)));
        }
        eYSpot = new Spot(startESpot.getX(), startESpot.getY() - rE);
    }

    /**
     * 计算第一条臂所推的长度
     */
    private void definitionPushAAndBrace() {
        //计算三角形中推点的位置
        pushSpot = belowLinePoint(startFSpot, endFSpot, rF * longProportion, rF * shortProportion);

        braceSpot = new Spot((int) (startFSpot.getX() + rF * braceXProportion),
                (int) (baseSpot.getY() + Math.sqrt(rF * rF * braceXProportion * braceXProportion
                        - rF * rF * braceYProportion * braceYProportion)));
        originalBraceDistance = getDistance(braceSpot, pushSpot);

        firstSpotTail = angleRotatePoint(baseSpot, endFSpot, degreeToRadian(degreeFMax - degreeFMin));
    }

    /**
     * 由点计算角度
     */
    private void computeRotate() {
        rotateF = 90 - radianToDegree(pointYAngle(startFSpot, endFSpot));
        if (startSSpot.getY() > endSSpot.getY()) {
            rotateS = 90 - radianToDegree(pointYAngle(startSSpot, endSSpot));
        } else if (startSSpot.getY() < endSSpot.getY()) {
            rotateS = 90 + radianToDegree(pointYAngle(startSSpot, endSSpot));
        } else {
            rotateS = 90;
        }
        if (startESpot.getY() > endESpot.getY()) {
            rotateE = 90 - radianToDegree(pointYAngle(startESpot, endESpot));
        } else if (startESpot.getY() < endESpot.getY()) {
            rotateE = 90 + radianToDegree(pointYAngle(startESpot, endESpot));
        } else {
            rotateE = 90;
        }
        Print.e("rotateF : " + rotateF + ", rotateS : " + rotateS + ", rotateE : " + rotateE);
    }

    /**
     * 由角度计算各个点的位置
     */
    public void calculationPointFromAngle(double rotateF, double rotateS, double rotateE) {
        startFSpot = baseSpot;
        fYSpot = new Spot(startFSpot.getX(), startFSpot.getY() - rF);
        endFSpot = angleRotatePoint(startFSpot, fYSpot, degreeToRadian(rotateF));
        startSSpot = endFSpot;
        sYSpot = new Spot(startSSpot.getX(), startSSpot.getY() - rS);
        endSSpot = angleRotatePoint(startSSpot, sYSpot, degreeToRadian(rotateS));
        startESpot = endSSpot;
        eYSpot = new Spot(startESpot.getX(), startESpot.getY() - rE);
        endESpot = angleRotatePoint(startESpot, eYSpot, degreeToRadian(rotateE));
    }

    /**
     * 两个圆相交，xy都取大的
     */
    private Spot belowLinePoint(Spot spot1, Spot spot2, double rS, double rE) {
        double[] intersectionTriangle = circleCircleIntersection(spot1, spot2, rS, rE);
        if (intersectionTriangle != null) {
            float[] intersectionFloatTriangle = new float[4];
            for (int i = 0; i < intersectionTriangle.length; i++) {
                BigDecimal b = new BigDecimal(Double.toString(intersectionTriangle[i]));
                intersectionFloatTriangle[i] = b.floatValue();
            }
            return new Spot(intersectionFloatTriangle[0] > intersectionFloatTriangle[2] ? intersectionFloatTriangle[0] : intersectionFloatTriangle[2],
                    intersectionFloatTriangle[1] > intersectionFloatTriangle[3] ? intersectionFloatTriangle[1] : intersectionFloatTriangle[3]);
        } else {
            return null;
        }
    }

    /**
     * 绘制图形
     *
     * @param canvas
     */
    private void drawSpot(Canvas canvas) {

        //绘制重置
        canvas.drawBitmap(resetBitmap, 10, 10, mPaint);
        //绘制地盘车
        mPaint.setColor(Color.MAGENTA);
        canvas.drawRect((float) leftBase, (float) topBase + armBBitmap.getHeight(),
                (float) rightBase + carW, (float) bottomBase + carH, mPaint);
        canvas.drawLine((float) braceSpot.getX(), (float) braceSpot.getY(), (float) pushSpot.getX(), (float) pushSpot.getY(), mPaint);
        //绘制基座
        mPaint.setColor(Color.WHITE);
        canvas.drawBitmap(armBBitmap, null, mArmBaseDestRect, mPaint);
        canvas.drawCircle(leftBase, topBase, rtouch, mPaint);
        //画第一条
        mPaint.setColor(Color.BLUE);
        mFMatrix.reset();
        float dxFirst = (float) (startFSpot.getX() - armFBitmap.getWidth() / 2);
        float dyFirst = (float) (startFSpot.getY() - armFBitmap.getHeight() * 4 / 5);
        mFMatrix.postTranslate(dxFirst, dyFirst);
        float pxFirst = (float) startFSpot.getX();
        float pyFirst = (float) startFSpot.getY();
        mFMatrix.postRotate((float) rotateF, pxFirst, pyFirst);
        canvas.drawBitmap(armFBitmap, mFMatrix, mPaint);
        canvas.drawCircle(pxFirst, pyFirst, rF, mPaint);
        canvas.drawLine(pxFirst, pyFirst, (float) endFSpot.getX(), (float) endFSpot.getY(), mPaint);
        //画第二条
        mPaint.setColor(Color.RED);
        mSMatrix.reset();
        float dxSecond = (float) (startSSpot.getX() - armSBitmap.getWidth() / 2);
        float dySecond = (float) (startSSpot.getY() - armSBitmap.getHeight() * 8 / 9);
        mSMatrix.postTranslate(dxSecond, dySecond);
        float pxSecond = (float) startSSpot.getX();
        float pySecond = (float) startSSpot.getY();
        mSMatrix.postRotate((float) rotateS, pxSecond, pySecond);
        canvas.drawBitmap(armSBitmap, mSMatrix, mPaint);
        canvas.drawCircle(pxSecond, pySecond, rS, mPaint);
        canvas.drawLine(pxSecond, pySecond, (float) endSSpot.getX(), (float) endSSpot.getY(), mPaint);
        //画第三条
        mPaint.setColor(Color.GREEN);
        mEMatrix.reset();
        float dxEnd = (float) (startESpot.getX() - armEBitmap.getWidth() / 2);
        float dyEnd = (float) (startESpot.getY() - armEBitmap.getHeight() * 14 / 16);
        mEMatrix.postTranslate(dxEnd, dyEnd);
        float pxEnd = (float) startESpot.getX();
        float pyEnd = (float) startESpot.getY();
        mEMatrix.postRotate((float) rotateE, pxEnd, pyEnd);
        canvas.drawBitmap(armEBitmap, mEMatrix, mPaint);
        canvas.drawCircle(pxEnd, pyEnd, rE, mPaint);
        canvas.drawLine(pxEnd, pyEnd, (float) endESpot.getX(), (float) endESpot.getY(), mPaint);

        mPaint.setColor(Color.YELLOW);
//        mySpot.setX(startESpot.getX());
//        mySpot.setY(startESpot.getY() - rE);
//        mySpot = angleAcquisitionPoint(startESpot, mySpot, myRotation);
//        canvas.drawCircle((float) mySpot.getX(), (float) mySpot.getY(), 50, mPaint);

        mPaint.setColor(Color.BLACK);
//        canvas.drawCircle((float) touchSpot.getX(), (float) touchSpot.getY(), rtouch, mPaint);
//        if(!isFirst) {
            if (rotateF - rotateFTemp != 0) {
                mDrawInterface.rotatioCallbackn(rotateF - rotateFTemp, 0, 0, -1);
            } else {
                if (isTouchSe) {
                    isTouchEe = false;
                    mDrawInterface.rotatioCallbackn(rotateF - rotateFTemp, rotateS - rotateSTemp, 0, -1);
                } else {
                    mDrawInterface.rotatioCallbackn(rotateF - rotateFTemp, rotateS - rotateSTemp, rotateE - rotateETemp, -1);

                }
            }
//        }else{
//            isFirst = false;
//            mDrawInterface.rotatioCallbackn(rotateF, rotateS , rotateE , -1);
//        }
        isDrawing = false;
    }

    /**
     * 备份6个点
     */
    private void backupSpot() {
        startFSpotTemp = startFSpot;
        endFSpotTemp = endFSpot;
        startSSpotTemp = startSSpot;
        endSSpotTemp = endSSpot;
        startESpotTemp = startESpot;
        endESpotTemp = endESpot;

        rotateFTemp = rotateF;
        rotateSTemp = rotateS;
        rotateETemp = rotateE;
    }

    private void reductionSpot() {
        startFSpot = startFSpotTemp;
        endFSpot = endFSpotTemp;
        startSSpot = startSSpotTemp;
        endSSpot = endSSpotTemp;
        startESpot = startESpotTemp;
        endESpot = endESpotTemp;
    }


    private void touchEe() {
        //触摸点再第三条线的触摸范围之内，且再最大半径以内
        //计算以第二圆和第三圆的交点
        double[] intersection = circleCircleIntersection(startSSpot, endESpot, rS, rE);
        if (intersection == null) {
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
        double distance1 = getDistance(spot1, endSSpot);
        double distance2 = getDistance(spot2, endSSpot);
        //判断距离，取最小值
        if (distance1 > distance2) {
            endSSpot = spot2;
        } else {
            endSSpot = spot1;
        }
        startESpot = endSSpot;
    }

    private void touchFe(Spot spot) {

        //触摸点到中心点的距离
        double distance = getDistance(startFSpot, spot);
        //由触摸点得到在中心圆上实际点的坐标
        Spot temporarySpot = getOuterCircle(startFSpot, distance, rF, spot);
        //由中心圆实际点的坐标计算出应该旋转的角度
        double rotation = pointAcquisitionAngle(startFSpot, endFSpot, temporarySpot);
        //判断触摸的方向
        int orientation = getOrientation(spot.getX() - endFSpot.getX(), spot.getY() - endFSpot.getY());
        //判断旋转方向
        rotation = rotationDirection(spot, startFSpot, endFSpot, rotation, orientation);

        //由角度计算出个点位置
        endFSpot = angleRotatePoint(startFSpot, endFSpot, rotation);
        startSSpot = angleRotatePoint(startFSpot, startSSpot, rotation);
        endSSpot = angleRotatePoint(startFSpot, endSSpot, rotation);
        startESpot = angleRotatePoint(startFSpot, startESpot, rotation);
        endESpot = angleRotatePoint(startFSpot, endESpot, rotation);

    }

    private void touchSe(Spot spot) {
        //触摸点到中心点的距离
        double distanceb = getDistance(startSSpot, spot);
        //由触摸点得到在中心圆上实际点的坐标
        Spot temporarySpot = getOuterCircle(startSSpot, distanceb, rS, spot);
        //由中心圆实际点的坐标计算出应该旋转的角度
        double rotation = pointAcquisitionAngle(startSSpot, endSSpot, temporarySpot);
        //判断触摸的方向
        int orientation = getOrientation(spot.getX() - endSSpot.getX(), spot.getY() - endSSpot.getY());
        //判断旋转方向
        rotation = rotationDirection(spot, startSSpot, endSSpot, rotation, orientation);

        //由角度计算出个点位置
        startESpot = angleRotatePoint(startSSpot, startESpot, rotation);
        endESpot = angleRotatePoint(startSSpot, endESpot, rotation);

        endSSpot = startESpot;
    }


    private void loadDrawSpot(Spot spot) {
        //点击的点到第三条线末端的位置
        double distanceTouch = getDistance(spot, endESpot);
        //点击的点到第二个圆中心点的距离
        double distanceMax = getDistance(spot, startSSpot);
        //点击的点到第三个圆的中心的距离
        double distanceEnd = getDistance(spot, startESpot);
        //触摸点再第三条线的触摸范围之内，且再最大半径以内
        if (distanceTouch < rtouch && distanceMax < rS + rE && distanceMax > rtouch) {
            endESpot = spot;
            touchEe();

        } else if (distanceMax > rS + rE && distanceMax < rS + rE + rtouch) {//触摸点再最大半径之外再可控范围之内

            //触摸点到第二个圆心点的距离
            double distance = getDistance(spot, startSSpot);
            //计算第二线的结束点位置
            endSSpot = getOuterCircle(startSSpot, distance, rS, spot);
            startESpot = endSSpot;
            //尾点
            endESpot = getOuterCircle(startSSpot, rS, rE + rS, startESpot);

        } else if (distanceMax < rtouch) {//第一条线的末点

            touchFe(spot);
        } else if (distanceEnd < rtouch) {
            isTouchSe = true;
            touchSe(spot);
        }

        if (judgeModel()) {
            return;
        }


//        pushSpot = belowLinePoint(baseSpot, endFSpot, rF * longProportion, rF * shortProportion);
//        double braceDistance = getDistance(braceSpot, pushSpot);
//        double changeCistance = originalBraceDistance - braceDistance;
//        originalBraceDistance = braceDistance;
//
//
//        if (mDrawInterface != null) {
//            mDrawInterface.rotatioCallbackn(radianToDegree(mCallRotation1), mCallRotation2, mCallRotation3, changeCistance * 48.5 / rF);
//        }
        computeRotate();
    }

    private boolean judgeModel() {
        if (endESpot.getX() < rightBase + carW) {
            if (pointToYDistance(endESpot.getY(), topBase + armBBitmap.getHeight())) {
                reductionSpot();
                computeRotate();
                return true;
            }
        }
        if (cannotTouchLand()) return true;

        if (judgeF()) return true;
        if (judgeS()) return true;
        if (judgeE()) return true;
        return false;
    }

    private boolean judgeE() {
        //secondSpot连接endingSpot的直线在圆心为endingSpot上的交点
        Spot endNeedleSpotRes = lineCircleIntersection(startESpot,
                new Spot(startSSpot.getX(), 2 * startESpot.getY() - startSSpot.getY()), rE);
        Spot endNeedleSpotRes0 = new Spot(endNeedleSpotRes.getX(),
                startESpot.getY() - (endNeedleSpotRes.getY() - startESpot.getY()));
        if (endNeedleSpotRes0.getX() > startSSpot.getX() && endNeedleSpotRes0.getY() < startSSpot.getY()
                && endESpot.getX() > startSSpot.getX() && endESpot.getY() < startSSpot.getY()) {
            if (endNeedleSpotRes0.getX() > endESpot.getX()) {
                reductionSpot();
                computeRotate();
                Print.e("judgeE");
                return true;
            }
        }

        Spot endNeedleSpotRes65 = angleRotatePoint(startESpot, endNeedleSpotRes0, degreeToRadian((degreeEMax - degreeEMin) / 2));
        double endDegree = radianToDegree(pointAcquisitionAngle(startESpot, endESpot, endNeedleSpotRes65));
        if (endDegree > (degreeEMax - degreeEMin) / 2) {
            reductionSpot();
            computeRotate();
            Print.e("judgeE");
            return true;
        }
        return false;
    }

    private boolean judgeS() {
        //originSpot连接firstSpot的直线在圆心为secondSpot上的交点
        Spot secondNeedleSpotRes = lineCircleIntersection(startSSpot,
                new Spot(startFSpot.getX(), 2 * startSSpot.getY() - startFSpot.getY()), rS);
        //android上准确点的位置
        Spot secondNeedleSpotRes0 = new Spot(secondNeedleSpotRes.getX(),
                startSSpot.getY() - (secondNeedleSpotRes.getY() - startSSpot.getY()));
        //求出圆心secondSpot圆上点homeopathicNeedleSpotRes0顺时针旋转40度的点
        Spot secondNeedleSpot40 = angleRotatePoint(startSSpot, secondNeedleSpotRes0, degreeToRadian((degreeSMax - degreeSMin) / 2));
        double secondDegree = radianToDegree(pointAcquisitionAngle(startSSpot, startESpot, secondNeedleSpot40));
        if (secondDegree > (degreeSMax - degreeSMin) / 2 + 1) {
            reductionSpot();
            computeRotate();
            Print.e("judgeS");
            return true;
        }
        return false;
    }

    private boolean judgeF() {
        double firstDegree = radianToDegree(pointAcquisitionAngle(startFSpot, endFSpot, levelSpot));
        if (firstDegree < degreeFMin) {
            reductionSpot();
            computeRotate();
            Print.e("judgeF");
            return true;
        }
        if (firstDegree > degreeFMax + 1) {
            reductionSpot();
            computeRotate();
            Print.e("judgeF");
            return true;
        }
        return false;
    }

    /**
     * 不能触地
     */
    private boolean cannotTouchLand() {
        if (pointToYDistance(endESpot.getY(), groundY)) {
            reductionSpot();
            computeRotate();
            return true;
        }
        if (pointToYDistance(endSSpot.getY(), groundY)) {
            reductionSpot();
            computeRotate();
            return true;
        }
        if (pointToYDistance(endFSpot.getY(), groundY)) {
            reductionSpot();
            computeRotate();
            return true;
        }
        return false;
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
                            backupSpot();
                            loadDrawSpot(spot);
                            drawSpot(canvas);
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
                            backupSpot();
                            mDrawInterface.rotatioReset(rotateF - rotateFTemp, rotateS - rotateSTemp, rotateE - rotateETemp, -1);
                            drawSpot(canvas);
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
     */
    public void touchMoveSpot(Spot spot) {
        if (spot.getX() > 10 && spot.getX() < 10 + resetBitmap.getHeight() && spot.getY() > 10 && spot.getY() < 10 + resetBitmap.getHeight()) {
            if (mDrawInterface != null) {
                mDrawInterface.onReset();
            }
        } else {
            if (!isDrawing) {
                isDrawing = true;
                // 通过 Message 参数将位置传给处理程序
                Message msg = Message.obtain(mReceiver, MSG_DRAW, spot);
                mReceiver.sendMessage(msg);
            }
        }
    }

    public void setSpotUp(Spot spot) {
        touchMoveSpot(spot);
        if (mDrawInterface != null) {
            mDrawInterface.onMotionEventUp();
        }
    }

    /**
     * 重置
     */
    public void reset() {
        if (System.currentTimeMillis() - curTime > 500) {
            curTime = System.currentTimeMillis();

            calculationPointFromAngle(30, 45, 90);
            computeRotate();
            mReceiver.sendEmptyMessage(MSG_DRAW);
        }
    }

    /**
     * 由角度绘制
     */
    public void onDrawAngle(double d1, double d2, double d3) {
        calculationPointFromAngle(d1, d2, d3);
        computeRotate();
        if (judgeModel()) return;
        mReceiver.sendEmptyMessage(MSG_DRAW);
    }

    public void onDrawF(double i) {
        calculationPointFromAngle(rotateF + i, rotateS, rotateE);
        computeRotate();
        backupSpot();
        if (judgeModel()) return;
        mReceiver.sendEmptyMessage(MSG_DRAW);

    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.mDrawInterface = drawInterface;
    }

    public void save() {
        PreferencesUtils.putString(mContext, "startFSpot", GsonUtil.GsonString(startFSpot));
        PreferencesUtils.putString(mContext, "endFSpot", GsonUtil.GsonString(endFSpot));
        PreferencesUtils.putString(mContext, "startSSpot", GsonUtil.GsonString(startSSpot));
        PreferencesUtils.putString(mContext, "endSSpot", GsonUtil.GsonString(endSSpot));
        PreferencesUtils.putString(mContext, "startESpot", GsonUtil.GsonString(startESpot));
        PreferencesUtils.putString(mContext, "endESpot", GsonUtil.GsonString(endESpot));
    }

    public Spot takeForKey(String key) {
        return GsonUtil.GsonToBean(PreferencesUtils.getString(mContext, key), Spot.class);
    }

    /**
     * 两园相交求交点坐标
     *
     * @param spot1
     * @param spot2
     * @return
     */
    private double[] circleCircleIntersection(Spot spot1, Spot spot2, double rS, double rE) {

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
            double A = (x1 * x1 - x2 * x2 + y1 * y1 - y2 * y2 + rE * rE - rS * rS) / (2 * (y1 - y2));
            double B = (x1 - x2) / (y1 - y2);
            a = 1 + B * B;
            b = -2 * (x1 + (A - y1) * B);
            c = x1 * x1 + (A - y1) * (A - y1) - rS * rS;
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
            x_1 = x_2 = (x1 * x1 - x2 * x2 + rE * rE - rS * rS) / (2 * (x1 - x2));
            a = 1;
            b = -2 * y1;
            c = y1 * y1 - rS * rS + (x_1 - x1) * (x_1 - x1);
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
     * @param rS     已知坐标的圆的半径
     * @param rE     未知坐标的圆的半径
     * @param spot   已知的坐标
     * @return 位置的坐标
     */
    private Spot getOuterCircle(Spot origin, double rS, double rE, Spot spot) {
        double x = spot.getX();
        double y = spot.getY();

        double x1 = x - origin.getX();
        double y1 = y - origin.getY();

        double x2 = x1 * rE / rS;
        double y2 = y1 * rE / rS;

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
     * 直线与y轴的角度
     */
    private double pointYAngle(Spot spot0, Spot spot1) {

        if (spot0 == null) {
            return 0;
        }
        double x0 = spot0.getX();
        double y0 = spot0.getY();
        double x1 = spot1.getX();
        double y1 = spot1.getY();

        double k1;

        if (x1 != x0) {
            k1 = (y1 - y0) / (x1 - x0);
        } else {
            k1 = Integer.MAX_VALUE;
        }
        return Math.atan(Math.abs(k1));

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
     * @param c     弧度
     * @return
     */
    private Spot radianRotatePoint(Spot spot0, Spot spot1, double c) {

        double x0 = spot0.getX();
        double y0 = spot0.getY();
        double x1 = spot1.getX();
        double y1 = spot1.getY();

        double x2 = (x1 - x0) * Math.cos(c) - (y1 - y0) * Math.sin(c) + x0;
        double y2 = (y1 - y0) * Math.cos(c) + (x1 - x0) * Math.sin(c) + y0;
        return new Spot((float) x2, (float) y2);
    }

    /**
     * @param spot0
     * @param spot1
     * @param c     角度
     * @return
     */
    private Spot angleRotatePoint(Spot spot0, Spot spot1, double c) {

        double x0 = spot0.getX();
        double y0 = spot0.getY();

        double x1 = spot1.getX();
        double y1 = spot1.getY();

        double x2 = (x1 - x0) * Math.cos(c) - (y1 - y0) * Math.sin(c) + x0;
        double y2 = (x1 - x0) * Math.sin(c) + (y1 - y0) * Math.cos(c) + y0;
        return new Spot((float) Math.abs(x2), (float) Math.abs(y2));
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
    private double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }


    /**
     * 角度换算成弧度
     *
     * @param degree
     * @return
     */
    private double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }


}

