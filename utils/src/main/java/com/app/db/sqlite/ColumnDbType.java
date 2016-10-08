package com.app.db.sqlite;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public enum ColumnDbType {
    INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT"), BLOB("BLOB");

    private String value;

    ColumnDbType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
