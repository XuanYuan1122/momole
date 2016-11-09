package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class StringColumnConverter implements  ColumnConverter<String> {
    @Override
    public String getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    @Override
    public Object fieldValue2DbValue(String fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.TEXT;
    }
}
