package com.example.explosiverobot.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.explosiverobot.modle.ActionItem;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACTION_ITEM".
*/
public class ActionItemDao extends AbstractDao<ActionItem, Long> {

    public static final String TABLENAME = "ACTION_ITEM";

    /**
     * Properties of entity ActionItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Item_id = new Property(0, String.class, "item_id", false, "ITEM_ID");
        public final static Property Item_name = new Property(1, String.class, "item_name", false, "ITEM_NAME");
        public final static Property Item_pic = new Property(2, String.class, "item_pic", false, "ITEM_PIC");
        public final static Property Item_isOpen = new Property(3, String.class, "item_isOpen", false, "ITEM_IS_OPEN");
        public final static Property Item_group = new Property(4, String.class, "item_group", false, "ITEM_GROUP");
        public final static Property Type = new Property(5, int.class, "type", false, "TYPE");
        public final static Property Id = new Property(6, Long.class, "id", true, "_id");
    }


    public ActionItemDao(DaoConfig config) {
        super(config);
    }
    
    public ActionItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACTION_ITEM\" (" + //
                "\"ITEM_ID\" TEXT," + // 0: item_id
                "\"ITEM_NAME\" TEXT," + // 1: item_name
                "\"ITEM_PIC\" TEXT," + // 2: item_pic
                "\"ITEM_IS_OPEN\" TEXT," + // 3: item_isOpen
                "\"ITEM_GROUP\" TEXT," + // 4: item_group
                "\"TYPE\" INTEGER NOT NULL ," + // 5: type
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT );"); // 6: id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACTION_ITEM\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ActionItem entity) {
        stmt.clearBindings();
 
        String item_id = entity.getItem_id();
        if (item_id != null) {
            stmt.bindString(1, item_id);
        }
 
        String item_name = entity.getItem_name();
        if (item_name != null) {
            stmt.bindString(2, item_name);
        }
 
        String item_pic = entity.getItem_pic();
        if (item_pic != null) {
            stmt.bindString(3, item_pic);
        }
 
        String item_isOpen = entity.getItem_isOpen();
        if (item_isOpen != null) {
            stmt.bindString(4, item_isOpen);
        }
 
        String item_group = entity.getItem_group();
        if (item_group != null) {
            stmt.bindString(5, item_group);
        }
        stmt.bindLong(6, entity.getType());
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(7, id);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ActionItem entity) {
        stmt.clearBindings();
 
        String item_id = entity.getItem_id();
        if (item_id != null) {
            stmt.bindString(1, item_id);
        }
 
        String item_name = entity.getItem_name();
        if (item_name != null) {
            stmt.bindString(2, item_name);
        }
 
        String item_pic = entity.getItem_pic();
        if (item_pic != null) {
            stmt.bindString(3, item_pic);
        }
 
        String item_isOpen = entity.getItem_isOpen();
        if (item_isOpen != null) {
            stmt.bindString(4, item_isOpen);
        }
 
        String item_group = entity.getItem_group();
        if (item_group != null) {
            stmt.bindString(5, item_group);
        }
        stmt.bindLong(6, entity.getType());
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(7, id);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6);
    }    

    @Override
    public ActionItem readEntity(Cursor cursor, int offset) {
        ActionItem entity = new ActionItem( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // item_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // item_name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // item_pic
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // item_isOpen
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // item_group
            cursor.getInt(offset + 5), // type
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ActionItem entity, int offset) {
        entity.setItem_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setItem_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setItem_pic(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setItem_isOpen(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setItem_group(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setType(cursor.getInt(offset + 5));
        entity.setId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ActionItem entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ActionItem entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ActionItem entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
