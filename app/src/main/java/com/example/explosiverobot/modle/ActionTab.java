/*
 * Created by 岱青海蓝信息系统(北京)有限公司 on 17-7-3 下午3:56
 * Copyright (c) 2017. All rights reserved.
 */

package com.example.explosiverobot.modle;

/**
 * Created by dqhl on 17/7/3.
 */
public class ActionTab {
    private String id;
    private String name;

    public ActionTab(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "LiveTab{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
