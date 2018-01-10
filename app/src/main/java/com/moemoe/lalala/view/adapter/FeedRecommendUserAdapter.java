package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class FeedRecommendUserAdapter extends BaseRecyclerViewAdapter<FeedRecommendUserEntity,FeedRecommendUserHolder> {


    public FeedRecommendUserAdapter() {
        super(R.layout.item_feed_recomment_user);
    }


    @Override
    protected void convert(FeedRecommendUserHolder helper, final FeedRecommendUserEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
