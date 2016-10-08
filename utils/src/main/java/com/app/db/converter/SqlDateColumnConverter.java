package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;

import java.sql.Date;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class SqlDateColumnConverter implements  ColumnConverter<Date> {
    @Override
    public java.sql.Date getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new java.sql.Date(cursor.getLong(index));
    }

    @Override
    public Object fieldValue2DbValue(java.sql.Date fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.getTime();
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
