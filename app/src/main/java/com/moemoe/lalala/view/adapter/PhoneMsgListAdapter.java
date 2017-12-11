package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.GroupNoticeEntity;
import com.moemoe.lalala.view.fragment.PhoneMsgListV2Fragment;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneMsgListAdapter extends BaseRecyclerViewAdapter<GroupNoticeEntity,PhoneMsgListHolder> {

    private int showPosition = -1;
    private PhoneMsgListV2Fragment fragment;

    public PhoneMsgListAdapter(PhoneMsgListV2Fragment fragment) {
        super(R.layout.item_msg);
        this.fragment = fragment;
    }

    @Override
    protected void convert(PhoneMsgListHolder helper, final GroupNoticeEntity item, int position) {
        helper.createItem(item,position == showPosition,position,fragment);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public int getShowPosition() {
        return showPosition;
    }

    public void setShowPosition(int showPosition) {
        this.showPosition = showPosition;
    }
}
