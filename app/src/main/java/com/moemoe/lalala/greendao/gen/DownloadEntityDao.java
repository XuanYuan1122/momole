package com.moemoe.lalala.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.moemoe.lalala.model.entity.DownloadEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DOWNLOAD_ENTITY".
*/
public class DownloadEntityDao extends AbstractDao<DownloadEntity, Long> {

    public static final String TABLENAME = "DOWNLOAD_ENTITY";

    /**
     * Properties of entity DownloadEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Url = new Property(1, String.class, "url", false, "URL");
        public final static Property Path = new Property(2, String.class, "path", false, "PATH");
        public final static Property FileName = new Property(3, String.class, "fileName", false, "FILE_NAME");
        public final static Property DirPath = new Property(4, String.class, "dirPath", false, "DIR_PATH");
        public final static Property Type = new Property(5, String.class, "type", false, "TYPE");
    }


    public DownloadEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DownloadEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DOWNLOAD_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"URL\" TEXT," + // 1: url
                "\"PATH\" TEXT," + // 2: path
                "\"FILE_NAME\" TEXT," + // 3: fileName
                "\"DIR_PATH\" TEXT," + // 4: dirPath
                "\"TYPE\" TEXT);"); // 5: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DOWNLOAD_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DownloadEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(2, url);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(3, path);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(4, fileName);
        }
 
        String dirPath = entity.getDirPath();
        if (dirPath != null) {
            stmt.bindString(5, dirPath);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DownloadEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(2, url);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(3, path);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(4, fileName);
        }
 
        String dirPath = entity.getDirPath();
        if (dirPath != null) {
            stmt.bindString(5, dirPath);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DownloadEntity readEntity(Cursor cursor, int offset) {
        DownloadEntity entity = new DownloadEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // url
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // path
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // fileName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // dirPath
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // type
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DownloadEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFileName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDirPath(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DownloadEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DownloadEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DownloadEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}