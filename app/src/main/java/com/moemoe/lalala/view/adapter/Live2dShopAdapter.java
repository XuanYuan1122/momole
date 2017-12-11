package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.Live2dShopEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class Live2dShopAdapter extends BaseRecyclerViewAdapter<Live2dShopEntity,Live2dShopHolder> {

    public Live2dShopAdapter() {
        super(R.layout.item_live2d_shop);
    }


    @Override
    protected void convert(Live2dShopHolder helper, final Live2dShopEntity item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
