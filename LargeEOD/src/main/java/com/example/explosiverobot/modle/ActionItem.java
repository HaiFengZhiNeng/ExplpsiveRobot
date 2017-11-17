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
    private String item_isOpen;//是否打开手臂
    private String item_group;// 该行为属于哪个分组下
    private int type;
    @Id(autoincrement = true)//该字段的类型为long或Long类型，autoincrement设置是否自动增长
    private Long id;

    @Generated(hash = 141556185)
    public ActionItem(String item_id, String item_name, String item_pic, String item_isOpen,
                      String item_group, int type, Long id) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_pic = item_pic;
        this.item_isOpen = item_isOpen;
        this.item_group = item_group;
        this.type = type;
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

    public ActionItem(String item_name, String item_pic, String item_isOpen, String item_group, int type) {
        this.item_name = item_name;
        this.item_pic = item_pic;
        this.item_isOpen = item_isOpen;
        this.item_group = item_group;
        this.type = type;
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

    public String getItem_group() {
        return this.item_group;
    }

    public void setItem_group(String item_group) {
        this.item_group = item_group;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
