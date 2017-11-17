/*
 * Created by 岱青海蓝信息系统(北京)有限公司 on 17-7-3 下午3:56
 * Copyright (c) 2017. All rights reserved.
 */

package com.example.explosiverobot.modle;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dqhl on 17/7/3.
 */
@Entity
public class ActionTab {
    private String tab_id;
    private String tab_name;
    @Id(autoincrement = true)//该字段的类型为long或Long类型，autoincrement设置是否自动增长
    private Long id;
    @Generated(hash = 629106265)
    public ActionTab(String tab_id, String tab_name, Long id) {
        this.tab_id = tab_id;
        this.tab_name = tab_name;
        this.id = id;
    }
    @Generated(hash = 1400049625)
    public ActionTab() {
    }

    public ActionTab(String tab_id, String tab_name) {
        this.tab_id = tab_id;
        this.tab_name = tab_name;
    }

    public String getTab_id() {
        return this.tab_id;
    }
    public void setTab_id(String tab_id) {
        this.tab_id = tab_id;
    }
    public String getTab_name() {
        return this.tab_name;
    }
    public void setTab_name(String tab_name) {
        this.tab_name = tab_name;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

}
