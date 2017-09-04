package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.WenZhangFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class BagWenZhangAdapter extends BaseRecyclerViewAdapter<WenZhangFolderEntity,BagWenZhangHolder> {

    private boolean isSelect;

    public BagWenZhangAdapter() {
        super(R.layout.item_folder_wenzhang);
        isSelect = false;
    }


    @Override
    protected void convert(BagWenZhangHolder helper, final WenZhangFolderEntity item, int position) {
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
