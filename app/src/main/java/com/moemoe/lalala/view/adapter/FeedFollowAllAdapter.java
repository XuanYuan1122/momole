package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.view.widget.adapter.BaseMultiItemRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedFollowAllAdapter extends BaseMultiItemRecyclerViewAdapter<FeedFollowType1Entity,FeedFollowAllHolder> {

    public FeedFollowAllAdapter() {
        super(null);
        addItemType(0,R.layout.item_empty);
        addItemType(1,R.layout.item_feed_type_1_v3);
        addItemType(2,R.layout.item_feed_type_2_v3);
    }

    @Override
    protected void convert(FeedFollowAllHolder helper, final FeedFollowType1Entity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        FeedFollowType1Entity item = getItem(position);
        switch (item.getType()){
            case "WZ":
                return 1;
            case "MOVIE":
            case "MUSIC":
                return 2;
            default:
                return 2;
        }
    }

}
