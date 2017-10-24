package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class OldDocAdapter extends BaseRecyclerViewAdapter<DocResponse,OldDocHolder> {


    public OldDocAdapter() {
        super(R.layout.item_new_feed_list);
    }


    @Override
    protected void convert(OldDocHolder helper, final DocResponse item, int position) {
        helper.createItem(item);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
