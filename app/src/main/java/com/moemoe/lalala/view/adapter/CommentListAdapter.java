package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class CommentListAdapter extends BaseRecyclerViewAdapter<CommentV2Entity,CommentListHolder> {

    private String mId;
    private boolean showFavorite;

    public CommentListAdapter(String id) {
        super(R.layout.item_new_comment);
        mId = id;
        showFavorite = true;
    }


    @Override
    protected void convert(CommentListHolder helper, final CommentV2Entity item, int position) {
        helper.createItem(item,position,mId,showFavorite);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public void setShowFavorite(boolean showFavorite){
        this.showFavorite = showFavorite;
    }
}
