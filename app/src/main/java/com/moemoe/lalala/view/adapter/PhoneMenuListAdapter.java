package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneMenuListAdapter extends BaseRecyclerViewAdapter<PhoneMenuEntity,PhoneMenuListHolder> {


    public PhoneMenuListAdapter() {
        super(R.layout.item_phone_menu);
    }


    @Override
    protected void convert(PhoneMenuListHolder helper, final PhoneMenuEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
