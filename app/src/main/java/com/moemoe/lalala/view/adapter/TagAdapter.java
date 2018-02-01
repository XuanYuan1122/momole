package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class TagAdapter extends BaseRecyclerViewAdapter<UserFollowTagEntity,TagHolder> {

    private ArrayList<UserFollowTagEntity> userIds;

    private boolean showClose;

    public TagAdapter() {
        super(R.layout.item_tags);
        showClose = true;
    }

    public TagAdapter(ArrayList<UserFollowTagEntity> list){
        super(list);
    }
    
    @Override
    protected void convert(TagHolder helper, final UserFollowTagEntity item, int position) {
        helper.createItem(item,position,userIds,showClose);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public boolean isShowClose() {
        return showClose;
    }

    public void setShowClose(boolean showClose) {
        this.showClose = showClose;
    }

    public ArrayList<UserFollowTagEntity> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<UserFollowTagEntity> userIds) {
        this.userIds = userIds;
    }
}
