package com.moemoe.lalala.view.adapter;

import android.support.annotation.Nullable;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class UserAdapter extends BaseRecyclerViewAdapter<UserTopEntity,UserHolder> {

    public UserAdapter() {
        super(R.layout.item_new_doc_creator);
    }

    public UserAdapter(@Nullable ArrayList<UserTopEntity> list) {
        super(R.layout.item_new_doc_creator,list);
    }

    @Override
    protected void convert(UserHolder helper, final UserTopEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
