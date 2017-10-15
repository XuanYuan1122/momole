package com.moemoe.lalala.view.adapter;

import android.view.ViewGroup;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneLuYinListAdapter extends BaseRecyclerViewAdapter<LuYinEntity,PhoneLuYinListHolder> {

    private int playingPosition;
    private String type;

    public PhoneLuYinListAdapter(String type) {
        super(R.layout.item_luyin);
        playingPosition = -1;
        this.type = type;
    }

    @Override
    protected PhoneLuYinListHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return new PhoneLuYinListHolder(getItemView(R.layout.item_luyin, parent),this);
    }

    @Override
    protected void convert(PhoneLuYinListHolder helper, final LuYinEntity item, int position) {
        helper.createItem(item,position,playingPosition,type);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public int getPlayingPosition() {
        return playingPosition;
    }

    public void setPlayingPosition(int playingPosition) {
        this.playingPosition = playingPosition;
    }
}
