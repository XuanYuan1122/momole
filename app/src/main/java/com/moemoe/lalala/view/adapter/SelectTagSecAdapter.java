package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class SelectTagSecAdapter extends BaseRecyclerViewAdapter<OfficialTag.OfficialTagSec,SelectTagSecHolder> {

    private ArrayList<UserFollowTagEntity> userIds;

    public SelectTagSecAdapter() {
        super(R.layout.item_tag_sec);
    }
    
    @Override
    protected void convert(SelectTagSecHolder helper, final OfficialTag.OfficialTagSec item, int position) {
        helper.createItem(item,position,userIds);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public ArrayList<UserFollowTagEntity> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<UserFollowTagEntity> userIds) {
        this.userIds = userIds;
    }
}
