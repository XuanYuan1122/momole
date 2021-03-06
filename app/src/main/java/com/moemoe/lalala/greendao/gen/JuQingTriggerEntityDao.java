package com.moemoe.lalala.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.moemoe.lalala.model.entity.JuQingTriggerEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "JU_QING_TRIGGER_ENTITY".
*/
public class JuQingTriggerEntityDao extends AbstractDao<JuQingTriggerEntity, String> {

    public static final String TABLENAME = "JU_QING_TRIGGER_ENTITY";

    /**
     * Properties of entity JuQingTriggerEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Extra = new Property(1, String.class, "extra", false, "EXTRA");
        public final static Property Force = new Property(2, boolean.class, "force", false, "FORCE");
        public final static Property Level = new Property(3, int.class, "level", false, "LEVEL");
        public final static Property RoleOf = new Property(4, String.class, "roleOf", false, "ROLE_OF");
        public final static Property StoryId = new Property(5, String.class, "storyId", false, "STORY_ID");
        public final static Property Type = new Property(6, String.class, "type", false, "TYPE");
        public final static Property ConditionStr = new Property(7, String.class, "conditionStr", false, "CONDITION_STR");
    }


    public JuQingTriggerEntityDao(DaoConfig config) {
        super(config);
    }
    
    public JuQingTriggerEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"JU_QING_TRIGGER_ENTITY\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"EXTRA\" TEXT," + // 1: extra
                "\"FORCE\" INTEGER NOT NULL ," + // 2: force
                "\"LEVEL\" INTEGER NOT NULL ," + // 3: level
                "\"ROLE_OF\" TEXT," + // 4: roleOf
                "\"STORY_ID\" TEXT," + // 5: storyId
                "\"TYPE\" TEXT," + // 6: type
                "\"CONDITION_STR\" TEXT);"); // 7: conditionStr
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"JU_QING_TRIGGER_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, JuQingTriggerEntity entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String extra = entity.getExtra();
        if (extra != null) {
            stmt.bindString(2, extra);
        }
        stmt.bindLong(3, entity.getForce() ? 1L: 0L);
        stmt.bindLong(4, entity.getLevel());
 
        String roleOf = entity.getRoleOf();
        if (roleOf != null) {
            stmt.bindString(5, roleOf);
        }
 
        String storyId = entity.getStoryId();
        if (storyId != null) {
            stmt.bindString(6, storyId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String conditionStr = entity.getConditionStr();
        if (conditionStr != null) {
            stmt.bindString(8, conditionStr);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, JuQingTriggerEntity entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String extra = entity.getExtra();
        if (extra != null) {
            stmt.bindString(2, extra);
        }
        stmt.bindLong(3, entity.getForce() ? 1L: 0L);
        stmt.bindLong(4, entity.getLevel());
 
        String roleOf = entity.getRoleOf();
        if (roleOf != null) {
            stmt.bindString(5, roleOf);
        }
 
        String storyId = entity.getStoryId();
        if (storyId != null) {
            stmt.bindString(6, storyId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String conditionStr = entity.getConditionStr();
        if (conditionStr != null) {
            stmt.bindString(8, conditionStr);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public JuQingTriggerEntity readEntity(Cursor cursor, int offset) {
        JuQingTriggerEntity entity = new JuQingTriggerEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // extra
            cursor.getShort(offset + 2) != 0, // force
            cursor.getInt(offset + 3), // level
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // roleOf
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // storyId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // type
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // conditionStr
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, JuQingTriggerEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setExtra(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setForce(cursor.getShort(offset + 2) != 0);
        entity.setLevel(cursor.getInt(offset + 3));
        entity.setRoleOf(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStoryId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setConditionStr(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final String updateKeyAfterInsert(JuQingTriggerEntity entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(JuQingTriggerEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(JuQingTriggerEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
