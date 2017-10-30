package com.example.explosiverobot.util;

/**
 * Created by zhangyuanyuan on 2017/10/30.
 */

public class SPManager {
    //存储
    private static String mOrientation;

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
    //越障后臂选择
    public static String armObstacleStop = "00";
    public static String armObstacleUp = "01";
    public static String armObstacleDown = "02";
    //等
    public static String lampOpen = "00";
    public static String lampClose = "01";

    /***************************灯的控制*****************************/
    public static String controlLampFrontOpen() {
        return head + control + lamp + unTick + lampNumFront + lampOpen;
    }

    public static String controlLampFrontClose() {
        return head + control + lamp + unTick + lampNumFront + lampClose;
    }

    public static String controlLampBackOpen() {
        return head + control + lamp + unTick + lampNumBack + lampOpen;
    }

    public static String controlLampBackClose() {
        return head + control + lamp + unTick + lampNumBack + lampClose;
    }

    /***************************越障臂控制*****************************/
    public static String controlarmObstacleStop(double angle) {
        return head + control + track + unTick + angle + armObstacleStop;
    }

    public static String controlarmObstacleUp(double angle) {
        return head + control + track + unTick + angle + armObstacleUp;
    }

    public static String controlarmObstacleDown(double angle) {
        return head + control + track + unTick + angle + armObstacleDown;
    }


    /***************************机械臂控制*****************************/
    public static String controlarmMechanics(String number, double angle) {
        return head + control + track + unTick + number + angle;
    }


    /***************************履带控制*****************************/
    public static String controlTrack(String orientation, String speed) {
        mOrientation = orientation;
        return head + control + track + unTick + orientation + speed;
    }

    public static String controlTrackSpeed(String speed) {
        return head + control + track + unTick + mOrientation + speed;
    }

    public static String controlTrackSpeedHigh() {
        return head + control + track + unTick + mOrientation + speedHigh;
    }

    public static String controlTrackSpeedMedium() {
        return head + control + track + unTick + mOrientation + speedMedium;
    }

    public static String controlTrackSpeedLow() {
        return head + control + track + unTick + mOrientation + speedLow;
    }

    public static String controlTrackSpeedHigh(String orientation) {
        mOrientation = orientation;
        return head + control + track + unTick + orientation + speedHigh;
    }

    public static String controlTrackSpeedMedium(String orientation) {
        mOrientation = orientation;
        return head + control + track + unTick + orientation + speedMedium;
    }

    public static String controlTrackSpeedLow(String orientation) {
        mOrientation = orientation;
        return head + control + track + unTick + orientation + speedLow;
    }


}
