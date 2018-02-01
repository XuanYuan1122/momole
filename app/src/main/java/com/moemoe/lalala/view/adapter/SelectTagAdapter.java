package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class SelectTagAdapter extends BaseRecyclerViewAdapter<OfficialTag,SelectTagHolder> {

    private int selectPosition;

    public SelectTagAdapter() {
        super(R.layout.item_classify_tag);
        selectPosition = -1;
    }
    
    @Override
    protected void convert(SelectTagHolder helper, final OfficialTag item, int position) {
        helper.createItem(item,position,selectPosition == position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public int getSelectPosition() {
        return selectPosition;
    }
}
