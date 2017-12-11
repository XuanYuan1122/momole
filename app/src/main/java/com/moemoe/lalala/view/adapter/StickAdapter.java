package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.StickEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class StickAdapter extends BaseRecyclerViewAdapter<StickEntity.Stick,StickHolder> {

    private int selectPosition;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public StickAdapter() {
        super(R.layout.item_stick);
    }

    @Override
    protected void convert(StickHolder helper, final StickEntity.Stick item, int position) {
        helper.createItem(item,position == selectPosition);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
