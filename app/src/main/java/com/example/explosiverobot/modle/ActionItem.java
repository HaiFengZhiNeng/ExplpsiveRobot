package com.example.explosiverobot.modle;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dell on 2017/10/21.
 */
@Entity
public class ActionItem {
    private String item_id;
    private String item_name;
    private String item_pic;
    private String item_isOpen;
    @Id(autoincrement = true)//该字段的类型为long或Long类型，autoincrement设置是否自动增长
    private Long id;

    @Generated(hash = 191247622)
    public ActionItem(String item_id, String item_name, String item_pic,
                      String item_isOpen, Long id) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_pic = item_pic;
        this.item_isOpen = item_isOpen;
        this.id = id;
    }

    @Generated(hash = 1133867744)
    public ActionItem() {
    }

    public ActionItem(String item_name, String item_pic, String item_isOpen) {
        this.item_name = item_name;
        this.item_pic = item_pic;
        this.item_isOpen = item_isOpen;
    }

    public String getItem_id() {
        return this.item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return this.item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_pic() {
        return this.item_pic;
    }

    public void setItem_pic(String item_pic) {
        this.item_pic = item_pic;
    }

    public String getItem_isOpen() {
        return this.item_isOpen;
    }

    public void setItem_isOpen(String item_isOpen) {
        this.item_isOpen = item_isOpen;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
