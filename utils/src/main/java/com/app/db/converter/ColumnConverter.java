package com.app.db.converter;

import android.database.Cursor;

import com.app.db.sqlite.ColumnDbType;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public interface ColumnConverter<T> {
    T getFieldValue(final Cursor cursor, int index);

    Object fieldValue2DbValue(T fieldValue);

    ColumnDbType getColumnDbType();
}
