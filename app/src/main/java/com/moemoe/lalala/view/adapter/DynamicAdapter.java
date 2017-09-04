package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DynamicEntity;
import com.moemoe.lalala.model.entity.DynamicTopEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class DynamicAdapter extends BaseRecyclerViewAdapter<DynamicEntity,DynamicViewHolder> {

    public DynamicAdapter() {
        super(R.layout.item_dynamic);
    }


    @Override
    protected void convert(DynamicViewHolder helper, final DynamicEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
