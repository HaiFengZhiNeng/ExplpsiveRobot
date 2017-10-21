package com.example.explosiverobot.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.explosiverobot.modle.ActionTab;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACTION_TAB".
*/
public class ActionTabDao extends AbstractDao<ActionTab, Long> {

    public static final String TABLENAME = "ACTION_TAB";

    /**
     * Properties of entity ActionTab.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Tab_id = new Property(0, String.class, "tab_id", false, "TAB_ID");
        public final static Property Tab_name = new Property(1, String.class, "tab_name", false, "TAB_NAME");
        public final static Property Id = new Property(2, Long.class, "id", true, "_id");
    }


    public ActionTabDao(DaoConfig config) {
        super(config);
    }
    
    public ActionTabDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACTION_TAB\" (" + //
                "\"TAB_ID\" TEXT," + // 0: tab_id
                "\"TAB_NAME\" TEXT," + // 1: tab_name
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT );"); // 2: id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACTION_TAB\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ActionTab entity) {
        stmt.clearBindings();
 
        String tab_id = entity.getTab_id();
        if (tab_id != null) {
            stmt.bindString(1, tab_id);
        }
 
        String tab_name = entity.getTab_name();
        if (tab_name != null) {
            stmt.bindString(2, tab_name);
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(3, id);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ActionTab entity) {
        stmt.clearBindings();
 
        String tab_id = entity.getTab_id();
        if (tab_id != null) {
            stmt.bindString(1, tab_id);
        }
 
        String tab_name = entity.getTab_name();
        if (tab_name != null) {
            stmt.bindString(2, tab_name);
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(3, id);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2);
    }    

    @Override
    public ActionTab readEntity(Cursor cursor, int offset) {
        ActionTab entity = new ActionTab( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // tab_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // tab_name
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ActionTab entity, int offset) {
        entity.setTab_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTab_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ActionTab entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ActionTab entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ActionTab entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
