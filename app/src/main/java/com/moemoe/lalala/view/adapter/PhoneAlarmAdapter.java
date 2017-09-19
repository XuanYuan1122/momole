package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneAlarmAdapter extends BaseRecyclerViewAdapter<AlarmClockEntity,PhoneAlarmHolder> {


    public PhoneAlarmAdapter() {
        super(R.layout.item_phone_alarm);
    }


    @Override
    protected void convert(PhoneAlarmHolder helper, final AlarmClockEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
