package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class Comment24Adapter extends BaseRecyclerViewAdapter<Comment24Entity,Comment24Holder> {

    public Comment24Adapter() {
        super(R.layout.item_24_comment);
    }


    @Override
    protected void convert(Comment24Holder helper, final Comment24Entity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
