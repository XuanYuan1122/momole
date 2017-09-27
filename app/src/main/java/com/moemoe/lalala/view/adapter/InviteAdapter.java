package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class InviteAdapter extends BaseRecyclerViewAdapter<InviteUserEntity,InviteHolder> {

    public InviteAdapter() {
        super(R.layout.item_invite);
    }

    @Override
    protected void convert(InviteHolder helper, final InviteUserEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
