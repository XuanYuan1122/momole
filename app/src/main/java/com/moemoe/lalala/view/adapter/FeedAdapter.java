package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedAdapter extends BaseRecyclerViewAdapter<NewDynamicEntity,FeedHolder> {


    public FeedAdapter() {
        super(R.layout.item_new_feed_list);
    }


    @Override
    protected void convert(FeedHolder helper, final NewDynamicEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
