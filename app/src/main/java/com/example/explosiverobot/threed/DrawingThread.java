package com.example.explosiverobot.threed;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.math.BigDecimal;

/**
 * Created by zhangyuanyuan on 2017/9/28.
 */

public class DrawingThread extends HandlerThread implements Handler.Callback {

    private static final int MSG_DRAW = 101;

    private SurfaceHolder mDrawingSurface;
    private Paint mPaint;
    private Handler mReceiver;

    // 定义一个记录图像是否开始渲染的旗帜
    private boolean mRunning;

    private int mScreenW;        //屏幕宽度
    private int mScreenH;        //屏幕高度
    /**
     * 第一条线的长度
     */
    private int r0 = 400;
    /**
     * 第二条线的长度
     */
    private int r1 = 300;
    /**
     * 第三条线的长度
     */
    private int r2 = 200;
    /**
     * 定义触摸的范围
     */
    private int rtouch = 70;
    /**
     * 固定点坐标
     */
    private Spot originSpot;
    /**
     * 第一条线末端位置，第二条线的圆心
     */
    private Spot firstSpot;
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

    /**
     * 回调接口
     */
    private DrawInterface mDrawInterface;

    public DrawingThread(SurfaceHolder holder, int screenW, int screenH) {
        super("DrawingThread");
        mDrawingSurface = holder;
        mScreenW = screenW;
        mScreenH = screenH;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        mPaint.setTextSize(30);
        mPaint.setColor(Color.rgb(255, 203, 15));
        mPaint.setTextAlign(Paint.Align.LEFT);


        originSpot = new Spot((float) (screenW * 0.3), (float) (screenH * 0.7));

        firstSpot = new Spot((float) (screenW * 0.3) + r0, (float) (screenH * 0.7));

        secondSpot = new Spot((float) (screenW * 0.3) + r0, (float) (screenH * 0.7));
        endingSpot = new Spot((float) (screenW * 0.3) + r0, (float) (screenH * 0.7) - r1);
        touchSpot = new Spot((float) (screenW * 0.3) + r0 + r2, (float) (screenH * 0.7) - r1);

    }

    /**
     * 绘制图形
     *
     * @param canvas
     */
    private void drawSpot(Canvas canvas) {

        canvas.drawLine(secondSpot.getX(), secondSpot.getY(), endingSpot.getX(), endingSpot.getY(), mPaint);
        canvas.drawLine(endingSpot.getX(), endingSpot.getY(), touchSpot.getX(), touchSpot.getY(), mPaint);
        canvas.drawLine(originSpot.getX(), originSpot.getY(), firstSpot.getX(), firstSpot.getY(), mPaint);

        canvas.drawCircle(originSpot.getX(), originSpot.getY(), rtouch, mPaint);
    }

    private void drawSpot(Canvas canvas, Spot spot) {

        Spot temporaryendingSpot = endingSpot;
        Spot temporaryTouchSpot = touchSpot;

        double rotation1 = 0.0;
        double rotation2 = 0.0;
        double rotation3 = 0.0;
        //点击的点到第三条线末端的位置
        double distanceTouch = getDistance(spot, touchSpot);
        //点击的点到第二个圆中心点的距离
        double distanceMax = getDistance(spot, secondSpot);

        //触摸点再第三条线的触摸范围之内，且再最大半径以内
        if (distanceTouch < rtouch && distanceMax < r1 + r2 && distanceMax > rtouch) {
            //控件末端就是触摸点
            touchSpot = spot;
            //计算以第二圆和第三圆的交点
            double[] intersection = intersect(secondSpot, touchSpot);
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
            rotation3 = pointAcquisitionAngle(endingSpot, temporaryTouchSpot, touchSpot);
            rotation2 = positiveNegative(secondSpot, temporaryendingSpot, endingSpot) ? rotation2 : -rotation2;
            rotation3 = positiveNegative(endingSpot, temporaryTouchSpot, touchSpot) ? rotation3 : -rotation3;
        } else if (distanceMax > r1 + r2 && distanceMax < r1 + r2 + rtouch) {//触摸点再最大半径之外再可控范围之内
            //交点 同心圆坐标求解
            endingSpot = getOuterCircle(secondSpot, r2 + r1, r1, spot);
            //尾点
            touchSpot = getOuterCircle(secondSpot, r1, r2 + r1, endingSpot);

            rotation2 = pointAcquisitionAngle(secondSpot, temporaryendingSpot, endingSpot);
            rotation3 = pointAcquisitionAngle(endingSpot, temporaryTouchSpot, touchSpot);
            rotation2 = positiveNegative(secondSpot, temporaryendingSpot, endingSpot) ? rotation2 : -rotation2;
            rotation3 = positiveNegative(endingSpot, temporaryTouchSpot, touchSpot) ? rotation3 : -rotation3;
        } else if (distanceMax < rtouch) {//接近第二个圆
            //触摸点到中心点的距离
            double distanceb = getDistance(originSpot, spot);
            //由触摸点得到在中心圆上实际点的坐标
            Spot temporarySpot = getOuterCircle(originSpot, (int) distanceb, r0, spot);
            //由中心圆实际点的坐标计算出应该旋转的角度
            double rotation = pointAcquisitionAngle(originSpot, firstSpot, temporarySpot);
            //触摸点与原点的差值
            float dx = spot.getX() - firstSpot.getX();
            float dy = spot.getY() - firstSpot.getY();
            //差值大于1再进行判断
            if (Math.abs(dx) > 1 && Math.abs(dy) > 1) {
                //判断触摸的方向
                int orientation = getOrientation(dx, dy);
                //判断旋转方向
                rotation = rotationDirection(spot, rotation, orientation);
                //由角度计算出个点位置
                firstSpot = angleAcquisitionPoint(originSpot, firstSpot, rotation);
                secondSpot = angleAcquisitionPoint(originSpot, secondSpot, rotation);
                touchSpot = angleAcquisitionPoint(originSpot, touchSpot, rotation);
                endingSpot = angleAcquisitionPoint(originSpot, endingSpot, rotation);

                rotation1 = rotation;
            }
        }

        drawSpot(canvas);
        if(mDrawInterface != null) {
            mDrawInterface.rotatioCallbackn(rotation1, rotation2, rotation3);
        }

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
                    }
                } else {
                    // 锁定 SurfaceView，并返回到要绘图的 Canvas
                    Canvas canvas = mDrawingSurface.lockCanvas();
                    if (canvas == null) {
                        break;
                    }
                    try {
                        synchronized (mDrawingSurface) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                            drawSpot(canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // 解锁 Canvas，并渲染当前的图像
                        mDrawingSurface.unlockCanvasAndPost(canvas);
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
        // 通过 Message 参数将位置传给处理程序
        Message msg = Message.obtain(mReceiver, MSG_DRAW, spot);
        mReceiver.sendMessage(msg);
    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.mDrawInterface = drawInterface;
    }

    /**
     * 两园相交求交点坐标
     *
     * @param spot1
     * @param sot2
     * @return
     */
    private double[] intersect(Spot spot1, Spot sot2) {

        double x1 = spot1.getX();
        double y1 = spot1.getY();
        double x2 = sot2.getX();
        double y2 = sot2.getY();
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
    private Spot getOuterCircle(Spot origin, double r1, int r2, Spot spot) {
        float x = spot.getX();
        float y = spot.getY();

        float x1 = x - origin.getX();
        float y1 = y - origin.getY();

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

        float x0 = spot0.getX();
        float y0 = spot0.getY();
        float x1 = spot1.getX();
        float y1 = spot1.getY();
        float x2 = spot2.getX();
        float y2 = spot2.getY();

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
        return Math.atan(Math.abs(k1 - k2) / (1 + k1 * k2));

    }

    /**
     * 圆心为spot0 的圆上点spot1 旋转角度c后的点的位置
     * @param spot0
     * @param spot1
     * @param c
     * @return
     */
    private Spot angleAcquisitionPoint(Spot spot0, Spot spot1, double c) {

        float x0 = spot0.getX();
        float y0 = spot0.getY();
        float x1 = spot1.getX();
        float y1 = spot1.getY();

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
    private int getOrientation(float dx, float dy) {
        if (Math.abs(dx) < Math.abs(dy)) {
            return dy > 0 ? 'b' : 't';
        } else {
            return dx > 0 ? 'r' : 'l';
        }
    }

    /**
     * 判断旋转方向
     *
     * @param spot        触摸点
     * @param rotation
     * @param orientation 角度
     * @return
     */
    private double rotationDirection(Spot spot, double rotation, int orientation) {
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
                    Log.e("3r", rotation + " ");
                    break;
                case 'l':
                    if (firstSpot.getX() - originSpot.getX() > 0) {///实际点在第四象限
                        rotation = -rotation;
                    } else {
                        rotation = rotation;
                    }
                    Log.e("3l", rotation + " ");
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
                case 'l':
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
        float x0 = spot0.getX();
        float y0 = spot0.getY();

        float x1 = spot1.getX();
        float y1 = spot1.getY();

        float x2 = spot2.getX();
        float y2 = spot2.getY();

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

}
