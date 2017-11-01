package com.example.explosiverobot.util;

/**
 * Created by zhangyuanyuan on 2017/10/30.
 */

public class SPManager {
    //存储
    private static String mOrientation;
    private static String mSpeed;

    //包头
    public static String head = "FF";

    //操作码
    public static String control = "01";
    public static String readState = "02";
    public static String undetermined = "03";

    //操作目标
    public static String track = "01";
    public static String armMechanics = "02";
    public static String armObstacle = "03";
    public static String lamp = "04";

    //是否反馈
    public static String unTick = "00";

    //数据1
    //方向
    public static String trackFront = "02";
    public static String trackBack = "08";
    public static String trackLeft = "04";
    public static String trackRight = "06";
    public static String trackStop = "05";
    //机械臂选择
    public static String armMechanics01 = "01";
    public static String armMechanics02 = "02";
    public static String armMechanics03 = "03";
    public static String armMechanics04 = "04";
    public static String armMechanics05 = "05";
    public static String armMechanics06 = "06";
    //越障前臂角度
    public static String armObstacleAngle;
    //灯序号
    public static String lampNumFront = "01";
    public static String lampNumBack = "02";

    //数据2
    //速度
    public static String speedHigh = "01";
    public static String speedMedium = "02";
    public static String speedLow = "03";
    //机械臂角度
    public static String armMechanicAngle;
    //05
    public static String armMechanicStop05 = "00";
    public static String armMechanicClockwise05 = "01";
    public static String armMechanicAntiClockwise05 = "02";
    public static String armMechanicStop06 = "00";
    public static String armMechanicTight06 = "01";
    public static String armMechanicPine06 = "02";
    //越障后臂选择
    public static String armObstacleStop = "00";
    public static String armObstacleUp = "01";
    public static String armObstacleDown = "02";
    //等
    public static String lampOpen = "00";
    public static String lampClose = "01";

    public static void setTrackOrientation(String orientation) {
        mOrientation = orientation;
    }

    public static void setTrackSpeed(String speed) {
        mSpeed = speed;
    }

    /***************************灯的控制*****************************/
    /**
     * 照明灯前开
     */
    public static String controlLampFrontOpen() {
        return head + control + lamp + unTick + lampNumFront + lampOpen;
    }

    /**
     * 照明灯前关
     */
    public static String controlLampFrontClose() {
        return head + control + lamp + unTick + lampNumFront + lampClose;
    }

    /**
     * 照明灯后开
     */
    public static String controlLampBackOpen() {
        return head + control + lamp + unTick + lampNumBack + lampOpen;
    }

    /**
     * 照明灯后关
     */
    public static String controlLampBackClose() {
        return head + control + lamp + unTick + lampNumBack + lampClose;
    }

    /***************************越障臂控制*****************************/
    /**
     * 越障臂前臂旋转 angle 度
     *
     * @param angle
     */
    public static String controlarmObstacleStop(double angle) {
        return head + control + track + unTick + angle + armObstacleStop;
    }

    public static String controlarmObstacleUp(double angle) {
        return head + control + track + unTick + angle + armObstacleUp;
    }

    public static String controlarmObstacleDown(double angle) {
        return head + control + track + unTick + angle + armObstacleDown;
    }

    /**
     * 越障臂前臂停止
     */
    public static String controlarmObstacleStop() {
        return controlarmObstacleStop(0);
    }

    /**
     * 越障臂后臂向上
     */
    public static String controlarmObstacleUp() {
        return controlarmObstacleUp(0);
    }

    /**
     * 越障臂后臂向下
     */
    public static String controlarmObstacleDown() {
        return controlarmObstacleDown(0);
    }


    /***************************机械臂控制*****************************/
    /**
     * 控制机械臂旋转
     *
     * @param number 机械臂
     * @param angle  为正 顺时针，为负 逆时针
     */
    public static String controlarmMechanics(String number, double angle) {
        return head + control + track + unTick + number + angle;
    }

    /**
     * 机械臂05 停止旋转
     */
    public static String controlarmMechanicStop05() {
        return head + control + track + unTick + armMechanics05 + armMechanicStop05;
    }

    /**
     * 机械臂05 顺时针旋转
     */
    public static String controlarmMechanicClockwise05() {
        return head + control + track + unTick + armMechanics05 + armMechanicClockwise05;
    }

    /**
     * 机械臂05 逆时针旋转
     */
    public static String controlarmMechanicAntiClockwise05() {
        return head + control + track + unTick + armMechanics05 + armMechanicAntiClockwise05;
    }

    /**
     * 机械臂06 停止抓紧
     */
    public static String controlarmMechanicStop06() {
        return head + control + track + unTick + armMechanics06 + armMechanicStop06;
    }

    /**
     * 机械臂06 紧
     */
    public static String controlarmMechanicTight06() {
        return head + control + track + unTick + armMechanics06 + armMechanicTight06;
    }

    /**
     * 机械臂06 松
     */
    public static String controlarmMechanicPine06() {
        return head + control + track + unTick + armMechanics06 + armMechanicPine06;
    }


    /***************************履带控制*****************************/
    public static String controlTrack(String orientation, String speed) {
        mOrientation = orientation;
        mSpeed = speed;
        return head + control + track + unTick + orientation + speed;
    }

    public static String controlTrackOrientation(String orientation) {
        return controlTrack(orientation, mSpeed);
    }

    public static String controlTrackFront() {
        return controlTrack(trackFront, mSpeed);
    }

    public static String controlTrackBack() {
        return controlTrack(trackBack, mSpeed);
    }

    public static String controlTrackLeft() {
        return controlTrack(trackLeft, mSpeed);
    }

    public static String controlTrackRight() {
        return controlTrack(trackRight, mSpeed);
    }

    public static String controlTrackStop() {
        return controlTrack(trackStop, mSpeed);
    }


    public static String controlTrackSpeedHigh() {
        return controlTrack(mOrientation, speedHigh);
    }

    public static String controlTrackSpeedMedium() {
        return controlTrack(mOrientation, speedMedium);
    }

    public static String controlTrackSpeedLow() {
        return controlTrack(mOrientation, speedLow);
    }

    public static String controlTrackSpeedHigh(String orientation) {
        return controlTrack(orientation, speedHigh);
    }

    public static String controlTrackSpeedMedium(String orientation) {
        return controlTrack(orientation, speedMedium);
    }

    public static String controlTrackSpeedLow(String orientation) {
        return controlTrack(orientation, speedLow);
    }


}
