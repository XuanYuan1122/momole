package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedNoticeAdapter extends BaseRecyclerViewAdapter<FeedNoticeEntity,FeedNoticeViewHolder> {

    public FeedNoticeAdapter() {
        super(R.layout.item_feed_type_1);
    }


    @Override
    protected void convert(FeedNoticeViewHolder helper, final FeedNoticeEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
