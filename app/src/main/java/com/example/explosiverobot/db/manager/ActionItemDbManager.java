package com.example.explosiverobot.db.manager;

import com.example.explosiverobot.db.ActionItemDao;
import com.example.explosiverobot.db.ActionTabDao;
import com.example.explosiverobot.db.base.BaseManager;
import com.example.explosiverobot.modle.ActionItem;
import com.example.explosiverobot.modle.ActionTab;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by dell on 2017/10/21.
 */

public class ActionItemDbManager extends BaseManager<ActionItem, Long> {
    @Override
    public AbstractDao<ActionItem, Long> getAbstractDao() {
        return daoSession.getActionItemDao();
    }

    public List<ActionItem> queryByItemName(String name) {
        Query<ActionItem> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(ActionItemDao.Properties.Item_group.eq(name))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }
}
