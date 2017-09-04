package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class XiaoShuoAdapter extends BaseRecyclerViewAdapter<String,ClickableViewHolder> {

    public XiaoShuoAdapter() {
        super(R.layout.item_xiaoshuo);
    }

    @Override
    protected void convert(ClickableViewHolder helper, final String item, int position) {
        helper.setText(R.id.tv_txt,item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
