package com.app.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.app.Utils;
import com.app.common.util.IOUtil;
import com.app.common.util.KeyValue;
import com.app.common.util.LogUtil;
import com.app.db.sqlite.SqlInfo;
import com.app.db.sqlite.SqlInfoBuilder;
import com.app.db.sqlite.WhereBuilder;
import com.app.db.table.ColumnEntity;
import com.app.db.table.DbModel;
import com.app.db.table.TableEntity;
import com.app.ex.DbException;
import com.app.view.DbManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Haru on 2016/4/19 0019.
 */
public class DbManagerImpl implements DbManager {

    private static HashMap<DaoConfig,DbManagerImpl> daoMap = new HashMap<>();

    private SQLiteDatabase database;
    private DaoConfig daoConfig;
    private boolean allowTransaction;

    private DbManagerImpl(DaoConfig daoConfig){
        if(daoConfig == null){
            throw new IllegalArgumentException("daoConfig may not be null");
        }
        this.database = createDatabase(daoConfig);
        this.daoConfig = daoConfig;
        this.allowTransaction = daoConfig.isAllowTransaction();
    }

    public synchronized static DbManager getInstance(DaoConfig daoConfig){
        if(daoConfig == null){
            daoConfig = new DaoConfig();
        }
        DbManagerImpl dao = daoMap.get(daoConfig);
        if(dao == null){
            dao = new DbManagerImpl(daoConfig);
            daoMap.put(daoConfig,dao);
        }else {
            dao.daoConfig = daoConfig;
        }

        SQLiteDatabase database = dao.database;
        int oldVersion = database.getVersion();
        int newVersion = daoConfig.getDbVersion();
        if(oldVersion != newVersion){
            if(oldVersion != 0){
                DbUpgradeListener upgradeListener = daoConfig.getDbUpgradeListener();
                if(upgradeListener != null){
                    upgradeListener.onUpgrade(dao,oldVersion,newVersion);
                }else {
                    try {
                        dao.dropDb();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setVersion(newVersion);
        }
        return dao;
    }

    @Override
    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return database;
    }

    @Override
    public boolean saveBindingId(Object entity) throws DbException {
        boolean result = false;
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return false;
                TableEntity<?> table = TableEntity.get(this, entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    if (!saveBindingIdWithoutTransaction(table, item)) {
                        throw new DbException("saveBindingId error, transaction will not commit!");
                    }
                }
            } else {
                TableEntity<?> table = TableEntity.get(this, entity.getClass());
                createTableIfNotExist(table);
                result = saveBindingIdWithoutTransaction(table, entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public void saveOrUpdate(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = TableEntity.get(this, entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    saveOrUpdateWithoutTransaction(table, item);
                }
            } else {
                TableEntity<?> table = TableEntity.get(this, entity.getClass());
                createTableIfNotExist(table);
                saveOrUpdateWithoutTransaction(table, entity);
            }

            setTransactionSuccessful();
        }finally {
            endTransaction();
        }
    }

    private void saveOrUpdateWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            if (id.getColumnValue(entity) != null) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, entity));
            } else {
                saveBindingIdWithoutTransaction(table, entity);
            }
        } else {
            execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
        }
    }

    private boolean saveBindingIdWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            long idValue = getLastAutoIncrementId(table.getName());
            if (idValue == -1) {
                return false;
            }
            id.setAutoIdValue(entity, idValue);
            return true;
        } else {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            return true;
        }
    }

    private long getLastAutoIncrementId(String tableName) throws DbException {
        long id = -1;
        Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "' LIMIT 1");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return id;
    }

    @Override
    public void save(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = TableEntity.get(this, entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, item));
                }
            } else {
                TableEntity<?> table = TableEntity.get(this, entity.getClass());
                createTableIfNotExist(table);
                execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void replace(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = TableEntity.get(this, entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, item));
                }
            } else {
                TableEntity<?> table = TableEntity.get(this, entity.getClass());
                createTableIfNotExist(table);
                execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void deleteById(Class<?> entityType, Object idValue) throws DbException {
        TableEntity<?> table = TableEntity.get(this, entityType);
        if (!table.tableIsExist()) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfoById(table, idValue));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void delete(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = TableEntity.get(this, entities.get(0).getClass());
                if (!table.tableIsExist()) return;
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(table, item));
                }
            } else {
                TableEntity<?> table = TableEntity.get(this, entity.getClass());
                if (!table.tableIsExist()) return;
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(table, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void delete(Class<?> entityType) throws DbException {
        delete(entityType, null);
    }

    @Override
    public int delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException {
        TableEntity<?> table = TableEntity.get(this, entityType);
        if (!table.tableIsExist()) return 0;
        int result = 0;
        try {
            beginTransaction();

            result = executeUpdateDelete(SqlInfoBuilder.buildDeleteSqlInfo(table, whereBuilder));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public void update(Object entity, String... updateColumnNames) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = TableEntity.get(this, entities.get(0).getClass());
                if (!table.tableIsExist()) return;
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, item, updateColumnNames));
                }
            } else {
                TableEntity<?> table = TableEntity.get(this, entity.getClass());
                if (!table.tableIsExist()) return;
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, entity, updateColumnNames));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public int update(Class<?> entityType, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        TableEntity<?> table = TableEntity.get(this, entityType);
        if (!table.tableIsExist()) return 0;

        int result = 0;
        try {
            beginTransaction();

            result = executeUpdateDelete(SqlInfoBuilder.buildUpdateSqlInfo(table, whereBuilder, nameValuePairs));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }

        return result;
    }

    @Override
    public <T> T findById(Class<T> entityType, Object idValue) throws DbException {
        TableEntity<T> table = TableEntity.get(this, entityType);
        if (!table.tableIsExist()) return null;

        Selector selector = Selector.from(table).where(table.getId().getName(), "=", idValue);

        String sql = selector.limit(1).toString();
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getEntity(table, cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return null;
    }

    @Override
    public <T> T findFirst(Class<T> entityType) throws DbException {
        return this.selector(entityType).findFirst();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) throws DbException {
        return this.selector(entityType).findAll();
    }

    @Override
    public <T> Selector<T> selector(Class<T> entityType) throws DbException {
        return Selector.from(TableEntity.get(this, entityType));
    }

    @Override
    public DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException {
        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return null;
    }

    @Override
    public List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException {
        List<DbModel> dbModelList = new ArrayList<DbModel>();

        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return dbModelList;
    }

    private SQLiteDatabase createDatabase(DaoConfig config) {
        SQLiteDatabase result = null;

        File dbDir = config.getDbDir();
        if (dbDir != null && (dbDir.exists() || dbDir.mkdirs())) {
            File dbFile = new File(dbDir, config.getDbName());
            result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } else {
            result = Utils.app().openOrCreateDatabase(config.getDbName(), 0, null);
        }
        return result;
    }

    @Override
    public void dropTable(Class<?> entityType) throws DbException {
        TableEntity<?> table = TableEntity.get(this, entityType);
        if (!table.tableIsExist()) return;
        execNonQuery("DROP TABLE \"" + table.getName() + "\"");
        TableEntity.remove(this, entityType);
    }

    @Override
    public void addColumn(Class<?> entityType, String column) throws DbException {
        try {
            beginTransaction();
            TableEntity.remove(this, entityType);
            TableEntity<?> table = TableEntity.get(this, entityType);
            ColumnEntity col = table.getColumnMap().get(column);
            if (col != null) {
                StringBuilder builder = new StringBuilder();
                builder.append("ALTER TABLE ").append("\"").append(table.getName()).append("\"").
                        append(" ADD COLUMN ").append("\"").append(col.getName()).append("\"").
                        append(" ").append(col.getColumnDbType()).
                        append(" ").append(col.getProperty());
                execNonQuery(builder.toString());
            }
            table.setCheckedDatabase(true);
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void dropDb() throws DbException {
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        String tableName = cursor.getString(0);
                        execNonQuery("DROP TABLE " + tableName);
                        TableEntity.remove(this, tableName);
                    } catch (Throwable e) {
                        LogUtil.e(e.getMessage(), e);
                    }
                }

            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (daoMap.containsKey(daoConfig)) {
            daoMap.remove(daoConfig);
            this.database.close();
        }
    }

    @Override
    public int executeUpdateDelete(SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(database);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public int executeUpdateDelete(String sql) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = database.compileStatement(sql);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public void execNonQuery(SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(database);
            statement.execute();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public void execNonQuery(String sql) throws DbException {
        try {
            database.execSQL(sql);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public Cursor execQuery(SqlInfo sqlInfo) throws DbException {
        try {
            return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public Cursor execQuery(String sql) throws DbException {
        try {
            return database.rawQuery(sql, null);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }


    private void createTableIfNotExist(TableEntity<?> table) throws DbException {
        if (!table.tableIsExist()) {
            synchronized (table.getClass()) {
                if (!table.tableIsExist()) {
                    SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(table);
                    execNonQuery(sqlInfo);
                    String execAfterTableCreated = table.getOnCreated();
                    if (!TextUtils.isEmpty(execAfterTableCreated)) {
                        execNonQuery(execAfterTableCreated);
                    }
                    table.setCheckedDatabase(true);
                    TableCreateListener listener = this.daoConfig.getTableCreateListener();
                    if (listener != null) {
                        listener.onTableCreate(this, table);
                    }
                }
            }
        }
    }


    private void beginTransaction() {
        if (allowTransaction) {
            database.beginTransaction();
        }
    }

    private void setTransactionSuccessful() {
        if (allowTransaction) {
            database.setTransactionSuccessful();
        }
    }

    private void endTransaction() {
        if (allowTransaction) {
            database.endTransaction();
        }
    }
}
