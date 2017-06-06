package com.moemoe.lalala.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.moemoe.lalala.model.entity.PrivateMessageItemEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRIVATE_MESSAGE_ITEM_ENTITY".
*/
public class PrivateMessageItemEntityDao extends AbstractDao<PrivateMessageItemEntity, String> {

    public static final String TABLENAME = "PRIVATE_MESSAGE_ITEM_ENTITY";

    /**
     * Properties of entity PrivateMessageItemEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property TalkId = new Property(0, String.class, "talkId", true, "TALK_ID");
        public final static Property Icon = new Property(1, String.class, "icon", false, "ICON");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property UpdateTime = new Property(4, java.util.Date.class, "updateTime", false, "UPDATE_TIME");
        public final static Property Dot = new Property(5, Integer.class, "dot", false, "DOT");
        public final static Property IsNew = new Property(6, boolean.class, "isNew", false, "IS_NEW");
        public final static Property State = new Property(7, boolean.class, "state", false, "STATE");
    }


    public PrivateMessageItemEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PrivateMessageItemEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRIVATE_MESSAGE_ITEM_ENTITY\" (" + //
                "\"TALK_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: talkId
                "\"ICON\" TEXT," + // 1: icon
                "\"NAME\" TEXT," + // 2: name
                "\"CONTENT\" TEXT," + // 3: content
                "\"UPDATE_TIME\" INTEGER," + // 4: updateTime
                "\"DOT\" INTEGER," + // 5: dot
                "\"IS_NEW\" INTEGER NOT NULL ," + // 6: isNew
                "\"STATE\" INTEGER NOT NULL );"); // 7: state
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRIVATE_MESSAGE_ITEM_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PrivateMessageItemEntity entity) {
        stmt.clearBindings();
 
        String talkId = entity.getTalkId();
        if (talkId != null) {
            stmt.bindString(1, talkId);
        }
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(2, icon);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(5, updateTime.getTime());
        }
 
        Integer dot = entity.getDot();
        if (dot != null) {
            stmt.bindLong(6, dot);
        }
        stmt.bindLong(7, entity.getIsNew() ? 1L: 0L);
        stmt.bindLong(8, entity.getState() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PrivateMessageItemEntity entity) {
        stmt.clearBindings();
 
        String talkId = entity.getTalkId();
        if (talkId != null) {
            stmt.bindString(1, talkId);
        }
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(2, icon);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(5, updateTime.getTime());
        }
 
        Integer dot = entity.getDot();
        if (dot != null) {
            stmt.bindLong(6, dot);
        }
        stmt.bindLong(7, entity.getIsNew() ? 1L: 0L);
        stmt.bindLong(8, entity.getState() ? 1L: 0L);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public PrivateMessageItemEntity readEntity(Cursor cursor, int offset) {
        PrivateMessageItemEntity entity = new PrivateMessageItemEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // talkId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // icon
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // updateTime
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // dot
            cursor.getShort(offset + 6) != 0, // isNew
            cursor.getShort(offset + 7) != 0 // state
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PrivateMessageItemEntity entity, int offset) {
        entity.setTalkId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setIcon(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUpdateTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setDot(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setIsNew(cursor.getShort(offset + 6) != 0);
        entity.setState(cursor.getShort(offset + 7) != 0);
     }
    
    @Override
    protected final String updateKeyAfterInsert(PrivateMessageItemEntity entity, long rowId) {
        return entity.getTalkId();
    }
    
    @Override
    public String getKey(PrivateMessageItemEntity entity) {
        if(entity != null) {
            return entity.getTalkId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PrivateMessageItemEntity entity) {
        return entity.getTalkId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
