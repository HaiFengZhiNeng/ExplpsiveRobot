package com.example.explosiverobot.listener;

/**
 * Created by zhangyuanyuan on 2017/10/25.
 */

public interface DrawInterface {

    void rotatioCallbackn(double rotation1, double rotation2, double rotation3, double changeCistance);

    void rotatioReset(double rotation1, double rotation2, double rotation3, double changeCistance);

    void onMotionEventUp();

    void onReset();
}
