package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class IntegerColumnConverter implements ColumnConverter<Integer> {
    @Override
    public Integer getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index);
    }

    @Override
    public Object fieldValue2DbValue(Integer fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
