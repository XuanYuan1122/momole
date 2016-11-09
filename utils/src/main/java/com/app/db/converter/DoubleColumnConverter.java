package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class DoubleColumnConverter implements ColumnConverter<Double> {
    @Override
    public Double getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    @Override
    public Object fieldValue2DbValue(Double fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.REAL;
    }
}
