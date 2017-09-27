package com.moemoe.lalala.view.adapter;

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

    public PhoneLuYinListAdapter() {
        super(R.layout.item_luyin);
        playingPosition = -1;
    }


    @Override
    protected void convert(PhoneLuYinListHolder helper, final LuYinEntity item, int position) {
        helper.createItem(item,position,playingPosition);
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
