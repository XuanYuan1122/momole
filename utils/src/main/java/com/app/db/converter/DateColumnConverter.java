package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;


import java.util.Date;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class DateColumnConverter implements ColumnConverter<Date> {
    @Override
    public java.util.Date getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new java.util.Date(cursor.getLong(index));
    }

    @Override
    public Object fieldValue2DbValue(java.util.Date fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.getTime();
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
