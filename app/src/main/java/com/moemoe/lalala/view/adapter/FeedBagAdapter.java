package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedBagAdapter extends BaseRecyclerViewAdapter<ShowFolderEntity,FeedBagHolder> {


    public FeedBagAdapter() {
        super(R.layout.item_feed_bag);
    }


    @Override
    protected void convert(FeedBagHolder helper, final ShowFolderEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
