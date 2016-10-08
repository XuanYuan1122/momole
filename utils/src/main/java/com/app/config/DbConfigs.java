package com.app.config;

import com.app.common.util.LogUtil;
import com.app.ex.DbException;
import com.app.view.DbManager;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public enum DbConfigs {
    HTTP(new DbManager.DaoConfig()
            .setDbName("utils_http_cache.db")
            .setDbVersion(1)
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    try {
                        db.dropDb(); // 默认删除所有表
                    } catch (DbException ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            })),

    COOKIE(new DbManager.DaoConfig()
            .setDbName("utils_http_cookie.db")
            .setDbVersion(1)
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    try {
                        db.dropDb(); // 默认删除所有表
                    } catch (DbException ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }));

    private DbManager.DaoConfig config;

    DbConfigs(DbManager.DaoConfig config) {
        this.config = config;
    }

    public DbManager.DaoConfig getConfig() {
        return config;
    }
}
