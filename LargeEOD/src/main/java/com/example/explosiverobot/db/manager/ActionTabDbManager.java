package com.example.explosiverobot.db.manager;

import com.example.explosiverobot.db.ActionTabDao;
import com.example.explosiverobot.db.base.BaseManager;
import com.example.explosiverobot.modle.ActionTab;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by dell on 2017/10/20.
 */

public class ActionTabDbManager extends BaseManager<ActionTab, Long> {
    @Override
    public AbstractDao<ActionTab, Long> getAbstractDao() {
        return daoSession.getActionTabDao();
    }

    public List<ActionTab> queryByTabName(String name) {
        Query<ActionTab> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(ActionTabDao.Properties.Tab_name.eq(name))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }
}
