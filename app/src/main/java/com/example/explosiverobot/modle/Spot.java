package com.example.explosiverobot.modle;

/**
 * Created by zhangyuanyuan on 2017/10/24.
 */

public class Spot {

    private double x;
    private double y;

    public Spot() {
    }

    public Spot(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Spot{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
