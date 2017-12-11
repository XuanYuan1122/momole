package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneGroupMemberListAdapter extends BaseRecyclerViewAdapter<UserTopEntity,PhoneGroupMemberListHolder> {

    private boolean isCheck;
    private String id;

    public PhoneGroupMemberListAdapter(String id) {
        super(R.layout.item_phone_menu);
        this.id = id;
    }


    @Override
    protected void convert(PhoneGroupMemberListHolder helper, final UserTopEntity item, int position) {
        helper.createItem(item,isCheck,id);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
