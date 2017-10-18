package com.example.explosiverobot.db.manager;

import com.example.explosiverobot.db.base.BaseManager;
import com.example.explosiverobot.modle.ChatMessageBean;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */

public class ChatDbManager extends BaseManager<ChatMessageBean, Long> {
    @Override
    public AbstractDao<ChatMessageBean, Long> getAbstractDao() {
        return daoSession.getChatMessageBeanDao();
    }
}
