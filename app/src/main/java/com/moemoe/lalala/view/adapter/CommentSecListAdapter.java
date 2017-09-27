package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class CommentSecListAdapter extends BaseRecyclerViewAdapter<CommentV2SecEntity,CommentSecListHolder> {


    public CommentSecListAdapter() {
        super(R.layout.item_new_comment);
    }


    @Override
    protected void convert(CommentSecListHolder helper, final CommentV2SecEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
