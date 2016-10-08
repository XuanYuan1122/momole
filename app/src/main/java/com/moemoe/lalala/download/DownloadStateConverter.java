package com.moemoe.lalala.download;

import android.database.Cursor;

import com.app.db.converter.ColumnConverter;
import com.app.db.sqlite.ColumnDbType;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class DownloadStateConverter implements ColumnConverter<DownloadState> {
    @Override
    public DownloadState getFieldValue(Cursor cursor, int index) {
        int dbValue = cursor.getInt(index);
        return DownloadState.valueOf(dbValue);
    }

    @Override
    public Object fieldValue2DbValue(DownloadState fieldValue) {
        return fieldValue.value();
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
