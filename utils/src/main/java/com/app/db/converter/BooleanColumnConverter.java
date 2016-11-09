package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class BooleanColumnConverter implements ColumnConverter<Boolean> {
    @Override
    public Boolean getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index) == 1;
    }

    @Override
    public Object fieldValue2DbValue(Boolean fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue ? 1 : 0;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
