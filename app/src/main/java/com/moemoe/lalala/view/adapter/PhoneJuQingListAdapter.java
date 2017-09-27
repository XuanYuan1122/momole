package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.JuQingEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneJuQingListAdapter extends BaseRecyclerViewAdapter<JuQingEntity,PhoneJuQingListHolder> {


    public PhoneJuQingListAdapter() {
        super(R.layout.item_juqing);
    }


    @Override
    protected void convert(PhoneJuQingListHolder helper, final JuQingEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
