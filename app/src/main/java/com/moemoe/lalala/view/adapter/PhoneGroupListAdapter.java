package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.GroupEntity;
import com.moemoe.lalala.model.entity.GroupNoticeEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneGroupListAdapter extends BaseRecyclerViewAdapter<GroupEntity,PhoneGroupListHolder> {
    public PhoneGroupListAdapter() {
        super(R.layout.item_msg);
    }

    @Override
    protected void convert(PhoneGroupListHolder helper, final GroupEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
