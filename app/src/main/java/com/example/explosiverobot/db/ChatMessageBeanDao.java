package com.example.explosiverobot.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.explosiverobot.modle.ChatMessageBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_MESSAGE_BEAN".
*/
public class ChatMessageBeanDao extends AbstractDao<ChatMessageBean, Long> {

    public static final String TABLENAME = "CHAT_MESSAGE_BEAN";

    /**
     * Properties of entity ChatMessageBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Messagetype = new Property(1, int.class, "messagetype", false, "messagetype");
        public final static Property MessageContent = new Property(2, String.class, "messageContent", false, "messageContent");
        public final static Property Time = new Property(3, String.class, "time", false, "time");
        public final static Property SendState = new Property(4, int.class, "sendState", false, "sendState");
        public final static Property ImageUrl = new Property(5, String.class, "imageUrl", false, "imageUrl");
        public final static Property ImageLocal = new Property(6, String.class, "imageLocal", false, "imageLocal");
        public final static Property VoiceAnswer = new Property(7, String.class, "voiceAnswer", false, "voiceAnswer");
        public final static Property Action = new Property(8, String.class, "action", false, "action");
        public final static Property Expression = new Property(9, String.class, "expression", false, "expression");
        public final static Property ActionData = new Property(10, String.class, "actionData", false, "actionData");
        public final static Property ExpressionData = new Property(11, String.class, "expressionData", false, "expressionData");
        public final static Property VideoName = new Property(12, String.class, "videoName", false, "videoName");
        public final static Property VideoUrl = new Property(13, String.class, "videoUrl", false, "videoUrl");
    }


    public ChatMessageBeanDao(DaoConfig config) {
        super(config);
    }
    
    public ChatMessageBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_MESSAGE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"messagetype\" INTEGER NOT NULL ," + // 1: messagetype
                "\"messageContent\" TEXT," + // 2: messageContent
                "\"time\" TEXT," + // 3: time
                "\"sendState\" INTEGER NOT NULL ," + // 4: sendState
                "\"imageUrl\" TEXT," + // 5: imageUrl
                "\"imageLocal\" TEXT," + // 6: imageLocal
                "\"voiceAnswer\" TEXT," + // 7: voiceAnswer
                "\"action\" TEXT," + // 8: action
                "\"expression\" TEXT," + // 9: expression
                "\"actionData\" TEXT," + // 10: actionData
                "\"expressionData\" TEXT," + // 11: expressionData
                "\"videoName\" TEXT," + // 12: videoName
                "\"videoUrl\" TEXT);"); // 13: videoUrl
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_MESSAGE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatMessageBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMessagetype());
 
        String messageContent = entity.getMessageContent();
        if (messageContent != null) {
            stmt.bindString(3, messageContent);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(4, time);
        }
        stmt.bindLong(5, entity.getSendState());
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(6, imageUrl);
        }
 
        String imageLocal = entity.getImageLocal();
        if (imageLocal != null) {
            stmt.bindString(7, imageLocal);
        }
 
        String voiceAnswer = entity.getVoiceAnswer();
        if (voiceAnswer != null) {
            stmt.bindString(8, voiceAnswer);
        }
 
        String action = entity.getAction();
        if (action != null) {
            stmt.bindString(9, action);
        }
 
        String expression = entity.getExpression();
        if (expression != null) {
            stmt.bindString(10, expression);
        }
 
        String actionData = entity.getActionData();
        if (actionData != null) {
            stmt.bindString(11, actionData);
        }
 
        String expressionData = entity.getExpressionData();
        if (expressionData != null) {
            stmt.bindString(12, expressionData);
        }
 
        String videoName = entity.getVideoName();
        if (videoName != null) {
            stmt.bindString(13, videoName);
        }
 
        String videoUrl = entity.getVideoUrl();
        if (videoUrl != null) {
            stmt.bindString(14, videoUrl);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatMessageBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMessagetype());
 
        String messageContent = entity.getMessageContent();
        if (messageContent != null) {
            stmt.bindString(3, messageContent);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(4, time);
        }
        stmt.bindLong(5, entity.getSendState());
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(6, imageUrl);
        }
 
        String imageLocal = entity.getImageLocal();
        if (imageLocal != null) {
            stmt.bindString(7, imageLocal);
        }
 
        String voiceAnswer = entity.getVoiceAnswer();
        if (voiceAnswer != null) {
            stmt.bindString(8, voiceAnswer);
        }
 
        String action = entity.getAction();
        if (action != null) {
            stmt.bindString(9, action);
        }
 
        String expression = entity.getExpression();
        if (expression != null) {
            stmt.bindString(10, expression);
        }
 
        String actionData = entity.getActionData();
        if (actionData != null) {
            stmt.bindString(11, actionData);
        }
 
        String expressionData = entity.getExpressionData();
        if (expressionData != null) {
            stmt.bindString(12, expressionData);
        }
 
        String videoName = entity.getVideoName();
        if (videoName != null) {
            stmt.bindString(13, videoName);
        }
 
        String videoUrl = entity.getVideoUrl();
        if (videoUrl != null) {
            stmt.bindString(14, videoUrl);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ChatMessageBean readEntity(Cursor cursor, int offset) {
        ChatMessageBean entity = new ChatMessageBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // messagetype
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // messageContent
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // time
            cursor.getInt(offset + 4), // sendState
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // imageUrl
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // imageLocal
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // voiceAnswer
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // action
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // expression
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // actionData
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // expressionData
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // videoName
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // videoUrl
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatMessageBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMessagetype(cursor.getInt(offset + 1));
        entity.setMessageContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSendState(cursor.getInt(offset + 4));
        entity.setImageUrl(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setImageLocal(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setVoiceAnswer(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAction(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setExpression(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setActionData(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setExpressionData(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setVideoName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setVideoUrl(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChatMessageBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChatMessageBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatMessageBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
