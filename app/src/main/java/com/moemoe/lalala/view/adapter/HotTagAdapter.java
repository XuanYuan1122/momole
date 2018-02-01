package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.RecommendTagEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class HotTagAdapter extends BaseRecyclerViewAdapter<RecommendTagEntity,HotTagHolder> {

    public HotTagAdapter() {
        super(R.layout.item_hot_tag);
    }
    
    @Override
    protected void convert(HotTagHolder helper, final RecommendTagEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
