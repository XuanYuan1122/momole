package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.RecommendTagEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class RecommendTagAdapter extends BaseRecyclerViewAdapter<RecommendTagEntity,RecommendTagHolder> {


    public RecommendTagAdapter() {
        super(R.layout.item_recommend_tag);
    }


    @Override
    protected void convert(RecommendTagHolder helper, final RecommendTagEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
