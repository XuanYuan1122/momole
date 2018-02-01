package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BagMyEntity;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class BagCollectionTopAdapter extends BaseRecyclerViewAdapter<ShowFolderEntity,BagCollectionTopHolder> {

    private boolean isSelect;

    public BagCollectionTopAdapter() {
        super(R.layout.item_feed_type_4_v3);
        isSelect = false;
    }
    
    @Override
    protected void convert(BagCollectionTopHolder helper, final ShowFolderEntity item, int position) {
        helper.createItem(item,position,isSelect);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
